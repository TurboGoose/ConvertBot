package com.telegram.bot;

import com.telegram.bot.chatstates.ChatStates;
import com.telegram.bot.handlers.commands.ConvertCommand;
import com.telegram.bot.handlers.commands.HelpCommand;
import com.telegram.bot.handlers.commands.StartCommand;
import com.telegram.bot.handlers.scripts.ConvertScript;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Bot extends TelegramLongPollingCommandBot {
    private final ChatStates chatStates = ChatStates.getInstance();
    private final String BOT_NAME;
    private final String BOT_TOKEN;

    public Bot(String BOT_NAME, String BOT_TOKEN) {
        this.BOT_NAME = BOT_NAME;
        this.BOT_TOKEN = BOT_TOKEN;
        registerCommands();
    }

    private void registerCommands() {
        register(new StartCommand("start", "Start bot"));
        register(new HelpCommand("help", "Request help"));
        register(new ConvertCommand("convert", "Convert images to PDF", this, ConvertScript.class));
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        chatStates.update(update);
    }
}
