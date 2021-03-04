package bot.processors.noncommands;

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
import java.util.List;

public class ConvertScript extends AbstractScript {
    private enum State {CHOOSING_FILE, LOADING_FILE}
    private boolean running = false;
    private final AbstractConverterFactory factory = new ConverterFactory();
    private State currentStateOfDialog;
    private Conversion chosenConversion;


    public ConvertScript(TelegramLongPollingBot bot) {
        super(bot);
    }

    @Override
    public void start(Chat chat) {
        running = true;
        currentStateOfDialog = State.CHOOSING_FILE;
        String text = "What type of conversion do you want to do?";
        sendTextReply(chat.getId().toString(), text, getKeyboardWithAvailableConversions());
    }

    @Override
    public void update(Update update) {
        if (update.hasMessage()) {
            sendTextReply(update.getMessage().getChatId().toString(), Boolean.toString(running));
        }
        if (running) {
            if (currentStateOfDialog == State.CHOOSING_FILE && update.hasCallbackQuery()) {
                CallbackQuery callback = update.getCallbackQuery();
                answerCallbackQuery(callback);
                chosenConversion = Conversion.parse(callback.getData());
                String text = "Load your " + chosenConversion.getFrom().toString() + " file";
                sendTextReply(callback.getMessage().getChatId().toString(), text);
                currentStateOfDialog = State.LOADING_FILE;
            } else if (currentStateOfDialog == State.LOADING_FILE && update.hasMessage()) {
                Message message = update.getMessage();
                String chatId = message.getChatId().toString();
                if (message.hasDocument()) {
                    Document document = message.getDocument();
                    String extension = FileNameTools.extractExtension(document.getFileName());
                    if (extension.equals(chosenConversion.getFrom().name().toLowerCase())) {
                        File sourceFile = downloadDocument(document);
                        Converter converter = factory.getConverter(chosenConversion);
                        File outputFile = converter.convert(sourceFile);
                        sendDocumentReply(chatId, outputFile);
                    } else {
                        sendTextReply(chatId, "Wrong file extension");
                    }
                } else {
                    sendTextReply(chatId, "Document required");
                }
                stop();
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
    public void stop() {
        running = false;
    }

    private ReplyKeyboard getKeyboardWithAvailableConversions(int maxButtonsInRow) {
        List<Conversion> conversions = AvailableConversions.getAvailable();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        int count = 0;
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (Conversion conversion : conversions) {
            if (count == maxButtonsInRow) {
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

    private ReplyKeyboard getKeyboardWithAvailableConversions() {
        return getKeyboardWithAvailableConversions(2);
    }
}
