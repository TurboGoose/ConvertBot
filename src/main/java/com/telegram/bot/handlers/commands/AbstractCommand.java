package com.telegram.bot.handlers.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class AbstractCommand extends BotCommand {
    protected Logger LOG = LoggerFactory.getLogger(getClass());

    public AbstractCommand(String command, String description) {
        super(command, description);
    }

    protected void setLogger(Logger logger) {
        this.LOG = logger;
    }

    public void sendTextReply(AbsSender sender, Long chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), text);
        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException exc) {
            LOG.error("Failed sending text reply \"{}\" for chat: {}", text, chatId, exc);
        }
    }
}
