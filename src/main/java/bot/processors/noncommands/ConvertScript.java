package bot.processors.noncommands;

import convertations.ConverterManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class ConvertScript implements Script {
    private boolean running = false;
    private final TelegramLongPollingBot bot;
    private ConverterManager manager;


    public ConvertScript(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void update(Update update) {
        
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private ReplyKeyboard createInlineReplyKeyboard() {
        return null;
    }
}
