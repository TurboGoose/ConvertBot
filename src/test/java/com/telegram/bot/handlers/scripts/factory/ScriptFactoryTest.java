package com.telegram.bot.handlers.scripts.factory;

import com.telegram.bot.handlers.scripts.ConvertScript;
import com.telegram.bot.handlers.scripts.Script;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class ScriptFactoryTest {
    @Mock
    TelegramLongPollingBot bot;
    String chatId = "1";

    @Test
    public void whenCreateConvertScriptThenReturnNewConvertScript() {
        assertThat(ScriptFactory.create(ConvertScript.class, bot, chatId), is(instanceOf(ConvertScript.class)));
    }

    @Test
    public void whenCreateUnknownScriptThenReturnNull() {
        assertThat(ScriptFactory.create(UnknownScript.class, bot, chatId), is(nullValue()));
    }

    static class UnknownScript implements Script {
        @Override
        public void start() {}

        @Override
        public void update(Update update) {}

        @Override
        public void stop() {}
    }
}