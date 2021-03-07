package bot.handlers.commands;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class HelpCommand extends AbstractCommand {

    public HelpCommand(String command, String description) {
        super(command, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String text = EmojiParser.parseToUnicode(String.format(
                "Help is already here! :sos:%n%nType /convert command, choose type of conversion and then download your file."));
        sendTextReply(absSender, chat.getId(), text);
    }
}
