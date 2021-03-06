package bot.processors.scripts;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Script {
    void start(Chat chat);
    void update(Update update);
    void stop();
}
