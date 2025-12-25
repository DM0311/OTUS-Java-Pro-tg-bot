package ru.otus.java.pro.pddBot.repository;


import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.java.pro.pddBot.model.ExamAnswer;

import java.util.List;

public interface ExamAnswerRepository extends CrudRepository<ExamAnswer, Long> {
    List<ExamAnswer> findByExamSessionId(Long examSessionId);

    @Query("SELECT COUNT(*) FROM exam_answers WHERE exam_session_id = :sessionId AND is_correct = true")
    long countCorrectAnswers(@Param("sessionId") Long sessionId);

    @Query("SELECT * FROM exam_answers WHERE exam_session_id = :sessionId ORDER BY created_at")
    List<ExamAnswer> findBySessionIdOrdered(@Param("sessionId") Long sessionId);
}
