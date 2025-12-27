package ru.otus.java.pro.pddbot.bot.handlers.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.otus.java.pro.pddbot.model.ExamSession;
import ru.otus.java.pro.pddbot.model.User;
import ru.otus.java.pro.pddbot.services.ExamService;
import ru.otus.java.pro.pddbot.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("/exam")
public class ExamCommandMessageHandler implements MessageHandler {



    private UserService userService;

    private ExamService examService;

    @Autowired
    public ExamCommandMessageHandler(UserService userService, ExamService examService) {
        this.userService = userService;
        this.examService = examService;
    }

    @Override
    public SendMessage handle(Long chatId, Long telegramId, String userName, String firstName) {

        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);

        Optional<User> user = userService.findByTelegramId(telegramId);
        if (!user.isPresent()) {
            msg.setText("Вы еще не зарегистрированы. Используйте /start");
            return msg;
        }

        Optional<ExamSession> activeSession = examService.getActiveSession(user.get().getId());
        if (activeSession.isPresent()) {
            msg.setText("У вас уже есть активный экзамен! Продолжайте отвечать на вопросы.");
            return msg;
        }

        String startExamText ="""
                Всего вопросов: 20
                Минимум правильных ответов 
                для сдачи: 18
                                    
                Для начала нажмите кнопку.
                """;

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("Начать")
                .callbackData("begin")
                .build());

        rows.add(row1);
        keyboardMarkup.setKeyboard(rows);
        msg.setText(startExamText);
        msg.setReplyMarkup(keyboardMarkup);
        return msg;
    }
}
