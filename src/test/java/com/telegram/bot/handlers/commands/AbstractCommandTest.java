package com.telegram.bot.handlers.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractCommandTest {
    @Mock
    AbsSender sender;
    AbstractCommand command;
    final Long chatId = 1L;
    final String text = "message";

    @BeforeEach
    public void setUp() {
        command = new StubCommand("com", "desc");
    }

    @Test
    public void whenSendingTextReplyAndExecuteMethodCallsSuccessfully() throws TelegramApiException {
        command.sendTextReply(sender, chatId, text);
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(sender).execute(captor.capture());
        SendMessage sentMessage = captor.getValue();
        assertThat(sentMessage.getChatId(), is(chatId.toString()));
        assertThat(sentMessage.getText(), is(text));
        verifyNoMoreInteractions(sender);
    }

    @Test
    public void whenSendingTextReplyAndExecuteMethodThrowsExceptionThenNothingHappens() throws TelegramApiException {
        Throwable exc = new TelegramApiException();
        when(sender.execute(any(SendMessage.class))).thenThrow(exc);
        command.sendTextReply(sender, chatId, text);
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(sender).execute(captor.capture());
        SendMessage sentMessage = captor.getValue();
        assertThat(sentMessage.getChatId(), is(chatId.toString()));
        assertThat(sentMessage.getText(), is(text));
        verifyNoMoreInteractions(sender);
    }
}

class StubCommand extends AbstractCommand {
    public StubCommand(String command, String description) {
        super(command, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {}
}