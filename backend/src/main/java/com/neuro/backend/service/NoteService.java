package com.neuro.backend.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuro.backend.dto.NoteDTO;
import com.neuro.backend.dto.RelationDTO;
import com.neuro.backend.entity.Note;
import com.neuro.backend.entity.NoteVersion;

import java.util.List;

public interface NoteService {
    void createNote(NoteDTO dto);
    Page<Note> getMyNotes(Long folderId, int pageNum, int pageSize);
    void updateNote(Long id, NoteDTO dto);
    void deleteNote(Long id);
    Page<Note> searchNotes(String tag, String outlineKeyword, int pageNum, int pageSize);
    Note getNoteDetail(Long id);
    Page<Note> listByFolder(Long folderId, Integer page, Integer size);
    // 获取笔记的历史版本列表
    List<NoteVersion> getNoteVersions(Long noteId);
    // 回滚到指定版本
    void rollbackNote(Long noteId, Long versionId);
    void addRelation(RelationDTO dto);

}
