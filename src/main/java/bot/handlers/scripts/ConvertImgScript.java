package bot.handlers.scripts;

import bot.fileloadingmanagers.ConversionInfo;
import bot.fileloadingmanagers.FileLoadingManager;
import bot.fileloadingmanagers.TelegramFileLoadingManager;
import bot.handlers.scripts.helperclasses.DataDownloader;
import bot.handlers.scripts.helperclasses.ImageBuffer;
import bot.handlers.scripts.helperclasses.ReplyKeyboards;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ConvertImgScript extends AbstractScript {
    private final AbstractImgConverterFactory factory = new ImgConverterFactory();
    private final FileLoadingManager<ConversionInfo, String> loadingManager = TelegramFileLoadingManager.getInstance();
    private final Map<String, ChatStateImg> chatStates = new HashMap<>();

    public ConvertImgScript(TelegramLongPollingBot bot) {
        super(bot);
    }

    @Override
    public void start(String chatId) {
        LOG.debug("[{}] Image converting script has been started.", chatId);
        chatStates.putIfAbsent(chatId, new ChatStateImg());
        chatStates.get(chatId).setChoosingConversion();
        sendTextReply(chatId, "What type of conversion do you want to do?",
                ReplyKeyboards.availableConversions(AvailableConversions.getImgConversions()));
    }

    @Override
    public void update(Update update) {
        final String STOP_WORD = "Done";

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String chatId = callbackQuery.getMessage().getChatId().toString();
            answerCallbackQuery(callbackQuery);
            if (chatStates.containsKey(chatId)) {
                ChatStateImg state = chatStates.get(chatId);
                if (state.isChoosingConversion()) {
                    Conversion chosenConversion = Conversion.parse(callbackQuery.getData());
                    LOG.debug("[{}] {} conversion has been chosen in image converting script.", chatId, chosenConversion);
                    sendTextReply(chatId, "Load your " + chosenConversion.getFrom().toString() + " files",
                            ReplyKeyboards.getButtonWithText(STOP_WORD));
                    state.setConversion(chosenConversion);
                    state.setLoadingFile();
                }
            }
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            if (chatStates.containsKey(chatId)) {
                ChatStateImg state = chatStates.get(chatId);
                Conversion conversion = state.getConversion();
                if (state.isLoadingFile()) {
                    if (message.hasText()) {
                        if (STOP_WORD.equals(message.getText())) {
                            if (state.getImageBuffer().size() == 0) {
                                sendTextReply(chatId, "You have to load at least one photo", new ReplyKeyboardRemove(true));
                                LOG.debug("[{}] No photo has been loaded.", chatId);
                            } else {
                                ConversionInfo info = new ConversionInfo(combineIds(state.getImageBuffer()), conversion);
                                if (!(loadingManager.contains(info) && sendDocumentReply(chatId, loadingManager.get(info)) != null)) {
                                    Document uploadedDocument = sendDocument(chatId, convertImages(downloadImages(state.getImageBuffer()), conversion));
                                    if (uploadedDocument != null) {
                                        saveDocumentInLoadingManager(uploadedDocument, info);
                                        LOG.debug("[{}] Document {} has been saved in loading manager. {}. FileId {}.",
                                                chatId, uploadedDocument.getFileUniqueId(), info, uploadedDocument.getFileId());
                                    }
                                }
                            }
                            stop(chatId);
                        }

                    } else if (message.hasDocument()) {
                        Document document = message.getDocument();
                        if (isSameExtensions(document, conversion)) {
                            addDocumentWithFailMessage(state, document, chatId);
                        } else {
                            sendTextReply(chatId, "Wrong file extension", new ReplyKeyboardRemove(true));
                            LOG.debug("[{}] Document {} has wrong extension ({} expected).",
                                    chatId, document.getFileName(), conversion.getFrom());
                            stop(chatId);
                        }

                    } else if (message.hasPhoto()) {
                        List<PhotoSize> photos = message.getPhoto();
                        for (PhotoSize ps : photos.subList(1, photos.size())) {
                            addDocumentWithFailMessage(state, photoSizeToDocument(ps), chatId);
                        }
                        LOG.debug("[{}] Compressed photos has been downloaded.", chatId);

                    } else {
                        sendTextReply(chatId, "Document or photo required", new ReplyKeyboardRemove(true));
                        LOG.debug("[{}] Message contains neither document nor photos.", chatId);
                        stop(chatId);
                    }
                }
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

    private void addDocumentWithFailMessage(ChatStateImg state, Document document, String chatId) {
        if (!state.addDocument(document)) {
            sendTextReply(chatId, String.format("You have already loaded maximum number of photos (%d)",
                    state.getImageBuffer().getCapacity()));
        }
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

    @Override
    public void stop(String chatId) {
        if (chatStates.containsKey(chatId)) {
            ChatStateImg state = chatStates.get(chatId);
            state.setCompleted();
            state.reset();
            LOG.debug("[{}] Image converting script has been stopped.", chatId);
        }
    }

    static class ChatStateImg extends ConvertDocScript.ChatStateDoc {

        private final ImageBuffer imageBuffer = new ImageBuffer();

        public boolean addDocument(Document document) {
            return imageBuffer.add(document);
        }

        public ImageBuffer getImageBuffer() {
            return imageBuffer;
        }

        public void reset() {
            super.reset();
            imageBuffer.clear();
        }
    }
}
