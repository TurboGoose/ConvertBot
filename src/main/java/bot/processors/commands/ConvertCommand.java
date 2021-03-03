package bot.processors.commands;

import bot.processors.noncommands.NonCommandProcessor;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class ConvertCommand extends AbstractCommand {
    private final NonCommandProcessor processor;

    public ConvertCommand(String command, String description, NonCommandProcessor processor) {
        super(command, description);
        this.processor = processor;
    }


    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        
    }
}
