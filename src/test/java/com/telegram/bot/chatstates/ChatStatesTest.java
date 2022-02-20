package com.telegram.bot.chatstates;

import com.telegram.bot.handlers.scripts.Script;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ChatStatesTest {
    @Test
    public void testSingletonWorking() {
        ChatStates chatStatesInstance1 = ChatStates.getInstance();
        ChatStates chatStatesInstance2 = ChatStates.getInstance();
        assertThat(chatStatesInstance1, is(chatStatesInstance2));
    }

    @Test
    public void useContainsWhenEmpty() {
        ChatStates chatStates = ChatStates.getInstance();
        assertThat(chatStates.contains("1"), is(false));
    }

    @Test
    public void useRemoveWhenEmpty() {
        ChatStates chatStates = ChatStates.getInstance();
        assertThat(chatStates.remove("1"), nullValue());
    }

    @Test
    public void putThenContainsThenGetThenRemove() {
        ChatStates chatStates = ChatStates.getInstance();
        String chatId = "1";
        Script script = mock(Script.class);

        chatStates.put(chatId, script);
        assertThat(chatStates.contains(chatId), is(true));
        assertThat(chatStates.get(chatId), is(script));
        assertThat(chatStates.contains(chatId), is(true));
        assertThat(chatStates.remove(chatId), is(script));
        assertThat(chatStates.contains(chatId), is(false));
    }
}