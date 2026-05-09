package com.neuro.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@TableName("note_relation")
public class NoteRelation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long noteIdA;
    private Long noteIdB;
    private String relationType;
    private Float similarityScore;
    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;
}
