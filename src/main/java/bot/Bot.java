package bot;

import bot.chatstates.ChatStates;
import bot.handlers.commands.ConvertCommand;
import bot.handlers.commands.HelpCommand;
import bot.handlers.commands.StartCommand;
import bot.handlers.scripts.ConvertDocScript;
import bot.handlers.scripts.ConvertImgScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Bot extends TelegramLongPollingCommandBot {
    private static final Logger LOG = LoggerFactory.getLogger(Bot.class.getName());
    private final ChatStates chatStates = ChatStates.getInstance();
    private String BOT_NAME;
    private String BOT_TOKEN;

    public Bot() {
        readBotProperties();
        register(new StartCommand("start", "Start bot"));
        register(new HelpCommand("help", "Request help"));
        register(new ConvertCommand("convert_doc", "Convert documents", this, ConvertDocScript.class));
        register(new ConvertCommand("convert_img", "Convert images", this, ConvertImgScript.class));
    }

    private void readBotProperties() {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("bot.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            BOT_NAME = properties.getProperty("BOT_NAME");
            BOT_TOKEN = properties.getProperty("BOT_TOKEN");
        } catch (IOException exc) {
            LOG.error("Error during reading bot.properties file", exc);
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
        chatStates.update(update);
    }
}
