package bot.processors.commands;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public abstract class AbstractCommand extends BotCommand {

    public AbstractCommand(String command, String description) {
        super(command, description);
    }

    public void sendTextReply(AbsSender sender, Long chatId, String text, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), text);
        sendMessage.setReplyMarkup(replyKeyboard);
        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendTextReply(AbsSender sender, Long chatId, String text) {
        sendTextReply(sender, chatId, text, null);
    }

    public void sendDocumentReply(AbsSender sender, Long chatId, String fileId, ReplyKeyboard replyKeyboard) {
        SendDocument sendDocument = new SendDocument(chatId.toString(), new InputFile(fileId));
        sendDocument.setReplyMarkup(replyKeyboard);
        try {
            sender.execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendDocumentReply(AbsSender sender, Long chatId, String fileId) {
        sendDocumentReply(sender, chatId, fileId, null);
    }

    public void sendDocumentReply(AbsSender sender, Long chatId, File file, ReplyKeyboard replyKeyboard) {
        SendDocument sendDocument = new SendDocument(chatId.toString(), new InputFile(file));
        sendDocument.setReplyMarkup(replyKeyboard);
        try {
            sender.execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendDocumentReply(AbsSender sender, Long chatId, File file) {
        sendDocumentReply(sender, chatId, file, null);
    }
}
