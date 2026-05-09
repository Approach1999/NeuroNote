package com.neuro.backend.dto;

import lombok.Data;

@Data
public class DueFlashcardVO {
    private Long id;
    private String frontMd;
    private String backMd;

    // 🌟 核心亮点：将数据库平铺的 srs 字段包装成 state 对象传给前端 Wasm
    private SrsState state;

    @Data
    public static class SrsState {
        private Integer interval;
        private Integer repetition;
        private Float efactor;
    }
}
