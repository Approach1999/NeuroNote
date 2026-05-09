package com.neuro.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neuro.backend.dto.DueFlashcardVO;
import com.neuro.backend.dto.FlashcardReviewDTO;
import com.neuro.backend.entity.Flashcard;
import com.neuro.backend.entity.FlashcardReviewLog;
import com.neuro.backend.exception.CustomException;
import com.neuro.backend.mapper.FlashcardMapper;
import com.neuro.backend.mapper.FlashcardReviewLogMapper;
import com.neuro.backend.service.FlashcardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlashcardServiceImpl implements FlashcardService {

    private final FlashcardMapper flashcardMapper;
    private final FlashcardReviewLogMapper flashcardReviewLogMapper;

    @Override
    public List<DueFlashcardVO> getDueCards() {
        // 1. 查出 next_review_date <= 今天 的未删除卡片
        LambdaQueryWrapper<Flashcard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Flashcard::getUserId, StpUtil.getLoginIdAsLong())
                .eq(Flashcard::getIsDeleted, false)
                .le(Flashcard::getNextReviewDate, LocalDate.now()); // le: less than or equal

        List<Flashcard> cards = flashcardMapper.selectList(wrapper);

        // 2. 组装 VO，把平铺的 srs 字段包装进 state 对象
        List<DueFlashcardVO> voList = new ArrayList<>();
        for (Flashcard card : cards) {
            DueFlashcardVO vo = new DueFlashcardVO();
            vo.setId(card.getId());
            vo.setFrontMd(card.getFrontMd());
            vo.setBackMd(card.getBackMd());

            DueFlashcardVO.SrsState state = new DueFlashcardVO.SrsState();
            state.setInterval(card.getSrsInterval());
            state.setRepetition(card.getSrsRepetition());
            state.setEfactor(card.getSrsEfactor());
            vo.setState(state);

            voList.add(vo);
        }
        return voList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 🌟 开启批量事务！要么全成功，要么全失败
    public void batchUpdateCards(List<FlashcardReviewDTO> dtoList) {
        Long userId = StpUtil.getLoginIdAsLong();

        for (FlashcardReviewDTO dto : dtoList) {
            // 1. 安全校验
            Flashcard card = flashcardMapper.selectById(dto.getId());
            if (card == null || card.getIsDeleted() || !card.getUserId().equals(userId)) {
                throw new CustomException("卡片不存在或无权操作");
            }

            // 2. 🌟 后端无脑存库：直接用前端传来的 new_state 覆盖数据库
            Flashcard updateCard = new Flashcard();
            updateCard.setId(dto.getId());
            updateCard.setSrsInterval(dto.getNew_state().getInterval());
            updateCard.setSrsRepetition(dto.getNew_state().getRepetition());
            updateCard.setSrsEfactor(dto.getNew_state().getEfactor());
            updateCard.setNextReviewDate(dto.getNew_state().getNext_review_date());
            flashcardMapper.updateById(updateCard);

            // 3. 插入复习日志，供后续 ECharts 看板使用
            FlashcardReviewLog log = new FlashcardReviewLog();
            log.setFlashcardId(dto.getId());
            log.setUserId(userId);
            log.setRating(dto.getRating());
            log.setTimeSpentSeconds(0); // 如果前端以后加了计时功能，可以传过来
            flashcardReviewLogMapper.insert(log);
        }
    }
}
