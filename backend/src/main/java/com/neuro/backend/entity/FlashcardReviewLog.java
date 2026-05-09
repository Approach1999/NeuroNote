package com.neuro.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@TableName("flashcard_review_log")
public class FlashcardReviewLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long flashcardId;
    private Long userId;
    private Integer rating;
    private Integer timeSpentSeconds;
    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;
}
