package com.neuro.backend.service;

import java.util.List;
import java.util.Map;

public interface StatsService {
    List<Map<String, Object>> getActivityHeatmap(Integer year);
    List<Map<String, Object>> getTagDistribution();
}
