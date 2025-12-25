package ru.otus.java.pro.pddBot.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table("questions")
@Data
@NoArgsConstructor
public class Question {

    @Id
    private Long id;

    private String text;

    @Column("image_url")
    private String imageUrl;

    private String topic;

    private String explanation;

    private Integer difficulty = 1;

    @Column("times_shown")
    private Integer timesShown = 0;

    @Column("times_correct")
    private Integer timesCorrect = 0;

    @Column("times_wrong")
    private Integer timesWrong = 0;

    @Column("is_active")
    private Boolean isActive = true;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Transient
    private List<Answer> answers = new ArrayList<>();

    @Transient
    public void incrementShown() {
        this.timesShown++;
    }

    @Transient
    public void incrementCorrect() {
        this.timesCorrect++;
    }

    @Transient
    public void incrementWrong() {
        this.timesWrong++;
    }

    @Transient
    public Double getSuccessRate() {
        if (timesShown == 0) return 0.0;
        return (timesCorrect * 100.0) / timesShown;
    }
}
