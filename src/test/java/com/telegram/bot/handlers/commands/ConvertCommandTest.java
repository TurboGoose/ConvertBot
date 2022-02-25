package com.telegram.bot.handlers.commands;

import com.telegram.bot.chatstates.ChatStates;
import com.telegram.bot.chatstates.ChatStatesImpl;
import com.telegram.bot.handlers.scripts.Script;
import com.telegram.bot.handlers.scripts.factory.ScriptFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConvertCommandTest {
    @Mock
    TelegramLongPollingBot bot;
    @Mock
    AbsSender mockedSender;
    @Mock
    User mockedUser;
    @Mock
    Chat mockedChat;

    final String chatId = "1";
    final ChatStates states = new ChatStatesImpl(chatId);
    ConvertCommand command;

    @BeforeEach
    public void setUp() {
        when(mockedChat.getId()).thenReturn(Long.valueOf(chatId));
        command = new ConvertCommand("c", "d", bot, Script.class);
    }

    @AfterEach
    public void tearDown() {
        if (states.contains()) {
            states.remove();
        }
    }

    @Test
    public void whenCallExecuteAndScriptDoesntExistThenNewScriptCreatesAndSavesSuccessfully() {
        try (MockedStatic<ScriptFactory> factoryMockedStatic = mockStatic(ScriptFactory.class)) {
            Script mockedScript = mock(Script.class);
            factoryMockedStatic.when(() -> ScriptFactory.create(Script.class, bot, chatId)).thenReturn(mockedScript);
            assertThat(states.contains(), is(false));
            command.execute(mockedSender, mockedUser, mockedChat, new String[0]);
            factoryMockedStatic.verify(() -> ScriptFactory.create(Script.class, bot, chatId));
            assertThat(states.contains(), is(true));
            assertThat(states.get(), is(equalTo(mockedScript)));
            verify(mockedScript).start();
        }
    }

    @Test
    public void whenCallExecuteAndScriptExistsThenNothingHappens() {
        Script mockedScript = mock(Script.class);
        assertThat(states.contains(), is(false));
        states.put(mockedScript);
        assertThat(states.contains(), is(true));
        assertThat(states.get(), is(equalTo(mockedScript)));
        try (MockedStatic<ScriptFactory> factoryMockedStatic = mockStatic(ScriptFactory.class)) {
            command.execute(mockedSender, mockedUser, mockedChat, new String[0]);
            factoryMockedStatic.verify(() -> ScriptFactory.create(Script.class, bot, chatId), never());
        }
    }

    @Test
    public void whenUnknownScriptTypePassedAndCallExecuteThenScriptDoesntCreate() {
        try (MockedStatic<ScriptFactory> factoryMockedStatic = mockStatic(ScriptFactory.class)) {
            factoryMockedStatic.when(() -> ScriptFactory.create(Script.class, bot, chatId)).thenReturn(null);
            assertThat(states.contains(), is(false));
            command.execute(mockedSender, mockedUser, mockedChat, new String[0]);
            factoryMockedStatic.verify(() -> ScriptFactory.create(Script.class, bot, chatId));
            assertThat(states.contains(), is(false));
        }
    }
}