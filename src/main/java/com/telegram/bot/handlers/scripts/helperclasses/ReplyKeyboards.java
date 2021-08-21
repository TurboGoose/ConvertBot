package com.telegram.bot.handlers.scripts.helperclasses;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class ReplyKeyboards {
    public static ReplyKeyboard getButtonWithText(String text) {
        return new ReplyKeyboardMarkup(
                List.of(new KeyboardRow(List.of(new KeyboardButton(text)))));
    }

    public static ReplyKeyboard removeKeyboard() {
        return new ReplyKeyboardRemove(true);
    }
}
