package com.neuro.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuro.backend.entity.Note;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface NoteMapper extends BaseMapper<Note> {

    // 🌟 PG 降维打击：利用 UNNEST 展开数组，统计 Top 6 标签
    @Select("SELECT unnest(tags) AS tag, COUNT(*) AS count " +
            "FROM note " +
            "WHERE user_id = #{userId} AND is_deleted = false " +
            "GROUP BY tag " +
            "ORDER BY count DESC " +
            "LIMIT 6")
    List<Map<String, Object>> getTagDistribution(@Param("userId") Long userId);

    // 🌟 按天 Group By 统计用户新增笔记字数 (如果未来有字数统计的话)
    // 这里我们暂时按笔记数量统计，或者可以按天统计创建笔记数
    @Select("SELECT DATE(created_at) AS date, COUNT(*) AS count " +
            "FROM note " +
            "WHERE user_id = #{userId} AND is_deleted = false AND EXTRACT(YEAR FROM created_at) = #{year} " +
            "GROUP BY DATE(created_at)")
    List<Map<String, Object>> getNoteActivityByYear(@Param("userId") Long userId, @Param("year") Integer year);
}
