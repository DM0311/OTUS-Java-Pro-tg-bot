package ru.otus.java.pro.pddBot.bot.handlers.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface MessageHandler {

    public SendMessage handle(Long chatId, Long telegramId, String userName, String firstName);
}
