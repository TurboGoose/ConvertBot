package com.telegram.bot.handlers.scripts;

import com.telegram.bot.chatservices.downloaders.DownloaderChatService;
import com.telegram.bot.chatservices.senders.SenderChatService;
import com.telegram.bot.chatstates.ChatStates;
import com.telegram.bot.helperclasses.ReplyKeyboards;
import com.telegram.convertations.converters.Converter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConvertScriptTest {
    @Mock
    Converter converter;
    @Mock
    SenderChatService sender;
    @Mock
    DownloaderChatService downloader;
    @Mock
    ChatStates states;
    @Spy
    @InjectMocks
    ConvertScript script;
    @Mock
    Update update;
    final String STOP_WORD = "Done";

    @Test
    public void ifScriptWasNotStartedThenNothingHappens() {
        script.update(update);
        script.stop();

        verifyNoInteractions(converter, sender, downloader, states, update);
    }

    @Test
    public void whenScriptStartsThenSendTextReply() {
        script.start();

        verify(sender).sendTextReply(anyString(), eq(ReplyKeyboards.getButtonWithText(STOP_WORD)));
        verifyNoMoreInteractions(sender);
    }

    @Test
    public void whenScriptStartsTwiceThenSecondTimeNothingHappens() {
        script.start();
        script.start();

        verify(sender).sendTextReply(anyString(), eq(ReplyKeyboards.getButtonWithText(STOP_WORD)));
        verifyNoMoreInteractions(sender);
    }

    @Test
    public void whenScriptStopsThenUpdateItsStateToNull() {
        script.start();
        script.stop();

        verify(sender).sendTextReply(anyString(), eq(ReplyKeyboards.getButtonWithText(STOP_WORD)));
        verify(states, only()).put(null);
        verifyNoMoreInteractions(sender);
    }

    @Test
    public void whenScriptStopsTwiceThenSecondTimeNothingHappens() {
        script.start();
        script.stop();
        script.stop();

        verify(sender).sendTextReply(anyString(), eq(ReplyKeyboards.getButtonWithText(STOP_WORD)));
        verify(states, only()).put(null);
        verifyNoMoreInteractions(sender);
    }

    @Test
    public void whenUpdateHasNoMessageThenNothingHappens() {
        when(update.hasMessage()).thenReturn(false);

        script.start();
        script.update(update);

        verify(sender).sendTextReply(anyString(), eq(ReplyKeyboards.getButtonWithText(STOP_WORD)));
        verify(update, only()).hasMessage();
        verifyNoMoreInteractions(sender);
    }

    @Test
    public void whenMessageHasTextAndItIsNotAStopWordThenNothingHappens() {
        Message message = spy(new Message());
        message.setText("Not a stop word");
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);

        script.start();
        script.update(update);

        verify(update).hasMessage();
        verify(update).getMessage();
        verify(message).hasText();
        verify(message).getText();
        verify(sender).sendTextReply(anyString(), eq(ReplyKeyboards.getButtonWithText(STOP_WORD)));
        verifyNoMoreInteractions(sender);
    }

    @Test
    public void whenMessageHasStopWordButNoImagesProvidedThenSendTextReplyAndStop() {
        Message message = spy(new Message());
        message.setText(STOP_WORD);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        script.start();
        script.update(update);
        verify(update).hasMessage();
        verify(update).getMessage();
        verify(message).hasText();
        verify(message).getText();
        verify(sender).sendTextReply(anyString(), eq(ReplyKeyboards.getButtonWithText(STOP_WORD)));
        verify(sender).sendTextReply(anyString(), eq(ReplyKeyboards.removeKeyboard()));
        verify(script).stop();
        verifyNoMoreInteractions(sender);
    }

    @Test
    public void whenMessageHasStopWordAndImagesProvidedThenDownloadConvertSendAndStop() {
        Message stopMes = spy(new Message());
        stopMes.setText(STOP_WORD);

        Message docMes = spy(new Message());
        Document doc = new Document();
        String fileName = "input.jpg";
        doc.setFileName(fileName);
        docMes.setDocument(doc);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(docMes, stopMes);

        File downloadedFile = new File(fileName);
        when(downloader.downloadDocument(doc)).thenReturn(downloadedFile);
        File outputFile = new File("output.pdf");
        when(converter.convert(List.of(downloadedFile))).thenReturn(outputFile);

        script.start();
        script.update(update);
        script.update(update);

        verify(sender).sendTextReply(anyString(), eq(ReplyKeyboards.getButtonWithText(STOP_WORD)));
        verify(update, times(2)).hasMessage();
        verify(update, times(2)).getMessage();
        verify(docMes).hasDocument();
        verify(docMes).getDocument();
        verify(stopMes).hasText();
        verify(stopMes).getText();
        verify(downloader, only()).downloadDocument(doc);
        verify(converter, only()).convert(List.of(downloadedFile));
        verify(sender).sendDocumentReply(eq(outputFile), eq("images.pdf"), eq(ReplyKeyboards.removeKeyboard()));
        verify(script).stop();
        verifyNoMoreInteractions(sender);
    }

    @Test
    public void whenDocumentProvidedThenNothingHappens() {
        Message mes = spy(new Message());
        Document doc = new Document();
        String fileName = "input.jpg";
        doc.setFileName(fileName);
        mes.setDocument(doc);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(mes);

        script.start();
        script.update(update);

        verify(sender).sendTextReply(anyString(), eq(ReplyKeyboards.getButtonWithText(STOP_WORD)));
        verify(update).hasMessage();
        verify(update).getMessage();
        verify(mes).hasDocument();
        verify(mes).getDocument();
        verifyNoMoreInteractions(sender);
    }

    @Test
    public void whenProvidedDocumentHasWrongExtensionThenSendErrorMessage() {
        Message mes = spy(new Message());
        Document doc = new Document();
        String fileName = "input.docx";
        doc.setFileName(fileName);
        mes.setDocument(doc);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(mes);

        script.start();
        script.update(update);

        verify(sender).sendTextReply(anyString(), eq(ReplyKeyboards.getButtonWithText(STOP_WORD)));
        verify(update).hasMessage();
        verify(update).getMessage();
        verify(mes).hasDocument();
        verify(mes).getDocument();
        verify(sender).sendTextReply(anyString());
        verifyNoMoreInteractions(sender);
    }

    @Test
    public void whenMaximumNumberOfImagesExceededByDocumentThenSendErrorMessageUploadConvertSendAndStop() {
        final int CAP = ConvertScript.CAPACITY;
        List<File> downloadedFiles = new ArrayList<>(CAP);
        List<Document> documents = new ArrayList<>(CAP);
        Message message = spy(new Message());
        when(update.hasMessage()).thenReturn(true);

        script.start();

        for (int i = 0; i < CAP; i++) {
            Document doc = new Document();
            String fileName = String.format("input%d.jpg", i);
            doc.setFileName(fileName);
            documents.add(doc);
            message.setDocument(doc);
            when(update.getMessage()).thenReturn(message);

            File file = new File(fileName);
            when(downloader.downloadDocument(doc)).thenReturn(file);
            downloadedFiles.add(file);

            script.update(update);
        }
        File outputFile = new File("output.pdf");
        when(converter.convert(downloadedFiles)).thenReturn(outputFile);

        script.update(update);

        verify(sender).sendTextReply(anyString(), eq(ReplyKeyboards.getButtonWithText(STOP_WORD)));
        verify(update, times(CAP + 1)).hasMessage();
        verify(update, times(CAP + 1)).getMessage();
        documents.forEach(doc -> verify(downloader).downloadDocument(doc));
        verify(message, times(CAP + 1)).hasDocument();
        verify(message, times(CAP + 1)).getDocument();
        verify(sender).sendTextReply(anyString()); // capacity exceeded warning
        verify(converter, only()).convert(downloadedFiles);
        verify(sender).sendDocumentReply(eq(outputFile), eq("images.pdf"), eq(ReplyKeyboards.removeKeyboard()));
        verify(script).stop();
        verifyNoMoreInteractions(sender);
    }

    @Test
    public void whenPhotoProvidedThenNothingHappens() {
        Message mes = spy(new Message());
        mes.setPhoto(List.of(new PhotoSize(), new PhotoSize()));

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(mes);

        script.start();
        script.update(update);

        verify(sender).sendTextReply(anyString(), eq(ReplyKeyboards.getButtonWithText(STOP_WORD)));
        verify(update).hasMessage();
        verify(update).getMessage();
        verify(mes).hasPhoto();
        verify(mes).getPhoto();
        verifyNoMoreInteractions(sender);
    }

    @Test
    public void whenMaximumNumberOfImagesExceededByPhotoThenSendErrorMessageUploadConvertSendAndStop() {
        final int CAP = ConvertScript.CAPACITY;
        List<File> downloadedFiles = new ArrayList<>(CAP);
        List<Document> documents = new ArrayList<>(CAP);
        Message message = spy(new Message());
        when(update.hasMessage()).thenReturn(true);
        script.start();
        for (int i = 0; i < CAP; i++) {
            PhotoSize photo = new PhotoSize();
            photo.setFileUniqueId("img" + i);
            message.setPhoto(List.of(photo, photo));
            when(update.getMessage()).thenReturn(message);

            Document docFromPhoto = script.photoSizeToDocument(photo);
            documents.add(docFromPhoto);
            File file = new File(docFromPhoto.getFileName());
            when(downloader.downloadDocument(docFromPhoto)).thenReturn(file);
            downloadedFiles.add(file);

            script.update(update);
        }
        File outputFile = new File("output.pdf");
        when(converter.convert(downloadedFiles)).thenReturn(outputFile);

        script.update(update);

        verify(sender).sendTextReply(anyString(), eq(ReplyKeyboards.getButtonWithText(STOP_WORD)));
        verify(update, times(CAP + 1)).hasMessage();
        verify(update, times(CAP + 1)).getMessage();
        verify(message, times(CAP + 1)).hasPhoto();
        verify(message, times(CAP + 1)).getPhoto();
        verify(sender).sendTextReply(anyString()); // capacity exceeded warning
        documents.forEach(doc -> verify(downloader).downloadDocument(doc));
        verify(converter, only()).convert(downloadedFiles);
        verify(sender).sendDocumentReply(eq(outputFile), eq("images.pdf"), eq(ReplyKeyboards.removeKeyboard()));
        verify(script).stop();
        verifyNoMoreInteractions(sender);
    }
}