package com.telegram.bot.chatservices.downloaders;

import com.telegram.utils.FileNameTools;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;

public class DownloaderChatServiceImpl implements DownloaderChatService {
    private final TelegramLongPollingBot bot;

    public DownloaderChatServiceImpl(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    @Override
    public File downloadDocument(Document document) {
        String fileName = document.getFileName();
        GetFile getFile = new GetFile(document.getFileId());
        try {
            String suffix = "." + FileNameTools.extractExtension(fileName);
            String prefix = FileNameTools.extractFilenameWithoutExtension(fileName);
            if (prefix.length() < 3) {
                prefix += "___";
            }
            File outputFile = File.createTempFile(prefix, suffix);
            outputFile.deleteOnExit();

            return bot.downloadFile(bot.execute(getFile), outputFile);
        } catch (TelegramApiException exc) {
            throw new RuntimeException(exc);
        } catch (IOException exc) {
            throw new RuntimeException("Unable to create temporary file for " + fileName, exc);
        }
    }
}