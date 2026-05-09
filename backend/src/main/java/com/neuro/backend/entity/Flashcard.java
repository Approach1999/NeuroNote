package com.neuro.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@TableName("flashcard")
public class Flashcard {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long noteId;
    private String cardHash;
    private String frontMd;
    private String backMd;
    private Integer srsInterval;
    private Integer srsRepetition;
    private Float srsEfactor;
    private LocalDate nextReviewDate;
    private Boolean isDeleted;
    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;
}
