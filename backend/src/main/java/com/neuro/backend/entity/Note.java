package com.neuro.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.apache.ibatis.type.ArrayTypeHandler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "note", autoResultMap = true)
public class Note {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long folderId;
    private String title;
    private String content;

    // PG 的 JSONB 映射为 Java 的 Map 或 List
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private List<Map<String, Object>> outlineJson;


    // PG 的 TEXT[] 映射为 Java 的 List<String>
    @TableField(typeHandler = ArrayTypeHandler.class)
    private String[] tags;

    private String contentHash = "";
    private Integer wordCount;
    private Boolean isDeleted;
    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private OffsetDateTime updatedAt;
}
