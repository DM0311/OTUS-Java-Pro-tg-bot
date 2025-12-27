package ru.otus.java.pro.pddbot.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("answers")
@Data
@NoArgsConstructor
public class Answer {

    @Id
    private Long id;

    @Column("question_id")
    private Long questionId;

    private String text;

    private String letter; // A, B, C, D

    @Column("is_correct")
    private Boolean isCorrect = false;

    @Column("created_at")
    private LocalDateTime createdAt;
}
