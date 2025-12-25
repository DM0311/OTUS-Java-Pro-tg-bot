package ru.otus.java.pro.pddBot.bot.handlers.callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.otus.java.pro.pddBot.model.ExamSession;
import ru.otus.java.pro.pddBot.model.Question;
import ru.otus.java.pro.pddBot.model.User;
import ru.otus.java.pro.pddBot.services.ExamService;
import ru.otus.java.pro.pddBot.services.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component("begin")
public class BeginCallBackHandler implements CallBackHandler {

    private ExamService examService;
    private UserService userService;

    @Autowired
    public BeginCallBackHandler(ExamService examService, UserService userService) {
        this.examService = examService;
        this.userService = userService;
    }

    @Override
    public EditMessageText acceptAnswer(Long chatId, Long telegramId, String callbackData, Integer messageId) {

        User user = userService.findByTelegramId(telegramId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Optional<ExamSession> activeSession = examService.getActiveSession(user.getId());

        if (activeSession.isPresent()) {
            return EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .text("У вас уже есть активный экзамен! Продолжайте отвечать на вопросы.")
                    .build();
        }

        examService.startNewExam(user.getId());
        userService.updateUserState(telegramId, "TESTING_IN_PROGRESS");
        String feedback = String.format("""
                ✅ Экзамен начался! 
                У вас 20 вопросов. Удачи!
                Время начала:
                %d
                """, System.currentTimeMillis());

        return EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text(feedback)
                .build();
    }

    @Override
    public SendMessage sendQuestion(Long chatId, Long telegramId, String userName, String firstName) {

        User user = userService.findByTelegramId(telegramId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        ExamSession session = examService.getActiveSession(user.getId()).orElseThrow(
                () -> new RuntimeException("Cессия пользователя не найдена"));

        Optional<Question> questionOpt = examService.getCurrentQuestion(session.getId());

        if (questionOpt.isEmpty()) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("❌ Ошибка: вопрос не найден")
                    .build();
        }

        Question question = questionOpt.get();

        StringBuilder messageText = new StringBuilder();
        messageText.append("Вопрос ").append(session.getCurrentQuestionIndex() + 1)
                .append("/").append(session.getTotalQuestions())
                .append("\r\n");
        messageText.append(question.getText()).append("\r\n");

        // Добавляем варианты ответов
        List<String> letters = Arrays.asList("A", "B", "C", "D");
        for (int i = 0; i < Math.min(question.getAnswers().size(), 4); i++) {
            messageText.append(letters.get(i)).append(") ")
                    .append(question.getAnswers().get(i).getText())
                    .append("\n");
        }

        // Создаем inline-клавиатуру
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createAnswerButton("A", question.getId(), question.getAnswers().get(0).getId()));
        row1.add(createAnswerButton("B", question.getId(), question.getAnswers().get(1).getId()));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createAnswerButton("C", question.getId(), question.getAnswers().get(2).getId()));
        row2.add(createAnswerButton("D", question.getId(), question.getAnswers().get(3).getId()));

        rows.add(row1);
        rows.add(row2);
        keyboardMarkup.setKeyboard(rows);

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(messageText.toString())
                .replyMarkup(keyboardMarkup)
                .build();

        return message;
    }

    private InlineKeyboardButton createAnswerButton(String letter, Long questionId, Long answerId) {
        return InlineKeyboardButton.builder()
                .text(letter)
                .callbackData(String.format("answer_%d_%d", questionId, answerId))
                .build();
    }
}
