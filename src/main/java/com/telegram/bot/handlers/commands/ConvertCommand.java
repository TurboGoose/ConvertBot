package com.telegram.bot.handlers.commands;

import com.telegram.bot.chatstates.ChatStates;
import com.telegram.bot.chatstates.ChatStatesImpl;
import com.telegram.bot.handlers.scripts.Script;
import com.telegram.bot.handlers.scripts.factory.ScriptFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class ConvertCommand extends BotCommand {
    private final Class<? extends Script> scriptClass;
    private final TelegramLongPollingBot bot;

    public ConvertCommand(String command, String description, TelegramLongPollingBot bot, Class<? extends Script> scriptClass) {
        super(command, description);
        this.bot = bot;
        this.scriptClass = scriptClass;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String chatId = chat.getId().toString();
        ChatStates chatStates = new ChatStatesImpl(chatId);
        if (chatStates.contains() && chatStates.get() != null) {
            return;
        }
        Script script = ScriptFactory.create(scriptClass, bot, chatId);
        if (script != null) {
            chatStates.put(script);
            script.start();
        }
    }
}
