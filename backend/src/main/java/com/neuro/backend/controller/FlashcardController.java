package com.neuro.backend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.neuro.backend.common.R;
import com.neuro.backend.dto.DueFlashcardVO;
import com.neuro.backend.dto.FlashcardReviewDTO;
import com.neuro.backend.service.FlashcardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
@RequiredArgsConstructor
public class FlashcardController {

    private final FlashcardService flashcardService;

    // 1. 获取今日待复习卡片
    @SaCheckLogin
    @GetMapping("/due")
    public R<List<DueFlashcardVO>> getDueCards() {
        return R.ok(flashcardService.getDueCards());
    }

    // 2. 提交复习结果 (批量)
    @SaCheckLogin
    @PostMapping("/batch-update")
    public R<Void> batchUpdateCards(@RequestBody List<FlashcardReviewDTO> dtoList) {
        flashcardService.batchUpdateCards(dtoList);
        return R.ok();
    }
}
