package ru.otus.java.pro.pddbot.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.java.pro.pddbot.model.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByTelegramId(Long telegramId);

    @Modifying
    @Query("UPDATE users SET state = :state WHERE telegram_id = :telegramId")
    void updateUserState(@Param("telegramId") Long telegramId, @Param("state") String state);

    @Modifying
    @Query("UPDATE users SET total_exams = total_exams + 1, updated_at = CURRENT_TIMESTAMP WHERE id = :userId")
    void incrementTotalExams(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE users SET passed_exams = passed_exams + 1, updated_at = CURRENT_TIMESTAMP WHERE id = :userId")
    void incrementPassedExams(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE users SET total_questions_answered = total_questions_answered + 1, correct_answers = correct_answers + :increment, updated_at = CURRENT_TIMESTAMP WHERE id = :userId")
    void updateStatistics(@Param("userId") Long userId, @Param("increment") int increment);
}
