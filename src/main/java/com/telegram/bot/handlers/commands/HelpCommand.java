package com.telegram.bot.handlers.commands;

import com.telegram.bot.handlers.scripts.ConvertScript;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class HelpCommand extends AbstractCommand {

    public HelpCommand(String command, String description) {
        super(command, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String text = EmojiParser.parseToUnicode(String.format(
            "Help is already here! :sos:%n%nTo convert images to PDF file type /convert command, upload your images (compressed or not) " +
            "and then press \"Done\".%nSupported image formats:   JPG, PNG.%nYou can upload no more than %d images per one conversion.", ConvertScript.CAPACITY));
        sendTextReply(absSender, chat.getId(), text);
    }
}
