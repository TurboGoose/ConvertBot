package com.telegram.bot.handlers.scripts.helperclasses;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataDownloaderTest {
    @Mock
    TelegramLongPollingBot botMocked;
    @InjectMocks
    DataDownloader downloader;
    final String fileId = "1";
    final String fileName = "document.pdf";

    @Test
    public void whenDocDownloadsSuccessfully() throws TelegramApiException {
        org.telegram.telegrambots.meta.api.objects.File telegramFile =
                new org.telegram.telegrambots.meta.api.objects.File();
        when(botMocked.execute(any(GetFile.class))).thenReturn(telegramFile);
        File returnedFile = new File(fileName);
        when(botMocked.downloadFile(eq(telegramFile), any(File.class))).thenReturn(returnedFile);
        Document document = new Document();
        document.setFileName(fileName);
        document.setFileId(fileId);
        File resultFile = downloader.downloadDocument(document);
        assertThat(resultFile, is(resultFile));
        ArgumentCaptor<GetFile> captor = ArgumentCaptor.forClass(GetFile.class);
        verify(botMocked).execute(captor.capture());
        GetFile passedGetFile = captor.getValue();
        assertThat(passedGetFile.getFileId(), is(fileId));
        verify(botMocked).downloadFile(eq(telegramFile), any(File.class));
    }

    @Test
    public void whenDocNameIsTooShortThenAnywayDocDownloadsSuccessfully() throws TelegramApiException {
        org.telegram.telegrambots.meta.api.objects.File telegramFile =
                new org.telegram.telegrambots.meta.api.objects.File();
        when(botMocked.execute(any(GetFile.class))).thenReturn(telegramFile);
        File returnedFile = new File("1.pdf");
        when(botMocked.downloadFile(eq(telegramFile), any(File.class))).thenReturn(returnedFile);
        Document document = new Document();
        document.setFileName("1.pdf");
        document.setFileId(fileId);
        File resultFile = downloader.downloadDocument(document);
        assertThat(resultFile, is(resultFile));
        ArgumentCaptor<GetFile> captor = ArgumentCaptor.forClass(GetFile.class);
        verify(botMocked).execute(captor.capture());
        GetFile passedGetFile = captor.getValue();
        assertThat(passedGetFile.getFileId(), is(fileId));
        verify(botMocked).downloadFile(eq(telegramFile), any(File.class));
    }

    @Test
    public void whenExecuteMethodThrowsExceptionThenRuntimeExceptionRethrows() throws TelegramApiException {
        Throwable cause = new TelegramApiException();
        when(botMocked.execute(any(GetFile.class))).thenThrow(cause);
        Document document = new Document();
        document.setFileName(fileName);
        document.setFileId(fileId);
        Throwable exc = assertThrows(RuntimeException.class, () -> downloader.downloadDocument(document));
        assertThat(exc.getCause(), is(cause));
        ArgumentCaptor<GetFile> captor = ArgumentCaptor.forClass(GetFile.class);
        verify(botMocked).execute(captor.capture());
        GetFile passedGetFile = captor.getValue();
        assertThat(passedGetFile.getFileId(), is(fileId));
    }

    @Test
    public void whenDownloadFileMethodThrowsExceptionThenRuntimeExceptionRethrows() throws TelegramApiException {
        org.telegram.telegrambots.meta.api.objects.File telegramFile =
                new org.telegram.telegrambots.meta.api.objects.File();
        when(botMocked.execute(any(GetFile.class))).thenReturn(telegramFile);
        Throwable cause = new TelegramApiException();
        when(botMocked.downloadFile(eq(telegramFile), any(File.class))).thenThrow(cause);
        Document document = new Document();
        document.setFileName(fileName);
        document.setFileId(fileId);
        Throwable exc = assertThrows(RuntimeException.class, () -> downloader.downloadDocument(document));
        assertThat(exc.getCause(), is(cause));
        ArgumentCaptor<GetFile> captor = ArgumentCaptor.forClass(GetFile.class);
        verify(botMocked).execute(captor.capture());
        GetFile passedGetFile = captor.getValue();
        assertThat(passedGetFile.getFileId(), is(fileId));
        verify(botMocked).downloadFile(eq(telegramFile), any(File.class));
    }

    @Test
    public void whenUnableToCreateTempFileThenRethrowRuntimeException() {
        Document document = new Document();
        document.setFileName(fileName);
        document.setFileId(fileId);
        try (MockedStatic<File> mockedFile = mockStatic(File.class)) {
            Throwable cause = new IOException();
            mockedFile.when(() -> File.createTempFile(anyString(), anyString())).thenThrow(cause);
            Throwable exc = assertThrows(RuntimeException.class, () -> downloader.downloadDocument(document));
            assertThat(exc.getCause(), is(cause));
        }
    }
}