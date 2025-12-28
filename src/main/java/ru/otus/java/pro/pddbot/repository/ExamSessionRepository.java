package ru.otus.java.pro.pddbot.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.java.pro.pddbot.model.ExamSession;

import java.util.Optional;

public interface ExamSessionRepository extends CrudRepository<ExamSession, Long> {

    @Query("SELECT * FROM exam_sessions WHERE user_id = :userId AND status = 'IN_PROGRESS' ORDER BY created_at DESC LIMIT 1")
    Optional<ExamSession> findActiveByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE exam_sessions SET status = 'COMPLETED', completed_at = CURRENT_TIMESTAMP, passed = :passed WHERE id = :sessionId")
    void completeSession(@Param("sessionId") Long sessionId, @Param("passed") boolean passed);

    @Modifying
    @Query("UPDATE exam_sessions SET correct_answers = correct_answers + 1 WHERE id = :sessionId")
    void incrementCorrectAnswers(@Param("sessionId") Long sessionId);

    @Modifying
    @Query("UPDATE exam_sessions SET current_question_index = current_question_index + 1 WHERE id = :sessionId")
    void moveToNextQuestion(@Param("sessionId") Long sessionId);
}
