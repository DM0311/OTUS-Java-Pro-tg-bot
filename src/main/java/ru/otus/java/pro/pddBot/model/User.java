package ru.otus.java.pro.pddBot.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("users")
@Data
@NoArgsConstructor
public class User {

    @Id
    private Long id;

    @Column("telegram_id")
    private Long telegramId;

    @Column("chat_id")
    private Long chatId;

    private String username;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    private String state = "MAIN_MENU";

    @Column("total_exams")
    private Integer totalExams = 0;

    @Column("passed_exams")
    private Integer passedExams = 0;

    @Column("total_questions_answered")
    private Integer totalQuestionsAnswered = 0;

    @Column("correct_answers")
    private Integer correctAnswers = 0;

    @Column("is_active")
    private Boolean isActive = true;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Transient
    public Double getSuccessRate() {
        if (totalQuestionsAnswered == 0) return 0.0;
        return (correctAnswers * 100.0) / totalQuestionsAnswered;
    }

    @Transient
    public void incrementQuestionsAnswered(boolean correct) {
        this.totalQuestionsAnswered++;
        if (correct) {
            this.correctAnswers++;
        }
    }
}
