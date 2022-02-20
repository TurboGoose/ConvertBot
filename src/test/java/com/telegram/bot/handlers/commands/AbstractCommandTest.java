package com.telegram.bot.handlers.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractCommandTest {
    @Mock
    AbsSender sender;

    final Long chatId = 1L;
    final String text = "message";

    @Test
    public void whenSendTextReplyThenCallExecuteOnAbsSenderSuccessfully() throws TelegramApiException {
        AbstractCommand command = mock(AbstractCommand.class, InvocationOnMock::callRealMethod);
        command.sendTextReply(sender, chatId, text);
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(sender).execute(captor.capture());
        SendMessage sentMessage = captor.getValue();
        assertThat(sentMessage.getChatId(), is(chatId.toString()));
        assertThat(sentMessage.getText(), is(text));
        verifyNoMoreInteractions(sender);
    }

    @Test
    public void whenSendTextReplyThenAbsSenderThrowsException() throws TelegramApiException {
        AbstractCommand command = mock(AbstractCommand.class, InvocationOnMock::callRealMethod);
        Logger log = mock(Logger.class);
        command.setLogger(log);
        Throwable exc = new TelegramApiException();
        when(sender.execute(any(SendMessage.class))).thenThrow(exc);
        command.sendTextReply(sender, chatId, text);
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(sender).execute(captor.capture());
        SendMessage sentMessage = captor.getValue();
        assertThat(sentMessage.getChatId(), is(chatId.toString()));
        assertThat(sentMessage.getText(), is(text));
        verify(log).error("Failed sending text reply \"{}\" for chat: {}", text, chatId, exc);
        verifyNoMoreInteractions(sender);
    }
}