package bot;

import bot.handlers.commands.ConvertCommand;
import bot.handlers.commands.HelpCommand;
import bot.handlers.commands.StartCommand;
import bot.handlers.scripts.ConvertScript;
import bot.handlers.scripts.Script;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Bot extends TelegramLongPollingCommandBot {
    private String BOT_NAME;
    private String BOT_TOKEN;
    private final List<Script> scripts = new ArrayList<>();

    public Bot() {
        readBotProperties();
        register(new StartCommand("start", "Start bot"));
        register(new HelpCommand("help", "Request help"));
        Script convertScript = new ConvertScript(this);
        register(new ConvertCommand("convert", "Convert files", convertScript));
        scripts.add(convertScript);
    }

    private void readBotProperties() {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("bot.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            BOT_NAME = properties.getProperty("BOT_NAME");
            BOT_TOKEN = properties.getProperty("BOT_TOKEN");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        scripts.forEach(s -> s.update(update));
    }
}
