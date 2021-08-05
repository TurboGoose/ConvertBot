package bot.handlers.scripts;

import bot.fileloadingmanagers.ConversionInfo;
import bot.fileloadingmanagers.FileLoadingManager;
import bot.fileloadingmanagers.TelegramFileLoadingManager;
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
import java.util.HashMap;
import java.util.Map;

import static bot.handlers.scripts.ReplyKeyboards.availableConversions;

public class ConvertDocScript extends AbstractScript {
    private final AbstractDocConverterFactory factory = new DocConverterFactory();
    private final FileLoadingManager<ConversionInfo, String> loadingManager = new TelegramFileLoadingManager();
    private final Map<String, State> chatStates = new HashMap<>();

    public ConvertDocScript(TelegramLongPollingBot bot) {
        super(bot);
    }

    @Override
    public void start(String chatId) {
        chatStates.putIfAbsent(chatId, new State());
        chatStates.get(chatId).setChoosingConversion();
        sendTextReply(chatId, "What type of conversion do you want to do?",
                availableConversions(AvailableConversions.getDocConversions()));
    }

    @Override
    public void update(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String chatId = callbackQuery.getMessage().getChatId().toString();
            answerCallbackQuery(callbackQuery);
            if (chatStates.containsKey(chatId)) {
                State state = chatStates.get(chatId);
                if (state.isChoosingConversion()) {
                    Conversion chosenConversion = Conversion.parse(callbackQuery.getData());
                    sendTextReply(chatId, "Load your " + chosenConversion.getFrom().toString() + " file");
                    state.setLoadingFile(chosenConversion);
                }
            }
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            if (chatStates.containsKey(chatId)) {
                State state = chatStates.get(chatId);
                if (state.isLoadingFile()) {
                    if (message.hasDocument()) {
                        Document document = message.getDocument();
                        String extension = FileNameTools.extractExtension(document.getFileName()).toLowerCase();
                        Conversion conversion = state.getConversion();
                        if (extension.equals(conversion.getFrom().name().toLowerCase())) {
                            ConversionInfo info = new ConversionInfo(document.getFileUniqueId(), conversion);
                            if (!(loadingManager.contains(info) && sendDocumentReply(chatId, loadingManager.get(info)) != null)) {
                                Document uploadedDocument = convertAndSendDocument(chatId, document, conversion);
                                if (uploadedDocument != null) {
                                    saveDocumentInLoadingManager(uploadedDocument, info);
                                }
                            }
                        } else {
                            sendTextReply(chatId, "Wrong file extension");
                        }
                    } else {
                        sendTextReply(chatId, "Document required");
                    }
                    stop(chatId);
                }
            }
        }
    }

    private Document convertAndSendDocument(String chatId, Document document, Conversion conversion) {
        File inputFile = new DataDownloader(bot).downloadDocument(document);
        DocConverter converter = factory.getConverter(conversion);
        File outputFile = converter.convert(inputFile);
        Document uploadedDocument = sendDocumentReply(chatId, outputFile,
                FileNameTools.extractFilenameWithoutExtension(
                        document.getFileName()) + "." + conversion.getTo().name().toLowerCase());
        inputFile.delete();
        outputFile.delete();
        return uploadedDocument;
    }

    private void saveDocumentInLoadingManager(Document document, ConversionInfo conversionInfo) {
        loadingManager.put(conversionInfo, document.getFileId());
    }


    @Override
    public void stop(String chatId) {
        if (chatStates.containsKey(chatId)) {
            chatStates.get(chatId).setStop();
        }
    }

    private static class State {
        private enum DialogStage {CHOOSING_CONVERSION, LOADING_FILE, NONE}

        private boolean running = false;
        private DialogStage currentDialogStage = DialogStage.NONE;
        private Conversion conversion = null;

        Conversion getConversion() {
            return conversion;
        }

        void setChoosingConversion() {
            running = true;
            currentDialogStage = DialogStage.CHOOSING_CONVERSION;
            conversion = null;
        }

        boolean isChoosingConversion() {
            return running && currentDialogStage == DialogStage.CHOOSING_CONVERSION;
        }

        void setLoadingFile(Conversion conversion) {
            running = true;
            this.conversion = conversion;
            currentDialogStage = DialogStage.LOADING_FILE;
        }

        boolean isLoadingFile() {
            return running && currentDialogStage == DialogStage.LOADING_FILE;
        }

        void setStop() {
            running = false;
            currentDialogStage = DialogStage.NONE;
            conversion = null;
        }
    }
}
