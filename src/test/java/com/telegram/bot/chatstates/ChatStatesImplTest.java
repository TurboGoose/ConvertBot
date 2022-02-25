package com.telegram.bot.chatstates;

import com.telegram.bot.handlers.scripts.Script;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ChatStatesImplTest {
    final String chatId = "1";
    ChatStates chatStates;

    @BeforeEach
    public void setUp() {
        chatStates = new ChatStatesImpl(chatId);
        if (chatStates.contains()) {
            chatStates.remove();
        }
    }

    @Test
    public void useContainsWhenEmpty() {
        assertThat(chatStates.contains(), is(false));
    }

    @Test
    public void useRemoveWhenEmpty() {
        assertThat(chatStates.remove(), nullValue());
    }

    @Test
    public void putThenContainsThenGetThenRemove() {
        Script script = mock(Script.class);
        chatStates.put(script);
        assertThat(chatStates.contains(), is(true));
        assertThat(chatStates.get(), is(script));
        assertThat(chatStates.contains(), is(true));
        assertThat(chatStates.remove(), is(script));
        assertThat(chatStates.contains(), is(false));
    }
}