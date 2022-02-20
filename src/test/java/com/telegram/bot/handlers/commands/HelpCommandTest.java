package com.telegram.bot.handlers.commands;

import com.telegram.bot.handlers.scripts.ConvertScript;
import com.vdurmont.emoji.EmojiParser;
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
class HelpCommandTest {
    @Mock
    AbsSender sender;
    @Mock
    User user;
    @Mock
    Chat chat;

    final Long chatId = 1L;
    final String[] strings = new String[0];
    final String text = EmojiParser.parseToUnicode(String.format(
            "Help is already here! :sos:%n%nTo convert images to PDF file type /convert command, upload your images (compressed or not) " +
                    "and then press \"Done\".%nSupported image formats:   JPG, PNG.%nYou can upload no more than %d images per one conversion.", ConvertScript.CAPACITY));


    @BeforeEach
    public void setUp() {
        when(chat.getId()).thenReturn(chatId);
    }

    @Test
    public void whenExecuteCalledThenTextSendingSuccessfully() {
        HelpCommand help = mock(HelpCommand.class);
        doCallRealMethod().when(help).execute(sender, user, chat, strings);
        help.execute(sender, user, chat, strings);
        verify(help).sendTextReply(sender, chatId, text);
    }
}