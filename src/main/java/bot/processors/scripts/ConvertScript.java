package bot.processors.scripts;

import convertations.conversions.AvailableConversions;
import convertations.conversions.Conversion;
import convertations.converters.Converter;
import convertations.factory.AbstractConverterFactory;
import convertations.factory.ConverterFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tools.files.FileNameTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertScript extends AbstractScript {
    private final AbstractConverterFactory factory = new ConverterFactory();
    private final Map<String, State> chatStates = new HashMap<>();

    public ConvertScript(TelegramLongPollingBot bot) {
        super(bot);
    }

    @Override
    public void start(String chatId) {
        chatStates.putIfAbsent(chatId, new State());
        chatStates.get(chatId).setChoosingConversion();
        sendTextReply(chatId, "What type of conversion do you want to do?", getKeyboardWithAvailableConversions());
    }

    private ReplyKeyboard getKeyboardWithAvailableConversions() {
        final int MAX_BUTTONS_IN_ROW = 2;
        List<Conversion> conversions = AvailableConversions.getAvailable();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        int count = 0;
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (Conversion conversion : conversions) {
            if (count == MAX_BUTTONS_IN_ROW) {
                buttons.add(row);
                row = new ArrayList<>();
                count = 0;
            }
            InlineKeyboardButton button = new InlineKeyboardButton(conversion.toString());
            button.setCallbackData(conversion.toString());
            row.add(button);
            count++;
        }
        if (!row.isEmpty()) {
            buttons.add(row);
        }
        return new InlineKeyboardMarkup(buttons);
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
                        String filename = document.getFileName();
                        String extension = FileNameTools.extractExtension(filename).toLowerCase();
                        String nameWithoutExtension = FileNameTools.extractFilenameWithoutExtension(filename);
                        Conversion conversion = state.getConversion();
                        if (extension.equals(conversion.getFrom().name().toLowerCase())) {
                            File sourceFile = downloadDocument(document);
                            Converter converter = factory.getConverter(conversion);
                            File outputFile = converter.convert(sourceFile);
                            sendDocumentReply(chatId, outputFile,
                                    nameWithoutExtension + "." + conversion.getTo().name().toLowerCase());
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

    private File downloadDocument(Document document) {
        String fileName = document.getFileName();
        GetFile getFile = new GetFile(document.getFileId());
        try {
            File outputFile = File.createTempFile(
                    FileNameTools.extractFilenameWithoutExtension(fileName),
                    "." + FileNameTools.extractExtension(fileName));
            outputFile.deleteOnExit();
            return bot.downloadFile(bot.execute(getFile), outputFile);
        } catch (TelegramApiException exc) {
            throw new IllegalStateException(exc);
        } catch (IOException exc) {
            throw new IllegalStateException("Can't create temporary file for " + fileName);
        }
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
