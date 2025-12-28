package ru.otus.java.pro.pddbot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.pro.pddbot.model.Answer;
import ru.otus.java.pro.pddbot.model.ExamAnswer;
import ru.otus.java.pro.pddbot.model.ExamSession;
import ru.otus.java.pro.pddbot.model.Question;
import ru.otus.java.pro.pddbot.repository.ExamAnswerRepository;
import ru.otus.java.pro.pddbot.repository.ExamSessionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ExamService {

    private static final int EXAM_QUESTIONS_COUNT = 20;

    private static final int MIN_CORRECT_ANSW = 18;
    private final ExamSessionRepository examSessionRepository;
    private final ExamAnswerRepository examAnswerRepository;
    private final UserService userService;
    private final QuestionService questionService;
    private final ConcurrentHashMap<Long, List<Question>> activeSessionQuestions = new ConcurrentHashMap<>();

    @Autowired
    public ExamService(ExamSessionRepository examSessionRepository, ExamAnswerRepository examAnswerRepository,
                       UserService userService, QuestionService questionService) {
        this.examSessionRepository = examSessionRepository;
        this.examAnswerRepository = examAnswerRepository;
        this.userService = userService;
        this.questionService = questionService;
    }

    public Optional<ExamSession> getActiveSession(Long userId) {
        return examSessionRepository.findActiveByUserId(userId);
    }

    @Transactional
    public ExamSession startNewExam(Long userId) {

        Optional<ExamSession> activeSession = examSessionRepository.findActiveByUserId(userId);
        if (activeSession.isPresent()) {
            throw new IllegalStateException("У пользователя уже есть активный экзамен");
        }

        List<Question> questions = questionService.getRandomQuestions(EXAM_QUESTIONS_COUNT);
        if (questions.size() < EXAM_QUESTIONS_COUNT) {
            throw new IllegalStateException("Недостаточно вопросов в базе данных");
        }

        ExamSession examSession = new ExamSession();
        examSession.setUserId(userId);
        examSession.setTotalQuestions(EXAM_QUESTIONS_COUNT);
        examSession.setStartedAt(LocalDateTime.now());
        examSession.setCreatedAt(LocalDateTime.now());

        ExamSession savedSession = examSessionRepository.save(examSession);

        activeSessionQuestions.put(savedSession.getId(), questions);

        log.info("Создана новая сессия экзамена: {} для пользователя {}",
                savedSession.getId(), userId);

        return savedSession;
    }

    @Transactional
    public ExamAnswer processAnswer(Long sessionId, Long userId, Long questionId, Long answerId) {
        // Получаем вопрос и проверяем ответ
        Question question = questionService.getQuestionWithAnswers(questionId)
                .orElseThrow(() -> new RuntimeException("Вопрос не найден"));

        boolean isCorrect = question.getAnswers().stream()
                .filter(answer -> answer.getId().equals(answerId))
                .findFirst()
                .map(Answer::getIsCorrect)
                .orElse(false);

        // Создаем запись об ответе
        ExamAnswer examAnswer = new ExamAnswer();
        examAnswer.setExamSessionId(sessionId);
        examAnswer.setUserId(userId);
        examAnswer.setQuestionId(questionId);
        examAnswer.setSelectedAnswerId(answerId);
        examAnswer.setIsCorrect(isCorrect);
        examAnswer.setAnsweredAt(LocalDateTime.now());
        examAnswer.setCreatedAt(LocalDateTime.now());

        ExamAnswer savedAnswer = examAnswerRepository.save(examAnswer);

        // Обновляем статистику
        if (isCorrect) {
            examSessionRepository.incrementCorrectAnswers(sessionId);
        }

        examSessionRepository.moveToNextQuestion(sessionId);
        userService.updateStatistics(userId, isCorrect);
        //
        questionService.markAnswer(questionId, isCorrect);

        // Проверяем, завершен ли экзамен
        ExamSession session = examSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Сессия не найдена"));

        if (session.getCurrentQuestionIndex() >= session.getTotalQuestions()) {
            completeExam(session);
        }

        return savedAnswer;
    }

    @Transactional
    public void completeExam(ExamSession session) {
        boolean passed = session.getCorrectAnswers() >= MIN_CORRECT_ANSW;
        examSessionRepository.completeSession(session.getId(), passed);

        // Обновляем статистику пользователя
        userService.incrementExamCount(session.getUserId(), passed);

        // Удаляем вопросы из памяти
        activeSessionQuestions.remove(session.getId());

        log.info("Экзамен {} завершен. Правильных ответов: {}/{}. {}",
                session.getId(), session.getCorrectAnswers(),
                session.getTotalQuestions(), passed ? "СДАЛ" : "НЕ СДАЛ");
    }

    public List<Question> getQuestionsForSession(Long sessionId) {
        return activeSessionQuestions.getOrDefault(sessionId, new ArrayList<>());
    }

    public Optional<Question> getCurrentQuestion(Long sessionId) {
        List<Question> questions = getQuestionsForSession(sessionId);
        return examSessionRepository.findById(sessionId).flatMap(session -> {
            if (session.getCurrentQuestionIndex() < questions.size()) {
                return Optional.of(questions.get(session.getCurrentQuestionIndex()));
            }
            return Optional.empty();
        });
    }


}
