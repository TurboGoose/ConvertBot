package bot.processors.scripts;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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

    public void sendDocumentReply(String chatId, String fileId) {
        executeSendingDocument(new SendDocument(chatId, new InputFile(fileId)));
    }

    public void sendDocumentReply(String chatId, File file) {
        executeSendingDocument(new SendDocument(chatId, new InputFile(file)));
    }

    public void sendDocumentReply(String chatId, File file, String filename) {
        executeSendingDocument(new SendDocument(chatId, new InputFile(file, filename)));
    }

    private void executeSendingDocument(SendDocument sendDocument) {
        try {
            bot.execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void answerCallbackQuery(CallbackQuery callbackQuery, String text) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery(callbackQuery.getId());
        answer.setText(text);
        executeAnsweringCallbackQuery(answer);
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
