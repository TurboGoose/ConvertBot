package bot.handlers.scripts;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Script {
    void start(String chatId);
    void update(Update update);
    void stop(String chatId);
}
