package com.telegram.bot.helperclasses;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ReplyKeyboardsTest {
    @Test
    public void whenGettingButtonWithText() {
        String text = "text";
        ReplyKeyboardMarkup keyboard = (ReplyKeyboardMarkup) ReplyKeyboards.getButtonWithText(text);
        assertThat(keyboard.getKeyboard().get(0).get(0).getText(), is(text));
    }

    @Test
    public void whenRemovingKeyboard() {
        ReplyKeyboardRemove keyboardRemove = (ReplyKeyboardRemove) ReplyKeyboards.removeKeyboard();
        assertThat(keyboardRemove.getRemoveKeyboard(), is(true));
    }
}