package bot.handlers.commands;

import bot.handlers.chats.Chats;
import bot.handlers.scripts.Script;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ConvertCommand extends AbstractCommand {
    private final Class<? extends Script> scriptClass;
    private final TelegramLongPollingBot bot;
    private final Chats chats = Chats.getInstance();

    public ConvertCommand(String command, String description, TelegramLongPollingBot bot, Class<? extends Script> scriptClass) {
        super(command, description);
        this.bot = bot;
        this.scriptClass = scriptClass;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String chatId = chat.getId().toString();
        if (chats.contains(chatId) && chats.get(chatId) != null) {
            return;
        }
        Script script = createScriptInstance(chatId);
        chats.put(chatId, script);
        script.start();
    }

    private Script createScriptInstance(String chatId) {
        try {
            Constructor<?> con = scriptClass.getConstructor(TelegramLongPollingBot.class, String.class);
            return (Script) con.newInstance(bot, chatId);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException exc) {
            LOG.error("[{}] Failed instantiating Script object.", chatId, exc);
            throw new RuntimeException(exc);
        }
    }
}
