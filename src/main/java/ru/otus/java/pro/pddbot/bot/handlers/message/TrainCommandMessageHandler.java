package ru.otus.java.pro.pddbot.bot.handlers.message;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component("/train")
public class TrainCommandMessageHandler implements MessageHandler {
    @Override
    public SendMessage handle(Long chatId, Long telegramId, String userName, String firstName) {

        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        String messageText = """
                📚 Режим тренировки
                            
                В разработке:
                - Тренировка по темам
                - Работа над ошибками
                - Случайные вопросы
                            
                Пока что используйте /exam для подготовки!
                """;
        msg.setText(messageText);
        return msg;
    }
}
