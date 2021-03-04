package bot.processors.noncommands;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

    public void sendDocumentReply(String chatId, String fileId, ReplyKeyboard replyKeyboard) {
        SendDocument sendDocument = new SendDocument(chatId, new InputFile(fileId));
        sendDocument.setReplyMarkup(replyKeyboard);
        try {
            bot.execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendDocumentReply(String chatId, String fileId) {
        sendDocumentReply(chatId, fileId, null);
    }

    public void sendDocumentReply(String chatId, File file, ReplyKeyboard replyKeyboard) {
        SendDocument sendDocument = new SendDocument(chatId, new InputFile(file));
        sendDocument.setReplyMarkup(replyKeyboard);
        try {
            bot.execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendDocumentReply(String chatId, File file) {
        sendDocumentReply(chatId, file, null);
    }
}
