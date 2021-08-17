package bot.handlers.scripts.helperclasses;

import convertations.conversions.Conversion;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class ReplyKeyboards {
    public static ReplyKeyboard availableConversions(List<Conversion> conversions, int maxButtonsInRow) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        int count = 0;
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (Conversion conversion : conversions) {
            if (count == maxButtonsInRow) {
                buttons.add(row);
                row = new ArrayList<>();
                count = 0;
            }
            InlineKeyboardButton button = new InlineKeyboardButton(conversion.toString());
            button.setCallbackData(conversion.toString());
            row.add(button);
            count++;
        }
        if (!row.isEmpty()) {
            buttons.add(row);
        }
        return new InlineKeyboardMarkup(buttons);
    }

    public static ReplyKeyboard availableConversions(List<Conversion> conversions) {
        return availableConversions(conversions, 2);
    }

    public static ReplyKeyboard getButtonWithText(String text) {
        return new ReplyKeyboardMarkup(
                List.of(new KeyboardRow(List.of(new KeyboardButton(text)))));
    }
}
