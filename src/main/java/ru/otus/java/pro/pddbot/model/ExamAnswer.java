package ru.otus.java.pro.pddbot.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("exam_answers")
@Data
@NoArgsConstructor
public class ExamAnswer {

    @Id
    private Long id;

    @Column("exam_session_id")
    private Long examSessionId;

    @Column("user_id")
    private Long userId;

    @Column("question_id")
    private Long questionId;

    @Column("selected_answer_id")
    private Long selectedAnswerId;

    @Column("is_correct")
    private Boolean isCorrect = false;

    @Column("answered_at")
    private LocalDateTime answeredAt;

    @Column("created_at")
    private LocalDateTime createdAt;
}
