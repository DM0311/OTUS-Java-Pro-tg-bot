package ru.otus.java.pro.pddBot.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("exam_sessions")
@Data
@NoArgsConstructor
public class ExamSession {
    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    private String status = "IN_PROGRESS";

    @Column("started_at")
    private LocalDateTime startedAt;

    @Column("completed_at")
    private LocalDateTime completedAt;

    @Column("total_questions")
    private Integer totalQuestions;

    @Column("correct_answers")
    private Integer correctAnswers = 0;

    @Column("current_question_index")
    private Integer currentQuestionIndex = 0;

    private Boolean passed = false;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Transient
    private User user;

    @Transient
    public boolean isCompleted() {
        return "COMPLETED".equals(status) || "TIMEOUT".equals(status);
    }

    @Transient
    public void complete(boolean passed) {
        this.status = "COMPLETED";
        this.completedAt = LocalDateTime.now();
        this.passed = passed;
    }

    @Transient
    public void incrementCorrectAnswers() {
        this.correctAnswers++;
    }

    @Transient
    public void moveToNextQuestion() {
        this.currentQuestionIndex++;
    }
}
