package com.neuro.backend.service;

import com.neuro.backend.dto.DueFlashcardVO;
import com.neuro.backend.dto.FlashcardReviewDTO;

import java.util.List;

public interface FlashcardService {
    List<DueFlashcardVO> getDueCards();
    void batchUpdateCards(List<FlashcardReviewDTO> dtoList);
}
