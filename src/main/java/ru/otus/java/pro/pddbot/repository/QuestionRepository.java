package ru.otus.java.pro.pddbot.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.java.pro.pddbot.model.Question;

import java.util.List;

public interface QuestionRepository extends CrudRepository<Question, Long> {

    @Query("SELECT * FROM questions WHERE is_active = true ORDER BY RANDOM() LIMIT :limit")
    List<Question> findRandomQuestions(@Param("limit") int limit);

}
