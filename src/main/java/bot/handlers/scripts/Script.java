package bot.handlers.scripts;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Script {

    void start();
    void update(Update update);
    void stop();
}
