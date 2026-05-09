package com.neuro.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.neuro.backend.dto.NoteDTO;
import com.neuro.backend.entity.Note;
import com.neuro.backend.mapper.NoteMapper;
import com.neuro.backend.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteMapper noteMapper;

    @Override
    public void createNote(NoteDTO dto) {
        Note note = new Note();
        note.setUserId(StpUtil.getLoginIdAsLong());
        note.setFolderId(1L);
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        // 👇 补上新增字段
        note.setOutlineJson(dto.getOutlineJson());
        note.setTags(dto.getTags());
        noteMapper.insert(note);
    }

    @Override
    public List<Note> getMyNotes() {
        return noteMapper.selectList(
                new QueryWrapper<Note>()
                        .eq("user_id", StpUtil.getLoginIdAsLong())
                        .eq("is_deleted", false) // 👇 【重要】过滤软删除
                        .orderByDesc("updated_at")
        );
    }

    @Override
    public void updateNote(Long id, NoteDTO dto) {
        // 1. 查老对象只做权限校验
        Note existingNote = noteMapper.selectById(id);
        if (existingNote == null || existingNote.getIsDeleted() || !existingNote.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new RuntimeException("笔记不存在或无权修改");
        }

        // 2. 关键：new 一个空对象！只塞要改的字段！
        Note updateNote = new Note();
        updateNote.setId(id);
        updateNote.setTitle(dto.getTitle());
        updateNote.setContent(dto.getContent());
        updateNote.setOutlineJson(dto.getOutlineJson());
        updateNote.setTags(dto.getTags());

        // 3. 这样生成的 SQL 只有 SET title=?, content=?, outline_json=?, tags=? WHERE id=?
        noteMapper.updateById(updateNote);
    }

    @Override
    public void deleteNote(Long id) {
        Note existingNote = noteMapper.selectById(id);
        if (existingNote == null || existingNote.getIsDeleted() || !existingNote.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new RuntimeException("笔记不存在或无权删除");
        }

        // 关键：软删除也 new 一个空对象，只改 is_deleted！
        Note updateNote = new Note();
        updateNote.setId(id);
        updateNote.setIsDeleted(true);
        noteMapper.updateById(updateNote);
    }

}
