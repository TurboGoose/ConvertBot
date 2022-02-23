package com.telegram.bot.handlers.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartCommandTest {
    @Mock
    AbsSender sender;
    @Mock
    User user;
    @Mock
    Chat chat;

    final Long chatId = 1L;
    final String[] strings = new String[0];

    @BeforeEach
    public void setUp() {
        when(chat.getId()).thenReturn(chatId);
    }

    @Test
    public void whenExecuteCalledThenTextSendingSuccessfully() {
        StartCommand start = mock(StartCommand.class);
        doCallRealMethod().when(start).execute(sender, user, chat, strings);
        start.execute(sender, user, chat, strings);
        verify(start).sendTextReply(eq(sender), eq(chatId), anyString());
    }
}