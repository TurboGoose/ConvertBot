package com.telegram.bot.chatstates;

import com.telegram.bot.handlers.scripts.Script;

import java.util.HashMap;
import java.util.Map;

public class ChatStatesImpl implements ChatStates {
    private static final Map<String, Script> chatStates = new HashMap<>();
    private final String chatId;

    public ChatStatesImpl(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public void put(Script script) {
        chatStates.put(chatId, script);
    }

    @Override
    public Script get() {
        return chatStates.get(chatId);
    }

    @Override
    public boolean contains() {
        return chatStates.containsKey(chatId);
    }

    @Override
    public Script remove() {
        return chatStates.remove(chatId);
    }
}
