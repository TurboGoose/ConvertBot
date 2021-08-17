package com.telegram.bot.handlers.scripts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    protected final TelegramLongPollingBot bot;

    public AbstractScript(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

// sending text replies ------------------------------------------------------------------------------------------------

    public void sendTextReply(String chatId, String text, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = new SendMessage(chatId, text);
        sendMessage.setReplyMarkup(replyKeyboard);
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException exc) {
            LOG.error("[{}] Failed sending text reply \"{}\".", chatId, text, exc);
        }
    }

    public void sendTextReply(String chatId, String text) {
        sendTextReply(chatId, text, null);
    }

// sending document replies --------------------------------------------------------------------------------------------

    public Document sendDocumentReply(String chatId, File file, String filename, ReplyKeyboard replyKeyboard) {
        SendDocument sendDocument = new SendDocument(chatId, new InputFile(file, filename));
        sendDocument.setReplyMarkup(replyKeyboard);
        try {
            Document uploadedDocument = bot.execute(sendDocument).getDocument();
            LOG.debug("[{}] Document {} has been successfully sent. Current fileId is {}.",
                    chatId, uploadedDocument.getFileUniqueId(), uploadedDocument.getFileId());
            return uploadedDocument;
        } catch (TelegramApiException exc) {
            LOG.error("[{}] Failed sending document {}.", chatId, filename, exc);
        }
        return null;
    }

    public Document sendDocumentReply(String chatId, File file, String filename) {
        return sendDocumentReply(chatId, file, filename, null);
    }

    public Document sendDocumentReply(String chatId, String fileId, ReplyKeyboard replyKeyboard) {
        SendDocument sendDocument = new SendDocument(chatId, new InputFile(fileId));
        sendDocument.setReplyMarkup(replyKeyboard);
        try {
            Document uploadedDocument = bot.execute(sendDocument).getDocument();
            LOG.debug("[{}] Document {} has been successfully sent via fileId. Current fileId is {}.",
                    chatId, uploadedDocument.getFileUniqueId(), uploadedDocument.getFileId());
            return uploadedDocument;
        } catch (TelegramApiException exc) {
            LOG.error("[{}] Failed sending document via fileId {}.", chatId, fileId, exc);
        }
        return null;
    }

    public Document sendDocumentReply(String chatId, String fileId) {
        return sendDocumentReply(chatId, fileId, null);
    }

// answering callback queries ------------------------------------------------------------------------------------------

    public void answerCallbackQuery(CallbackQuery callbackQuery) {
        String callbackQueryId = callbackQuery.getId();
        AnswerCallbackQuery answer = new AnswerCallbackQuery(callbackQueryId);
        try {
            bot.execute(answer);
        } catch (TelegramApiException exc) {
            LOG.error("[{}] Failed answering callback query with id {}.",
                    callbackQuery.getMessage().getChatId(), callbackQueryId, exc);
        }
    }
}
