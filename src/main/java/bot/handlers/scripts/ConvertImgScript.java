package bot.handlers.scripts;

import bot.fileloadingmanagers.ConversionInfo;
import bot.fileloadingmanagers.FileLoadingManager;
import bot.fileloadingmanagers.TelegramFileLoadingManager;
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
    private final FileLoadingManager<ConversionInfo, String> loadingManager = new TelegramFileLoadingManager();
    private final Map<String, ChatState> chatStates = new HashMap<>();

    public ConvertImgScript(TelegramLongPollingBot bot) {
        super(bot);
    }

    @Override
    public void start(String chatId) {
        chatStates.putIfAbsent(chatId, new ChatState());
        chatStates.get(chatId).setStage(ChatState.Stage.CHOOSING_CONVERSION);
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
                ChatState state = chatStates.get(chatId);
                if (state.getStage() == ChatState.Stage.CHOOSING_CONVERSION) {
                    Conversion chosenConversion = Conversion.parse(callbackQuery.getData());
                    sendTextReply(chatId, "Load your " + chosenConversion.getFrom().toString() + " files",
                            ReplyKeyboards.getButtonWithText(STOP_WORD));
                    state.setChosenConversion(chosenConversion);
                    state.setStage(ChatState.Stage.LOADING_FILE);
                }
            }
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            if (chatStates.containsKey(chatId)) {
                ChatState state = chatStates.get(chatId);
                Conversion conversion = state.getChosenConversion();
                if (state.getStage() == ChatState.Stage.LOADING_FILE) {

                    if (message.hasText()) {
                        if (STOP_WORD.equals(message.getText())) {
                            if (state.getImageBuffer().size() == 0) {
                                sendTextReply(chatId, "You have to load at least one photo",
                                        new ReplyKeyboardRemove(true));
                            } else {
                                List<File> downloadedImages = downloadImages(state.getImageBuffer());
                                File convertedImages = convertImages(downloadedImages, conversion);
                                Document uploadedDocument = sendConvertedDocument(chatId, convertedImages);
                                //adding to file manager...
                            }
                            stop(chatId);
                        }

                    } else if (message.hasDocument()) {
                        Document document = message.getDocument();
                        String extension = FileNameTools.extractExtension(document.getFileName()).toLowerCase();
                        if (extension.equals(conversion.getFrom().name().toLowerCase())) {
                            if (!state.addDocument(document)) {
                                sendTextReply(chatId,
                                        String.format("You have already loaded maximum number of photos (%d)",
                                                state.getImageBuffer().getCapacity()));
                            }
                        } else {
                            sendTextReply(chatId, "Wrong file extension");
                            stop(chatId);
                        }

                    } else if (message.hasPhoto()) {
                        List<PhotoSize> photos = message.getPhoto();
                        for (PhotoSize ps : photos.subList(1, photos.size())) {
                            Document img = photoSizeToDocument(ps);
                            if (!state.addDocument(img)) {
                                sendTextReply(chatId,
                                        String.format("You have already loaded maximum number of photos (%d)",
                                                state.getImageBuffer().getCapacity()));
                            }
                        }

                    } else {
                        sendTextReply(chatId, "Document or photo required");
                        stop(chatId);
                    }
                }
            }
        }
    }

    private String combineIds(List<Document> photos) {
        StringBuilder result = new StringBuilder();
        photos.forEach(p -> result.append(p.getFileUniqueId()).append(";"));
        return result.toString();
    }

    private Document sendConvertedDocument(String chatId, File file) {
        String filename = file.getName();
        Document uploadedDocument = sendDocumentReply(chatId, file,
                "images" + filename.substring(filename.lastIndexOf('.')),
                new ReplyKeyboardRemove(true));
        // file.delete();
        return uploadedDocument;
    }

    private File convertImages(List<File> inputFiles, Conversion conversion) {
        ImgConverter converter = factory.getConverter(conversion);
        File outputFile = converter.convert(inputFiles);
        //adding file to loading manager
        inputFiles.forEach(File::delete);
        return outputFile;
    }

    private List<File> downloadImages(Iterable<Document> images) {
        DataDownloader downloader = new DataDownloader(bot);
        List<File> result = new LinkedList<>();
        for (Document img : images) {
            result.add(downloader.downloadDocument(img));
        }
        return result;
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
            ChatState state = chatStates.get(chatId);
            state.setStage(ChatState.Stage.COMPLETED);
            state.reset();
        }
    }


    private static class ChatState {
        private enum Stage {CHOOSING_CONVERSION, LOADING_FILE, COMPLETED}

        private Stage stage;
        private Conversion chosenConversion;
        private final ImageBuffer imageBuffer = new ImageBuffer();

        public Stage getStage() {
            return stage;
        }

        public void setStage(Stage stage) {
            this.stage = stage;
        }

        public Conversion getChosenConversion() {
            return chosenConversion;
        }

        public void setChosenConversion(Conversion chosenConversion) {
            this.chosenConversion = chosenConversion;
        }

        boolean addDocument(Document document) {
            return imageBuffer.add(document);
        }

        public ImageBuffer getImageBuffer() {
            return imageBuffer;
        }

        void reset() {
            chosenConversion = null;
            imageBuffer.clear();
        }
    }
}
