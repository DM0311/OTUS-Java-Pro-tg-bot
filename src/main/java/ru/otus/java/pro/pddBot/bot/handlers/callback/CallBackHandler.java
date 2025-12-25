package ru.otus.java.pro.pddBot.bot.handlers.callback;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface CallBackHandler {

    public EditMessageText acceptAnswer(Long chatId, Long telegramId, String callbackData, Integer messageId);

    public SendMessage sendQuestion(Long chatId, Long telegramId, String userName, String firstName);
}
