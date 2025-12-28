package ru.otus.java.pro.pddbot.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.java.pro.pddbot.model.Answer;

import java.util.List;

public interface AnswerRepository extends CrudRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long questionId);

    @Query("SELECT * FROM answers WHERE question_id IN (:questionIds) ORDER BY question_id, letter")
    List<Answer> findByQuestionIds(@Param("questionIds") List<Long> questionIds);

    @Modifying
    @Query("UPDATE questions SET times_correct = times_correct + 1 WHERE id = :questionId")
    void incrementTimesCorrect(@Param("questionId") Long questionId);

    @Modifying
    @Query("UPDATE questions SET times_wrong = times_wrong + 1 WHERE id = :questionId")
    void incrementTimesWrong(@Param("questionId") Long questionId);
}
