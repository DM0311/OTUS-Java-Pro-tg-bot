package ru.otus.java.pro.pddbot.bot.handlers.callback;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface CallBackHandler {

    public EditMessageWrapper acceptAnswer(Long chatId, Long telegramId, String callbackData, Integer messageId);

    public SendMessage sendQuestion(Long chatId, Long telegramId, String userName, String firstName);
}
