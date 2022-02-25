package com.telegram.bot.chatservices.senders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SenderChatServiceImplTest {
    @Mock
    TelegramLongPollingBot botMocked;
    SenderChatService senderChatService;
    final String chatId = "1";
    final ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();

    @BeforeEach
    public void setUp() {
        senderChatService = new SenderChatServiceImpl(botMocked, chatId);
    }

    @Nested
    @DisplayName("Testing sendTextReply method")
    class SendTextReplyTest {
        final String text = "text";

        @Test
        public void whenExecuteMethodCallsSuccessfully() throws TelegramApiException {
            senderChatService.sendTextReply(text, keyboard);
            ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
            verify(botMocked).execute(captor.capture());
            SendMessage sentMessage = captor.getValue();
            assertThat(sentMessage.getChatId(), is(chatId));
            assertThat(sentMessage.getText(), is(text));
            assertThat(sentMessage.getReplyMarkup(), is(keyboard));
            verifyNoMoreInteractions(botMocked);
        }

        @Test
        public void whenExecuteMethodThrowsException() throws TelegramApiException {
            Throwable exc = new TelegramApiException();
            doThrow(exc).when(botMocked).execute(any(SendMessage.class));
            senderChatService.sendTextReply(text, keyboard);
            ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
            verify(botMocked).execute(captor.capture());
            SendMessage sentMessage = captor.getValue();
            assertThat(sentMessage.getChatId(), is(chatId));
            assertThat(sentMessage.getText(), is(text));
            assertThat(sentMessage.getReplyMarkup(), is(keyboard));
            verifyNoMoreInteractions(botMocked);
        }
    }

    @Nested
    @DisplayName("Testing sendDocumentReply method")
    class SendDocumentReplyTest {
        final String filename = "file.txt";
        final String fileId = "fileId1";
        final File file = new File("test.txt");

        @Test
        public void whenSendingDocumentViaPassingFileAndExecuteMethodCallsSuccessfullyThenReturnUploadedFile() throws TelegramApiException {
            Message message = new Message();
            Document document = new Document();
            message.setDocument(document);
            when(botMocked.execute(any(SendDocument.class))).thenReturn(message);
            Document uploadedDocument = senderChatService.sendDocumentReply(file, filename, keyboard);
            assertThat(uploadedDocument, is(document));
            ArgumentCaptor<SendDocument> captor = ArgumentCaptor.forClass(SendDocument.class);
            verify(botMocked).execute(captor.capture());
            SendDocument sentDocument = captor.getValue();
            assertThat(sentDocument.getChatId(), is(chatId));
            assertThat(sentDocument.getReplyMarkup(), is(keyboard));
            InputFile inputFile = sentDocument.getDocument();
            assertThat(inputFile.getNewMediaFile(), is(file));
            assertThat(inputFile.getMediaName(), is(filename));
            verifyNoMoreInteractions(botMocked);
        }

        @Test
        public void whenSendingDocumentViaPassingFileAndExecuteMethodThrowsExceptionThenReturnNull() throws TelegramApiException {
            when(botMocked.execute(any(SendDocument.class))).thenThrow(new TelegramApiException());
            Document uploadedDocument = senderChatService.sendDocumentReply(file, filename, keyboard);
            assertThat(uploadedDocument, is(nullValue()));
            ArgumentCaptor<SendDocument> captor = ArgumentCaptor.forClass(SendDocument.class);
            verify(botMocked).execute(captor.capture());
            SendDocument sentDocument = captor.getValue();
            assertThat(sentDocument.getChatId(), is(chatId));
            assertThat(sentDocument.getReplyMarkup(), is(keyboard));
            InputFile inputFile = sentDocument.getDocument();
            assertThat(inputFile.getNewMediaFile(), is(file));
            assertThat(inputFile.getMediaName(), is(filename));
            verifyNoMoreInteractions(botMocked);
        }

        @Test
        public void whenSendingDocumentViaFileIdAndExecuteMethodCallsSuccessfullyThenReturnUploadedFile() throws TelegramApiException {
            Message message = new Message();
            Document document = new Document();
            message.setDocument(document);
            when(botMocked.execute(any(SendDocument.class))).thenReturn(message);
            Document uploadedDocument = senderChatService.sendDocumentReply(fileId, keyboard);
            assertThat(uploadedDocument, is(document));
            ArgumentCaptor<SendDocument> captor = ArgumentCaptor.forClass(SendDocument.class);
            verify(botMocked).execute(captor.capture());
            SendDocument sentDocument = captor.getValue();
            assertThat(sentDocument.getChatId(), is(chatId));
            assertThat(sentDocument.getReplyMarkup(), is(keyboard));
            assertThat(sentDocument.getDocument().getAttachName(), is(fileId));
            verifyNoMoreInteractions(botMocked);
        }

        @Test
        public void whenSendingDocumentViaFileIdAndExecuteMethodThrowsExceptionThenReturnNull() throws TelegramApiException {
            when(botMocked.execute(any(SendDocument.class))).thenThrow(new TelegramApiException());
            Document uploadedDocument = senderChatService.sendDocumentReply(fileId, keyboard);
            assertThat(uploadedDocument, is(nullValue()));
            ArgumentCaptor<SendDocument> captor = ArgumentCaptor.forClass(SendDocument.class);
            verify(botMocked).execute(captor.capture());
            SendDocument sentDocument = captor.getValue();
            assertThat(sentDocument.getChatId(), is(chatId));
            assertThat(sentDocument.getReplyMarkup(), is(keyboard));
            assertThat(sentDocument.getDocument().getAttachName(), is(fileId));
            verifyNoMoreInteractions(botMocked);
        }
    }

    @Nested
    @DisplayName("Testing answerCallbackQuery method")
    class AnswerCallbackQueryTest {
        @Mock
        CallbackQuery callbackQueryMocked;
        final String callbackQueryId = "123";

        @BeforeEach
        public void setUp() {
            when(callbackQueryMocked.getId()).thenReturn(callbackQueryId);
        }

        @Test
        public void whenAnsweringCallbackAndExecuteMethodCallsSuccessfully() throws TelegramApiException {
            senderChatService.answerCallbackQuery(callbackQueryMocked);
            ArgumentCaptor<AnswerCallbackQuery> captor = ArgumentCaptor.forClass(AnswerCallbackQuery.class);
            verify(botMocked).execute(captor.capture());
            AnswerCallbackQuery answerCallbackQuery = captor.getValue();
            assertThat(answerCallbackQuery.getCallbackQueryId(), is(callbackQueryId));
            verifyNoMoreInteractions(botMocked);
        }

        @Test
        public void whenAnsweringCallbackAndExecuteMethodThrowsExceptionThenNothingHappens() throws TelegramApiException {
            Message message = new Message();
            Chat chat = new Chat();
            chat.setId(Long.valueOf(chatId));
            message.setChat(chat);
            when(callbackQueryMocked.getMessage()).thenReturn(message);
            doThrow(new TelegramApiException()).when(botMocked).execute(any(AnswerCallbackQuery.class));
            senderChatService.answerCallbackQuery(callbackQueryMocked);
            ArgumentCaptor<AnswerCallbackQuery> captor = ArgumentCaptor.forClass(AnswerCallbackQuery.class);
            verify(botMocked).execute(captor.capture());
            AnswerCallbackQuery answerCallbackQuery = captor.getValue();
            assertThat(answerCallbackQuery.getCallbackQueryId(), is(callbackQueryId));
            verifyNoMoreInteractions(botMocked);
        }
    }
}