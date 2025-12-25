package ru.otus.java.pro.pddBot.bot;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.otus.java.pro.pddBot.model.ExamSession;
import ru.otus.java.pro.pddBot.model.Question;
import ru.otus.java.pro.pddBot.model.User;
import ru.otus.java.pro.pddBot.services.ExamService;
import ru.otus.java.pro.pddBot.services.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
//extends TelegramLongPollingBot
public class PddTelegramBot  {
//    @Value("${telegram.bot.token}")
//    private String botToken;
//
//    @Value("${telegram.bot.username}")
//    private String botUsername;
//
//    private final UserService userService;
//    private final ExamService examService;
//
//    public PddTelegramBot(UserService userService, ExamService examService) {
//        this.userService = userService;
//        this.examService = examService;
//    }
//
//    @PostConstruct
//    public void init() {
//        log.info("Telegram LongPolling бот инициализирован: {}", botUsername);
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//        try {
//            if (update.hasMessage() && update.getMessage().hasText()) {
//                handleMessage(update.getMessage());
//            } else if (update.hasCallbackQuery()) {
//                handleCallback(update.getCallbackQuery());
//            }
//        } catch (Exception e) {
//            log.error("Ошибка обработки обновления", e);
//        }
//    }
//
//    private void handleMessage(org.telegram.telegrambots.meta.api.objects.Message message) {
//        Long chatId = message.getChatId();
//        String text = message.getText();
//        Long telegramId = message.getFrom().getId();
//        String username = message.getFrom().getUserName();
//        String firstName = message.getFrom().getFirstName();
//
//        log.debug("Сообщение от {} ({}): {}", telegramId, username, text);
//
//        if (text.startsWith("/")) {
//            handleCommand(chatId, telegramId, text, username, firstName);
//        } else {
//            handleText(chatId, telegramId, text);
//        }
//    }
//
//    private void handleCommand(Long chatId, Long telegramId, String command,
//                               String username, String firstName) {
//        switch (command) {
//            case "/start":
//                handleStartCommand(chatId, telegramId, username, firstName);
//                break;
//
//            case "/exam":
//                handleExamCommand(chatId, telegramId);
//                break;
//
//            case "/train":
//                handleTrainCommand(chatId);
//                break;
//
//            case "/stats":
//                handleStatsCommand(chatId, telegramId);
//                break;
//
//            case "/help":
//                handleHelpCommand(chatId);
//                break;
//
//            default:
//                sendMessage(chatId, "Неизвестная команда. Используйте /help для списка команд.");
//        }
//    }
//
//    private void handleStartCommand(Long chatId, Long telegramId, String username, String firstName) {
//        // Создаем или получаем пользователя
////        User user = userService.getOrCreateUser(telegramId, chatId, username, firstName);
////
//        String welcomeMessage = String.format("""
//            🚗 Привет, %s!
//
//            Я бот для подготовки к экзамену ПДД.
//
//            📚 Доступные команды:
//            /exam - Начать экзамен (20 вопросов)
//            /train - Тренировка по темам
//            /stats - Ваша статистика
//            /help - Помощь
//
//            Удачи в подготовке! 🎯
//            """, firstName);
//
//        sendMessage(chatId, welcomeMessage);
//    }
//
//    private void handleExamCommand(Long chatId, Long telegramId) {
//        try {
//            // Получаем пользователя
////            User user = userService.findByTelegramId(telegramId)
////                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
////
////            // Проверяем активный экзамен
////            Optional<ExamSession> activeSession = examService.getActiveSession(user.getId());
////            if (activeSession.isPresent()) {
////                sendMessage(chatId, "У вас уже есть активный экзамен! Продолжайте отвечать на вопросы.");
////                return;
////            }
////
////            // Создаем новый экзамен
////            ExamSession session = examService.startNewExam(user.getId());
////
////            // Отправляем первый вопрос
////            sendQuestion(chatId, session);
////
////            sendMessage(chatId, "✅ Экзамен начался! У вас 20 вопросов. Удачи!");
////
////        } catch (Exception e) {
////            log.error("Ошибка запуска экзамена", e);
////            sendMessage(chatId, "❌ Ошибка запуска экзамена. Попробуйте позже.");
////        }
//    }
//
//    private void sendQuestion(Long chatId, ExamSession session) {
//        Optional<Question> questionOpt = examService.getCurrentQuestion(session.getId());
//
//        if (questionOpt.isEmpty()) {
//            sendMessage(chatId, "❌ Ошибка: вопрос не найден");
//            return;
//        }
//
//        Question question = questionOpt.get();
//
//        StringBuilder messageText = new StringBuilder();
//        messageText.append("Вопрос ").append(session.getCurrentQuestionIndex() + 1)
//                .append("/").append(session.getTotalQuestions())
//                .append("\n\n");
//        messageText.append(question.getText()).append("\n\n");
//
//        // Добавляем варианты ответов
//        List<String> letters = Arrays.asList("A", "B", "C", "D");
//        for (int i = 0; i < Math.min(question.getAnswers().size(), 4); i++) {
//            messageText.append(letters.get(i)).append(") ")
//                    .append(question.getAnswers().get(i).getText())
//                    .append("\n");
//        }
//
//        // Создаем inline-клавиатуру
//        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
//
//        List<InlineKeyboardButton> row1 = new ArrayList<>();
//        row1.add(createAnswerButton("A", 0, question.getId(), question.getAnswers().get(0).getId()));
//        row1.add(createAnswerButton("B", 1, question.getId(), question.getAnswers().get(1).getId()));
//
//        List<InlineKeyboardButton> row2 = new ArrayList<>();
//        row2.add(createAnswerButton("C", 2, question.getId(), question.getAnswers().get(2).getId()));
//        row2.add(createAnswerButton("D", 3, question.getId(), question.getAnswers().get(3).getId()));
//
//        rows.add(row1);
//        rows.add(row2);
//        keyboardMarkup.setKeyboard(rows);
//
//        SendMessage message = SendMessage.builder()
//                .chatId(chatId.toString())
//                .text(messageText.toString())
//                .replyMarkup(keyboardMarkup)
//                .build();
//
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            log.error("Ошибка отправки вопроса", e);
//        }
//    }
//
//    private InlineKeyboardButton createAnswerButton(String letter, int index, Long questionId, Long answerId) {
//        return InlineKeyboardButton.builder()
//                .text(letter)
//                .callbackData(String.format("answer_%d_%d", questionId, answerId))
//                .build();
//    }
//
//    private void handleCallback(org.telegram.telegrambots.meta.api.objects.CallbackQuery callbackQuery) {
//        Long chatId = callbackQuery.getMessage().getChatId();
//        Long telegramId = callbackQuery.getFrom().getId();
//        String callbackData = callbackQuery.getData();
//        Integer messageId = callbackQuery.getMessage().getMessageId();
//
//        log.debug("Callback от {}: {}", telegramId, callbackData);
//
//        if (callbackData.startsWith("answer_")) {
//            handleAnswerCallback(chatId, telegramId, callbackData, messageId);
//        }
//    }
//
//    private void handleAnswerCallback(Long chatId, Long telegramId, String callbackData, Integer messageId) {
//        try {
//            // Парсим callback данные: answer_questionId_answerId
//            String[] parts = callbackData.split("_");
//            if (parts.length < 3) return;
//
//            Long questionId = Long.parseLong(parts[1]);
//            Long answerId = Long.parseLong(parts[2]);
//
//            // Получаем пользователя и активный экзамен
//            User user = userService.findByTelegramId(telegramId)
//                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
//
//            ExamSession session = examService.getActiveSession(user.getId())
//                    .orElseThrow(() -> new RuntimeException("Активный экзамен не найден"));
//
//            // Обрабатываем ответ
//            examService.processAnswer(session.getId(), user.getId(), questionId, answerId);
//
//            // Обновляем сообщение с обратной связью
//            String feedback = "✅ Ответ принят!";
//            EditMessageText editMessage = EditMessageText.builder()
//                    .chatId(chatId.toString())
//                    .messageId(messageId)
//                    .text(feedback)
//                    .build();
//
//            execute(editMessage);
//
//            // Пауза перед следующим вопросом
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//
//            // Проверяем, завершен ли экзамен
//            ExamSession updatedSession = examService.getActiveSession(user.getId()).orElse(null);
//            if (updatedSession == null || updatedSession.isCompleted()) {
//                sendExamResults(chatId, session);
//            } else {
//                sendQuestion(chatId, updatedSession);
//            }
//
//        } catch (Exception e) {
//            log.error("Ошибка обработки ответа", e);
//            sendMessage(chatId, "❌ Ошибка обработки ответа. Попробуйте еще раз.");
//        }
//    }
//
//    private void sendExamResults(Long chatId, ExamSession session) {
////        String resultMessage = String.format("""
////            🏁 Экзамен завершен!
////
////            📊 Результаты:
////            Правильных ответов: %d из %d
////            Процент правильных: %.1f%%
////
////            %s
////
////            %s
////
////            Используйте /exam для нового экзамена или /stats для статистики.
////            """,
////                session.getCorrectAnswers(),
////                session.getTotalQuestions(),
////                (session.getCorrectAnswers() * 100.0) / session.getTotalQuestions(),
////                session.getPassed() ? "✅ ЭКЗАМЕН СДАН!" : "❌ ЭКЗАМЕН НЕ СДАЛ",
////                session.getPassed() ? "Поздравляем с успешной сдачей! 🎉" :
////                        "Попробуйте еще раз, для сдачи нужно 18 правильных ответов. 📚"
////        );
////
////        sendMessage(chatId, resultMessage);
//    }
//
//    private void handleText(Long chatId, Long telegramId, String text) {
//        // Обработка простого текста
//        if (text.equalsIgnoreCase("привет") || text.equalsIgnoreCase("hi")) {
//            sendMessage(chatId, "Привет! Используйте /start для начала работы.");
//        } else {
//            sendMessage(chatId, "Я не понял ваш запрос. Используйте /help для списка команд.");
//        }
//    }
//
//    private void sendMessage(Long chatId, String text) {
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId.toString());
//        message.setText(text);
//
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            log.error("Ошибка отправки сообщения в чат {}", chatId, e);
//        }
//    }

}
