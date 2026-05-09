package com.neuro.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@TableName("kb_folder")
public class KbFolder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long parentId;
    private String name;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;
}
