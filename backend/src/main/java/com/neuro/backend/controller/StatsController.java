package com.neuro.backend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.neuro.backend.common.R;
import com.neuro.backend.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    // 1. 获取学习热力图数据
    @SaCheckLogin
    @GetMapping("/activity-heatmap")
    public R<List<Map<String, Object>>> getActivityHeatmap(
            @RequestParam(required = false) Integer year) { // 可选参数，默认今年
        return R.ok(statsService.getActivityHeatmap(year));
    }

    // 2. 获取知识分布雷达图数据 (PG UNNEST 降维打击)
    @SaCheckLogin
    @GetMapping("/tag-distribution")
    public R<List<Map<String, Object>>> getTagDistribution() {
        return R.ok(statsService.getTagDistribution());
    }
}
