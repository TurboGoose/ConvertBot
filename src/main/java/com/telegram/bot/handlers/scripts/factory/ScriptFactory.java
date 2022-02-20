package com.telegram.bot.handlers.scripts.factory;

import com.telegram.bot.handlers.scripts.ConvertScript;
import com.telegram.bot.handlers.scripts.Script;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class ScriptFactory {
    public static Script create(Class<? extends Script> scriptClass, TelegramLongPollingBot bot, String chatId) {
        if (scriptClass == ConvertScript.class) {
            return new ConvertScript(bot, chatId);
        }
        return null;
    }
}
