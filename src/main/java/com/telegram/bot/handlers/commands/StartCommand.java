package com.telegram.bot.handlers.commands;

import com.telegram.bot.chatservices.senders.SenderChatService;
import com.telegram.bot.chatservices.senders.SenderChatServiceImpl;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class StartCommand extends BotCommand {

    public StartCommand(String command, String description) {
        super(command, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        SenderChatService senderChatService = new SenderChatServiceImpl(absSender, chat.getId().toString());
        String text = EmojiParser.parseToUnicode(String.format(
                "Hello :wave:%nMy name is ConvertBot.%nI can convert your images to one PDF file.%n%nType /convert command to start the conversion dialog."));
        senderChatService.sendTextReply(text);
    }
}
