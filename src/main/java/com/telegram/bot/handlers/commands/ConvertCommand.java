package com.telegram.bot.handlers.commands;

import com.telegram.bot.chatstates.ChatStates;
import com.telegram.bot.handlers.scripts.Script;
import com.telegram.bot.handlers.scripts.factory.ScriptFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class ConvertCommand extends AbstractCommand {
    private final Class<? extends Script> scriptClass;
    private final TelegramLongPollingBot bot;
    private final ChatStates chatStates = ChatStates.getInstance();

    public ConvertCommand(String command, String description, TelegramLongPollingBot bot, Class<? extends Script> scriptClass) {
        super(command, description);
        this.bot = bot;
        this.scriptClass = scriptClass;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String chatId = chat.getId().toString();
        if (chatStates.contains(chatId) && chatStates.get(chatId) != null) {
            return;
        }
        Script script = ScriptFactory.create(scriptClass, bot, chatId);
        if (script != null) {
            chatStates.put(chatId, script);
            script.start();
        }
    }
}
