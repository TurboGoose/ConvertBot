package com.telegram.bot.chatservices.senders;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.io.File;

public interface SenderChatService {
    void sendTextReply(String text, ReplyKeyboard replyKeyboard);

    void sendTextReply(String text);

    Document sendDocumentReply(File file, String filename, ReplyKeyboard replyKeyboard);

    Document sendDocumentReply(File file, String filename);

    Document sendDocumentReply(String fileId, ReplyKeyboard replyKeyboard);

    Document sendDocumentReply(String fileId);

    void answerCallbackQuery(CallbackQuery callbackQuery);
}
