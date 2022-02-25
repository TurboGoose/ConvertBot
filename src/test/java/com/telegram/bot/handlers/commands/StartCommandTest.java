package com.telegram.bot.handlers.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StartCommandTest {
    @Mock
    AbsSender sender;
    @Mock
    User user;
    @Mock
    Chat chat;
    StartCommand startCommand = new StartCommand("s", "d");
    final String[] strings = new String[0];

    @Test
    public void whenExecuteCalledThenTextSendingSuccessfully() throws TelegramApiException {
        startCommand.execute(sender, user, chat, strings);
        verify(sender).execute(any(SendMessage.class));
    }
}