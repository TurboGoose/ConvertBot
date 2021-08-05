package bot.handlers.scripts;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public abstract class AbstractScript implements Script {
    protected final TelegramLongPollingBot bot;

    public AbstractScript(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    public void sendTextReply(String chatId, String text, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = new SendMessage(chatId, text);
        sendMessage.setReplyMarkup(replyKeyboard);
        executeSendingTextReply(sendMessage);
    }

    public void sendTextReply(String chatId, String text) {
        executeSendingTextReply(new SendMessage(chatId, text));
    }

    private void executeSendingTextReply(SendMessage sendMessage) {
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public Document sendDocumentReply(String chatId, String fileId) {
        return executeSendingDocument(new SendDocument(chatId, new InputFile(fileId)));
    }

    public Document sendDocumentReply(String chatId, File file, String filename, ReplyKeyboard replyKeyboard) {
        SendDocument sendDocument = new SendDocument(chatId, new InputFile(file, filename));
        sendDocument.setReplyMarkup(replyKeyboard);
        return executeSendingDocument(sendDocument);
    }

    public Document sendDocumentReply(String chatId, File file, String filename) {
        return sendDocumentReply(chatId, file, filename, null);
    }

        private Document executeSendingDocument(SendDocument sendDocument) {
        try {
            return bot.execute(sendDocument).getDocument();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void answerCallbackQuery(CallbackQuery callbackQuery) {
        executeAnsweringCallbackQuery(new AnswerCallbackQuery(callbackQuery.getId()));
    }

    private void executeAnsweringCallbackQuery(AnswerCallbackQuery answer) {
        try {
            bot.execute(answer);
        } catch (TelegramApiException exc) {
            exc.printStackTrace();
        }
    }
}
