package ru.otus.java.pro.pddBot.bot.handlers.message;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.java.pro.pddBot.bot.handlers.message.MessageHandler;

@Component("/help")
public class HelpCommandMessageHandler implements MessageHandler {
    @Override
    public SendMessage handle(Long chatId, Long telegramId, String userName, String firstName) {

        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        String messageText = """
            📖 Помощь по командам
            
            /start - Начать работу с ботом
            /exam - Пройти экзамен (20 вопросов)
            /train - Тренировка по темам
            /stats - Показать статистику
            /help - Показать это сообщение
            
            📝 Правила экзамена:
            - 20 случайных вопросов
            - На каждый вопрос 4 варианта ответа
            - Для сдачи нужно 18+ правильных ответов
            
            Удачи на дорогах! 🚗
            """;

//        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
//
//        List<InlineKeyboardButton> row1 = new ArrayList<>();
//        row1.add(InlineKeyboardButton.builder()
//                .text("Начать работу")
//                .callbackData("/start")
//                .build());
//
//        List<InlineKeyboardButton> row2 = new ArrayList<>();
//        row2.add(InlineKeyboardButton.builder()
//                .text("Пройти экзамен")
//                .callbackData("/exam")
//                .build());
//
//        List<InlineKeyboardButton> row3 = new ArrayList<>();
//        row3.add(InlineKeyboardButton.builder()
//                .text("Тренировка")
//                .callbackData("/train")
//                .build());
//
//        List<InlineKeyboardButton> row4 = new ArrayList<>();
//        row4.add(InlineKeyboardButton.builder()
//                .text("Показать статистику")
//                .callbackData("/stats")
//                .build());
//
//        List<InlineKeyboardButton> row5 = new ArrayList<>();
//        row5.add(InlineKeyboardButton.builder()
//                .text("Помощь")
//                .callbackData("/help")
//                .build());
//
//
//        rows.add(row1);
//        rows.add(row2);
//        rows.add(row3);
//        rows.add(row4);
//        rows.add(row5);
//        keyboardMarkup.setKeyboard(rows);

         msg.setText(messageText);
         //msg.setReplyMarkup(keyboardMarkup);

        return msg;
    }
}
