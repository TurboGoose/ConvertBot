package com.telegram.bot.chatservices.downloaders;

import org.telegram.telegrambots.meta.api.objects.Document;

import java.io.File;

public interface DownloaderChatService {
    File downloadDocument(Document document);
}
