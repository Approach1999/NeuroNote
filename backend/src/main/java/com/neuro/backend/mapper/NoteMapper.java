package com.neuro.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuro.backend.entity.Note;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoteMapper extends BaseMapper<Note> {}
