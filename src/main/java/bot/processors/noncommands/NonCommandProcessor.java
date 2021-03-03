package bot.processors.noncommands;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface NonCommandProcessor {
    void processUpdate(Update update);
    void startScenario(int userId);

}
