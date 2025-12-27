package ru.otus.java.pro.pddbot.bot.handlers.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.java.pro.pddbot.services.UserService;

@Component("/stats")
public class StatsCommandMessageHandler implements MessageHandler {

    private UserService userService;

    @Autowired
    public StatsCommandMessageHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public SendMessage handle(Long chatId, Long telegramId, String userName, String firstName) {

        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        userService.findByTelegramId(telegramId).ifPresentOrElse(
                user -> {
                    String stats = String.format("""
                                    📊 Ваша статистика:

                                    Всего экзаменов: %d
                                    Сдано экзаменов: %d
                                    Всего вопросов: %d
                                    Правильных ответов: %d
                                    Успешность: %.1f%%

                                    """,
                            user.getTotalExams(),
                            user.getPassedExams(),
                            user.getTotalQuestionsAnswered(),
                            user.getCorrectAnswers(),
                            user.getSuccessRate()
                    );

                    msg.setText(stats);
                },
                () -> msg.setText("Вы еще не зарегистрированы. Используйте /start")
        );
        return msg;
    }
}
