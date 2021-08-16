package bot.handlers.scripts;

import bot.chatstates.ChatStates;
import bot.fileloadingmanagers.ConversionInfo;
import bot.fileloadingmanagers.FileLoadingManager;
import bot.fileloadingmanagers.TelegramFileLoadingManager;
import bot.handlers.scripts.helperclasses.DataDownloader;
import convertations.conversions.AvailableConversions;
import convertations.conversions.Conversion;
import convertations.converters.docconverters.DocConverter;
import convertations.converters.docconverters.factory.AbstractDocConverterFactory;
import convertations.converters.docconverters.factory.DocConverterFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tools.files.FileNameTools;

import java.io.File;

import static bot.handlers.scripts.helperclasses.ReplyKeyboards.availableConversions;

public class ConvertDocScript extends AbstractScript {

    private final String chatId;
    private final AbstractDocConverterFactory factory = new DocConverterFactory();
    private final FileLoadingManager<ConversionInfo, String> loadingManager = TelegramFileLoadingManager.getInstance();
    private final ScriptStateDoc state = new ScriptStateDoc();

    public ConvertDocScript(TelegramLongPollingBot bot, String chatId) {
        super(bot);
        this.chatId = chatId;
    }

    @Override
    public void start() {
        LOG.debug("[{}] Document converting script has started.", chatId);
        state.setChoosingConversion();
        sendTextReply(chatId, "What type of conversion do you want to do?",
                availableConversions(AvailableConversions.getDocConversions()));
    }

    @Override
    public void update(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            answerCallbackQuery(callbackQuery);
            if (state.isChoosingConversion()) {
                Conversion chosenConversion = Conversion.parse(callbackQuery.getData());
                LOG.debug("[{}] {} conversion has been chosen in document converting script.", chatId, chosenConversion);
                sendTextReply(chatId, "Load your " + chosenConversion.getFrom().toString() + " file");
                state.setConversion(chosenConversion);
                state.setLoadingFile();
            }
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            if (state.isLoadingFile()) {
                Conversion conversion = state.getConversion();
                if (message.hasDocument()) {
                    Document document = message.getDocument();
                    if (isSameExtensions(document, conversion)) {
                        ConversionInfo info = new ConversionInfo(document.getFileUniqueId(), conversion);
                        if (!(loadingManager.contains(info) && sendDocumentReply(chatId, loadingManager.get(info)) != null)) {  // trying to send document via fileId
                            Document uploadedDocument = sendFile(chatId,
                                    convertFile(downloadDocument(document), conversion),
                                    composeNewFilename(document, conversion));
                            if (uploadedDocument != null) {
                                saveInLoadingManager(uploadedDocument, info);
                                LOG.debug("[{}] Document {} has been saved in loading manager. {}. FileId {}.",
                                        chatId, uploadedDocument.getFileUniqueId(), info, uploadedDocument.getFileId());
                            }
                        }
                    } else {
                        sendTextReply(chatId, "Wrong document extension");
                        LOG.debug("[{}] Document {} has wrong extension ({} expected).",
                                chatId, document.getFileName(), conversion.getFrom());
                    }
                } else {
                    sendTextReply(chatId, "Document required");
                    LOG.debug("[{}] Message does not contain document (conversion {}).", chatId, conversion);
                }
                stop();
            }
        }
    }

    @Override
    public void stop() {
        ChatStates.getInstance().put(chatId, null);
        LOG.debug("[{}] Document converting script has been stopped.", chatId);
    }

    private boolean isSameExtensions(Document document, Conversion conversion) {
        String extension = FileNameTools.extractExtension(document.getFileName()).toLowerCase();
        return extension.equals(conversion.getFrom().name().toLowerCase());
    }

    private File downloadDocument(Document document) {
        return new DataDownloader(bot).downloadDocument(document);
    }

    private File convertFile(File file, Conversion conversion) {
        DocConverter converter = factory.getConverter(conversion);
        File result = converter.convert(file);
        file.delete();
        return result;
    }

    private Document sendFile(String chatId, File file, String newFilename) {
        Document uploadedDocument = sendDocumentReply(chatId, file, newFilename);
        file.delete();
        return uploadedDocument;
    }

    private String composeNewFilename(Document document, Conversion conversion) {
        return FileNameTools.extractFilenameWithoutExtension(document.getFileName()) +
                "." + conversion.getTo().name().toLowerCase();
    }

    private void saveInLoadingManager(Document document, ConversionInfo conversionInfo) {
        loadingManager.put(conversionInfo, document.getFileId());
    }

    static class ScriptStateDoc {
        private enum Stage {CHOOSING_CONVERSION, LOADING_FILE}

        private Stage stage;
        private Conversion conversion;

        public boolean isChoosingConversion() {
            return stage == Stage.CHOOSING_CONVERSION;
        }

        public void setChoosingConversion() {
            stage = Stage.CHOOSING_CONVERSION;
        }

        public boolean isLoadingFile() {
            return stage == Stage.LOADING_FILE;
        }

        public void setLoadingFile() {
            stage = Stage.LOADING_FILE;
        }

        public Conversion getConversion() {
            return conversion;
        }

        public void setConversion(Conversion conversion) {
            this.conversion = conversion;
        }
    }
}
