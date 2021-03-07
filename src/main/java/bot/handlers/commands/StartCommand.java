package bot.handlers.commands;

import com.vdurmont.emoji.EmojiParser;
import convertations.conversions.AvailableConversions;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class StartCommand extends AbstractCommand {

    public StartCommand(String command, String description) {
        super(command, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        StringBuilder builder = new StringBuilder(EmojiParser.parseToUnicode(String.format(
                "Hello :wave:%nMy name is ConvertBot and I can convert your files :page_facing_up:%n%nSupported conversions:%n")));
        AvailableConversions.getAvailable().forEach(c -> builder.append(c.toString()).append(System.lineSeparator()));
        builder.append(System.lineSeparator()).append(
                "Type /convert command to start the conversion dialog.");
        sendTextReply(absSender, chat.getId(), builder.toString());
    }
}
