package com.telegram.bot.handlers.scripts.factory;

import com.telegram.bot.chatservices.downloaders.DownloaderChatService;
import com.telegram.bot.chatservices.downloaders.DownloaderChatServiceImpl;
import com.telegram.bot.chatservices.senders.SenderChatService;
import com.telegram.bot.chatservices.senders.SenderChatServiceImpl;
import com.telegram.bot.chatstates.ChatStates;
import com.telegram.bot.chatstates.ChatStatesImpl;
import com.telegram.bot.handlers.scripts.ConvertScript;
import com.telegram.bot.handlers.scripts.Script;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class ScriptFactory {
    public static Script create(Class<? extends Script> scriptClass, TelegramLongPollingBot bot, String chatId) {
        SenderChatService senderService = new SenderChatServiceImpl(bot, chatId);
        DownloaderChatService downloaderService = new DownloaderChatServiceImpl(bot);
        ChatStates states = new ChatStatesImpl(chatId);

        if (scriptClass == ConvertScript.class) {
            return new ConvertScript(senderService, downloaderService, states);
        }
        return null;
    }
}
