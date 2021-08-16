package bot.handlers.scripts;

import bot.chatstates.ChatStates;
import bot.fileloadingmanagers.ConversionInfo;
import bot.fileloadingmanagers.FileLoadingManager;
import bot.fileloadingmanagers.TelegramFileLoadingManager;
import bot.handlers.scripts.helperclasses.DataDownloader;
import bot.handlers.scripts.helperclasses.FixedSizeList;
import bot.handlers.scripts.helperclasses.ReplyKeyboards;
import bot.handlers.scripts.helperclasses.ScriptStage;
import convertations.conversions.AvailableConversions;
import convertations.conversions.Conversion;
import convertations.converters.imgconverters.ImgConverter;
import convertations.converters.imgconverters.factory.AbstractImgConverterFactory;
import convertations.converters.imgconverters.factory.ImgConverterFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import tools.files.FileNameTools;

import java.io.File;
import java.util.LinkedList;
import java.util.List;


public class ConvertImgScript extends AbstractScript {
    private final String chatId;
    private final AbstractImgConverterFactory factory = new ImgConverterFactory();
    private final FileLoadingManager<ConversionInfo, String> loadingManager = TelegramFileLoadingManager.getInstance();
    private final ScriptStage stage = new ScriptStage();
    private Conversion chosenConversion;
    private final FixedSizeList<Document> images = new FixedSizeList<>(10);

    public ConvertImgScript(TelegramLongPollingBot bot, String chatId) {
        super(bot);
        this.chatId = chatId;
    }

    @Override
    public void start() {
        LOG.debug("[{}] Image converting script has been started.", chatId);
        stage.setChoosingConversion();
        sendTextReply(chatId, "What type of conversion do you want to do?",
                ReplyKeyboards.availableConversions(AvailableConversions.getImgConversions()));
    }

    @Override
    public void update(Update update) {
        final String STOP_WORD = "Done";
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if (stage.isChoosingConversion()) {
                Conversion chosenConversion = Conversion.parse(callbackQuery.getData());
                if (AvailableConversions.getImgConversions().contains(chosenConversion)) {
                    this.chosenConversion = chosenConversion;
                    stage.setLoadingFile();
                    LOG.debug("[{}] {} conversion has been chosen in image converting script.", chatId, chosenConversion);
                    sendTextReply(chatId, "Load your " + chosenConversion.getFrom().toString() + " files",
                            ReplyKeyboards.getButtonWithText(STOP_WORD));
                    answerCallbackQuery(callbackQuery);
                }
            }
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            if (stage.isLoadingFile()) {
                if (message.hasText()) {
                    if (STOP_WORD.equals(message.getText())) {
                        if (images.isEmpty()) {
                            sendTextReply(chatId, "You have to load at least one photo", new ReplyKeyboardRemove(true));
                            LOG.debug("[{}] No photo has been loaded.", chatId);
                        } else {
                            convertAndUploadImages();
                        }
                        stop();
                    }
                } else if (message.hasDocument()) {
                    Document document = message.getDocument();
                    if (isSameExtensions(document, chosenConversion)) {
                        if (!addDocumentWithFailMessage(document, chatId)) {
                            convertAndUploadImages();
                            stop();
                        }
                    } else {
                        sendTextReply(chatId, "Wrong file extension", new ReplyKeyboardRemove(true));
                        LOG.debug("[{}] Document {} has wrong extension ({} expected).",
                                chatId, document.getFileName(), chosenConversion.getFrom());
                        stop();
                    }
                } else if (message.hasPhoto()) {
                    List<PhotoSize> photos = message.getPhoto();
                    for (PhotoSize ps : photos.subList(1, photos.size())) {
                        if (!addDocumentWithFailMessage(photoSizeToDocument(ps), chatId)) {
                            convertAndUploadImages();
                            stop();
                        }
                    }
                    LOG.debug("[{}] Compressed photos has been downloaded.", chatId);
                } else {
                    sendTextReply(chatId, "Document or photo required", new ReplyKeyboardRemove(true));
                    LOG.debug("[{}] Message contains neither document nor photos.", chatId);
                    stop();
                }
            }
        }
    }

    @Override
    public void stop() {
        ChatStates.getInstance().put(chatId, null);
        LOG.debug("[{}] Image converting script has been stopped.", chatId);
    }

    private void convertAndUploadImages() {
        ConversionInfo info = new ConversionInfo(combineIds(images), chosenConversion);
        if (!(loadingManager.contains(info) && sendDocumentReply(chatId, loadingManager.get(info)) != null)) {
            Document uploadedDocument = sendDocument(chatId, convertImages(downloadImages(images), chosenConversion));
            if (uploadedDocument != null) {
                saveDocumentInLoadingManager(uploadedDocument, info);
                LOG.debug("[{}] Document {} has been saved in loading manager. {}. FileId {}.",
                        chatId, uploadedDocument.getFileUniqueId(), info, uploadedDocument.getFileId());
            }
        }
    }

    private String combineIds(Iterable<Document> images) {
        StringBuilder result = new StringBuilder();
        images.forEach(p -> result.append(p.getFileUniqueId()).append(";"));
        return result.toString();
    }

    private List<File> downloadImages(Iterable<Document> images) {
        DataDownloader downloader = new DataDownloader(bot);
        List<File> result = new LinkedList<>();
        for (Document img : images) {
            result.add(downloader.downloadDocument(img));
        }
        return result;
    }

    private File convertImages(List<File> inputFiles, Conversion conversion) {
        ImgConverter converter = factory.getConverter(conversion);
        File outputFile = converter.convert(inputFiles);
        inputFiles.forEach(File::delete);
        return outputFile;
    }

    private Document sendDocument(String chatId, File file) {
        String filename = file.getName();
        Document uploadedDocument = sendDocumentReply(chatId, file,
                "images" + filename.substring(filename.lastIndexOf('.')),
                new ReplyKeyboardRemove(true));
        file.delete();
        return uploadedDocument;
    }

    private boolean isSameExtensions(Document document, Conversion conversion) {
        String extension = FileNameTools.extractExtension(document.getFileName()).toLowerCase();
        return extension.equals(conversion.getFrom().name().toLowerCase());
    }

    private void saveDocumentInLoadingManager(Document document, ConversionInfo conversionInfo) {
        loadingManager.put(conversionInfo, document.getFileId());
    }

    private boolean addDocumentWithFailMessage(Document document, String chatId) {
        if (!images.add(document)) {
            int capacity = images.getCapacity();
            sendTextReply(chatId, String.format("You have already loaded maximum number of photos (%d)", capacity));
            LOG.debug("[{}] Maximum number of photos ({}) has already been loaded.", chatId, capacity);
            return false;
        }
        return true;
    }

    private Document photoSizeToDocument(PhotoSize photoSize) {
        Document doc = new Document();
        doc.setFileId(photoSize.getFileId());
        doc.setFileUniqueId(photoSize.getFileUniqueId());
        doc.setThumb(photoSize);
        doc.setFileName(String.format("IMG%s.jpg", photoSize.getFileUniqueId()));
        doc.setMimeType("image/jpeg");
        doc.setFileSize(photoSize.getFileSize());
        return doc;
    }
}
