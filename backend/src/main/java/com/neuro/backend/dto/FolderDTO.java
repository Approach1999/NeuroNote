package com.neuro.backend.dto;

import lombok.Data;

@Data
public class FolderDTO {
    // 新建或修改时，前端只需要传这三个核心字段
    private Long parentId;
    private String name;
    private Integer sortOrder;
}
