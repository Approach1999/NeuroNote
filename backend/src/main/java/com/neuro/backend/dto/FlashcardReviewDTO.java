package com.neuro.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FlashcardReviewDTO {
    private Long id;
    private Integer rating;

    // 🌟 核心亮点：前端 Wasm 算好的新状态直接传回来，后端无脑存库！
    private NewState new_state;

    @Data
    public static class NewState {
        private Integer interval;
        private Integer repetition;
        private Float efactor;
        private LocalDate next_review_date;
    }
}
