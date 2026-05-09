package com.neuro.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.neuro.backend.mapper.FlashcardReviewLogMapper;
import com.neuro.backend.mapper.NoteMapper;
import com.neuro.backend.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final NoteMapper noteMapper;
    private final FlashcardReviewLogMapper flashcardReviewLogMapper;

    @Override
    public List<Map<String, Object>> getActivityHeatmap(Integer year) {
        Long userId = StpUtil.getLoginIdAsLong();
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        // 1. 查出笔记活动
        List<Map<String, Object>> noteActivities = noteMapper.getNoteActivityByYear(userId, year);
        // 2. 查出复习活动
        List<Map<String, Object>> reviewActivities = flashcardReviewLogMapper.getReviewActivityByYear(userId, year);

        // 3. 合并数据：同一天的活动次数相加
        Map<String, Integer> mergedMap = new HashMap<>();

        for (Map<String, Object> item : noteActivities) {
            String date = String.valueOf(item.get("date"));
            Long count = (Long) item.get("count");
            mergedMap.put(date, count.intValue());
        }

        for (Map<String, Object> item : reviewActivities) {
            String date = String.valueOf(item.get("date"));
            Long count = (Long) item.get("count");
            // 如果这天已经有笔记活动，就累加；否则新建
            mergedMap.merge(date, count.intValue(), Integer::sum);
        }

        // 4. 转回 List<Map> 返回给前端
        return mergedMap.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", entry.getKey());
                    map.put("count", entry.getValue());
                    return map;
                })
                .sorted((a, b) -> String.valueOf(a.get("date")).compareTo(String.valueOf(b.get("date"))))
                .toList();
    }

    @Override
    public List<Map<String, Object>> getTagDistribution() {
        Long userId = StpUtil.getLoginIdAsLong();
        return noteMapper.getTagDistribution(userId);
    }
}
