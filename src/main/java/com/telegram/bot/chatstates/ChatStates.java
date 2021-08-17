package com.telegram.bot.chatstates;

import com.telegram.bot.handlers.scripts.Script;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

public class ChatStates {
    private final Map<String, Script> chatStates = new HashMap<>();
    private static ChatStates instance;

    private ChatStates() {}

    public static ChatStates getInstance() {
        if (instance == null) {
            instance = new ChatStates();
        }
        return instance;
    }

    public void put(String chatId, Script script) {
        chatStates.put(chatId, script);
    }

    public Script get(String chatId) {
        return chatStates.get(chatId);
    }

    public boolean contains(String chatId) {
        return chatStates.containsKey(chatId);
    }

    public Script remove(String chatId) {
        return chatStates.remove(chatId);
    }

    public void update(Update update) {
        String chatId = null;
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        } else if (update.hasMessage()) {
            chatId = update.getMessage().getChatId().toString();
        }
        if (chatId != null) {
            Script script = chatStates.get(chatId);
            if (script != null) {
                script.update(update);
            }
        }
    }
}
