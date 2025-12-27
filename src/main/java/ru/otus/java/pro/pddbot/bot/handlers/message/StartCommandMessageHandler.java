package ru.otus.java.pro.pddbot.bot.handlers.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.java.pro.pddbot.services.UserService;

@Component("/start")
public class StartCommandMessageHandler implements MessageHandler {

    private UserService userService;

    @Autowired
    public StartCommandMessageHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public SendMessage handle(Long chatId, Long telegramId, String userName, String firstName) {

        userService.getOrCreateUser(telegramId, chatId, userName, firstName);

        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        String messageText = String.format("""
                🚗 Привет, %s!
                            
                Я бот для подготовки к экзамену ПДД.
                            
                📚 Доступные команды:
                /exam - Начать экзамен (20 вопросов)
                /train - Тренировка по темам
                /stats - Ваша статистика
                /help - Помощь
                            
                Удачи в подготовке! 🎯
                """, firstName);
        msg.setText(messageText);
        return msg;
    }
}
