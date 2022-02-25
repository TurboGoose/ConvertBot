package com.telegram.bot.chatservices.senders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class SenderChatServiceImpl implements SenderChatService {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    private final AbsSender sender;
    private final String chatId;

    public SenderChatServiceImpl(AbsSender sender, String chatId) {
        this.sender = sender;
        this.chatId = chatId;
    }

    @Override
    public void sendTextReply(String text, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = new SendMessage(chatId, text);
        sendMessage.setReplyMarkup(replyKeyboard);
        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException exc) {
            LOG.error("[{}] Failed sending text reply \"{}\".", chatId, text, exc);
        }
    }

    @Override
    public void sendTextReply(String text) {
        sendTextReply(text, null);
    }

    @Override
    public Document sendDocumentReply(File file, String filename, ReplyKeyboard replyKeyboard) {
        SendDocument sendDocument = new SendDocument(chatId, new InputFile(file, filename));
        sendDocument.setReplyMarkup(replyKeyboard);
        try {
            Document uploadedDocument = sender.execute(sendDocument).getDocument();
            LOG.debug("[{}] Document {} has been successfully sent. Current fileId is {}.",
                    chatId, uploadedDocument.getFileUniqueId(), uploadedDocument.getFileId());
            return uploadedDocument;
        } catch (TelegramApiException exc) {
            LOG.error("[{}] Failed sending document {}.", chatId, filename, exc);
        }
        return null;
    }

    @Override
    public Document sendDocumentReply(File file, String filename) {
        return sendDocumentReply(file, filename, null);
    }

    @Override
    public Document sendDocumentReply(String fileId, ReplyKeyboard replyKeyboard) {
        SendDocument sendDocument = new SendDocument(chatId, new InputFile(fileId));
        sendDocument.setReplyMarkup(replyKeyboard);
        try {
            Document uploadedDocument = sender.execute(sendDocument).getDocument();
            LOG.debug("[{}] Document {} has been successfully sent via fileId. Current fileId is {}.",
                    chatId, uploadedDocument.getFileUniqueId(), uploadedDocument.getFileId());
            return uploadedDocument;
        } catch (TelegramApiException exc) {
            LOG.error("[{}] Failed sending document via fileId {}.", chatId, fileId, exc);
        }
        return null;
    }

    @Override
    public Document sendDocumentReply(String fileId) {
        return sendDocumentReply(fileId, null);
    }

    @Override
    public void answerCallbackQuery(CallbackQuery callbackQuery) {
        String callbackQueryId = callbackQuery.getId();
        AnswerCallbackQuery answer = new AnswerCallbackQuery(callbackQueryId);
        try {
            sender.execute(answer);
        } catch (TelegramApiException exc) {
            LOG.error("[{}] Failed answering callback query with id {}.",
                    callbackQuery.getMessage().getChatId(), callbackQueryId, exc);
        }
    }
}