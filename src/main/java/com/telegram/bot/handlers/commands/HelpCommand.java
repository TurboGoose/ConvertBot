package com.telegram.bot.handlers.commands;

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
                "Help is already here! :sos:%n%n" +
                        "To convert document type /convert_doc, choose type of conversion and then upload your file.%n%n" +
                        "To convert images type /convert_img, choose type of conversion, upload your images (compressed or not) " +
                        "and then press \"Done\". You can upload no more than 10 images per one conversion."));
        sendTextReply(absSender, chat.getId(), text);
    }
}
