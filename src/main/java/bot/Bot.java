package bot;

import bot.processors.commands.ConvertCommand;
import bot.processors.commands.HelpCommand;
import bot.processors.commands.StartCommand;
import bot.processors.noncommands.ConvertScript;
import bot.processors.noncommands.Script;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class Bot extends TelegramLongPollingCommandBot {
    private String BOT_NAME;
    private String BOT_TOKEN;
    private final Script script;

    public Bot() {
        readBotProperties();
        this.script = new ConvertScript(this);
        List<BotCommand> commands = List.of(
                new StartCommand("start", "Start bot"),
                new HelpCommand("help", "Request help"),
                new ConvertCommand("convert", "Convert files", script)
        );
        commands.forEach(this::register);
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
        script.update(update);
    }
}
