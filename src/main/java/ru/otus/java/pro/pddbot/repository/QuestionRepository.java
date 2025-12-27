package ru.otus.java.pro.pddbot.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.java.pro.pddbot.model.Question;

import java.util.List;

public interface QuestionRepository extends CrudRepository<Question, Long> {

    List<Question> findByTopic(String topic);

    List<Question> findByIsActiveTrue();

    @Query("SELECT * FROM questions WHERE is_active = true AND topic = :topic ORDER BY RANDOM() LIMIT :limit")
    List<Question> findRandomByTopic(@Param("topic") String topic, @Param("limit") int limit);

    @Query("SELECT * FROM questions WHERE is_active = true ORDER BY RANDOM() LIMIT :limit")
    List<Question> findRandomQuestions(@Param("limit") int limit);

    @Query("SELECT COUNT(*) FROM questions WHERE is_active = true")
    long countActiveQuestions();

    @Query("SELECT * FROM questions WHERE id IN (:ids)")
    List<Question> findByIds(@Param("ids") List<Long> ids);

}
