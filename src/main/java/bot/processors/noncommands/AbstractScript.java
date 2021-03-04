package bot.processors.noncommands;

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
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendTextReply(String chatId, String text) {
        sendTextReply(chatId, text, null);
    }

    public void sendDocumentReply(String chatId, String fileId) {
        SendDocument sendDocument = new SendDocument(chatId, new InputFile(fileId));
        try {
            bot.execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendDocumentReply(String chatId, File file) {
        SendDocument sendDocument = new SendDocument(chatId, new InputFile(file));
        try {
            bot.execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void answerCallbackQuery(CallbackQuery callbackQuery, String text) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery(callbackQuery.getId());
        answer.setText(text);
        try {
            bot.execute(answer);
        } catch (TelegramApiException exc) {
            exc.printStackTrace();
        }
    }

    public void answerCallbackQuery(CallbackQuery callbackQuery) {
        answerCallbackQuery(callbackQuery, null);
    }
}
