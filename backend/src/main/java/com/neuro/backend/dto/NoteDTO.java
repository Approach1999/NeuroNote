package com.neuro.backend.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NoteDTO {
    private String title;
    private String content;
    private List<Map<String, Object>> outlineJson;
    private String[] tags; // 注意这里用 String[]，和实体类保持一致
    private String contentHash;

}
