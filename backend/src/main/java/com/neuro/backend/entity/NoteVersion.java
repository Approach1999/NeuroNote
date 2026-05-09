package com.neuro.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@TableName("note_version")
public class NoteVersion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long noteId;
    private Integer versionNum;
    private String content;
    private String snapshotReason;
    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;
}
