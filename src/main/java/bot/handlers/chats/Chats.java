package bot.handlers.chats;

import bot.handlers.scripts.Script;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

public class Chats {
    private final Map<String, Script> chats = new HashMap<>();
    private static Chats instance;

    private Chats() {}

    public static Chats getInstance() {
        if (instance == null) {
            instance = new Chats();
        }
        return instance;
    }

    public void put(String chatId, Script script) {
        chats.put(chatId, script);
    }

    public Script get(String chatId) {
        return chats.get(chatId);
    }

    public boolean contains(String chatId) {
        return chats.containsKey(chatId);
    }

    public void update(Update update) {
        String chatId = null;
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        } else if (update.hasMessage()) {
            chatId = update.getMessage().getChatId().toString();
        }
        if (chatId != null) {
            Script script = chats.get(chatId);
            if (script != null) {
                script.update(update);
            }
        }
    }
}
