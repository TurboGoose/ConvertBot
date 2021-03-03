package bot;

import bot.processors.commands.ConvertCommand;
import bot.processors.commands.HelpCommand;
import bot.processors.commands.StartCommand;
import bot.processors.noncommands.ConvertProcessor;
import bot.processors.noncommands.NonCommandProcessor;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class Bot extends TelegramLongPollingCommandBot {
    private final String BOT_NAME;
    private final String BOT_TOKEN;
    private final NonCommandProcessor processor;

    public Bot (String BOT_NAME, String BOT_TOKEN) {
        this.BOT_NAME = BOT_NAME;
        this.BOT_TOKEN = BOT_TOKEN;

        this.processor = new ConvertProcessor();

        List<BotCommand> commands = List.of(
                new StartCommand("start", "Start bot"),
                new HelpCommand("help", "Request help"),
                new ConvertCommand("convert", "Convert files", processor)
        );

        commands.forEach(this::register);
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
        processor.processUpdate(update);
    }
}
