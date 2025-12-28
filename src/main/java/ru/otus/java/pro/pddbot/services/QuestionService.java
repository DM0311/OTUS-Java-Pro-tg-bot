package ru.otus.java.pro.pddbot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.pro.pddbot.model.Answer;
import ru.otus.java.pro.pddbot.model.Question;
import ru.otus.java.pro.pddbot.repository.AnswerRepository;
import ru.otus.java.pro.pddbot.repository.QuestionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class QuestionService {

    private final QuestionRepository questionRepository;

    private final AnswerRepository answerRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }


    public List<Question> getRandomQuestions(int count) {
        List<Question> questions = questionRepository.findRandomQuestions(count);
        return loadAnswersForQuestions(questions);
    }

    // для статистики по вопросам
    @Transactional
    public void markAnswer(Long questionId, boolean isCorrect) {
        if (isCorrect) {
            answerRepository.incrementTimesCorrect(questionId);
        } else {
            answerRepository.incrementTimesWrong(questionId);
        }
    }

    public Optional<Question> getQuestionWithAnswers(Long questionId) {
        return questionRepository.findById(questionId).map(question -> {
            List<Answer> answers = answerRepository.findByQuestionId(questionId);
            question.setAnswers(answers);
            return question;
        });
    }

    private List<Question> loadAnswersForQuestions(List<Question> questions) {
        if (questions.isEmpty()) {
            return questions;
        }

        List<Long> questionIds = questions.stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        List<Answer> answers = answerRepository.findByQuestionIds(questionIds);

        Map<Long, List<Answer>> answersByQuestionId = answers.stream()
                .collect(Collectors.groupingBy(Answer::getQuestionId));

        for (Question question : questions) {
            List<Answer> questionAnswers = answersByQuestionId.getOrDefault(
                    question.getId(), new ArrayList<>());
            question.setAnswers(questionAnswers);
        }

        return questions;
    }
}
