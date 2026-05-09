package com.neuro.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuro.backend.entity.FlashcardReviewLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface FlashcardReviewLogMapper extends BaseMapper<FlashcardReviewLog> {

    // 🌟 按天 Group By 统计复习次数
    @Select("SELECT DATE(created_at) AS date, COUNT(*) AS count " +
            "FROM flashcard_review_log " +
            "WHERE user_id = #{userId} AND EXTRACT(YEAR FROM created_at) = #{year} " +
            "GROUP BY DATE(created_at)")
    List<Map<String, Object>> getReviewActivityByYear(@Param("userId") Long userId, @Param("year") Integer year);
}
