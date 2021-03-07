package bot.handlers.commands;

import bot.handlers.scripts.Script;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class ConvertCommand extends AbstractCommand {
    private final Script script;

    public ConvertCommand(String command, String description, Script script) {
        super(command, description);
        this.script = script;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        script.start(chat.getId().toString());
    }
}
