package com.telegram.bot;

import com.telegram.bot.chatstates.ChatStatesImpl;
import com.telegram.bot.handlers.commands.ConvertCommand;
import com.telegram.bot.handlers.commands.HelpCommand;
import com.telegram.bot.handlers.commands.StartCommand;
import com.telegram.bot.handlers.scripts.ConvertScript;
import com.telegram.bot.handlers.scripts.Script;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Bot extends TelegramLongPollingCommandBot {
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
        String chatId = null;
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        } else if (update.hasMessage()) {
            chatId = update.getMessage().getChatId().toString();
        }
        if (chatId != null) {
            Script script = new ChatStatesImpl(chatId).get();
            if (script != null) {
                script.update(update);
            }
        }
    }
}
