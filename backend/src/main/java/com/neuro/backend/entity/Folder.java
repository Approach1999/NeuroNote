package com.neuro.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@TableName("kb_folder")
public class Folder {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long parentId;

    private String name;

    private Integer sortOrder;

    // kb_folder 表只有 created_at，没有 updated_at，把那个删掉！
    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;
}
