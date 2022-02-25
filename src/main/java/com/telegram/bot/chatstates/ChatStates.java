package com.telegram.bot.chatstates;

import com.telegram.bot.handlers.scripts.Script;

public interface ChatStates {
    void put(Script script);

    Script get();

    boolean contains();

    Script remove();
}
