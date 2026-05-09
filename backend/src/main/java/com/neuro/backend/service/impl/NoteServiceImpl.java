package com.neuro.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuro.backend.dto.NoteDTO;
import com.neuro.backend.entity.Note;
import com.neuro.backend.entity.NoteRelation;
import com.neuro.backend.entity.NoteVersion;
import com.neuro.backend.exception.CustomException;
import com.neuro.backend.mapper.NoteMapper;
import com.neuro.backend.mapper.NoteRelationMapper;
import com.neuro.backend.mapper.NoteVersionMapper;
import com.neuro.backend.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.neuro.backend.dto.RelationDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private NoteVersionMapper noteVersionMapper;
    @Autowired
    private NoteRelationMapper noteRelationMapper;

    @Override
    public void createNote(NoteDTO dto) {
        Note note = new Note();
        note.setUserId(StpUtil.getLoginIdAsLong());
        note.setFolderId(1L);
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setOutlineJson(dto.getOutlineJson());
        note.setTags(dto.getTags());
        noteMapper.insert(note);
    }

    @Override
    public Page<Note> getMyNotes(Long folderId, int pageNum, int pageSize) {
        Page<Note> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getUserId, StpUtil.getLoginIdAsLong())
                .eq(Note::getIsDeleted, false)
                .eq(folderId != null, Note::getFolderId, folderId)
                .orderByDesc(Note::getUpdatedAt);
        return noteMapper.selectPage(page, wrapper);
    }

    @Override
    public void updateNote(Long id, NoteDTO dto) {
        Note existingNote = noteMapper.selectById(id);
        if (existingNote == null || existingNote.getIsDeleted() || !existingNote.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new CustomException("笔记不存在或无权修改");
        }

        if (dto.getContentHash() != null && dto.getContentHash().equals(existingNote.getContentHash())) {
            return; // Hash 一致，不执行更新
        }

        // 👉 新增：🌟 核心逻辑！在更新前，先把老内容存入版本表
        Long versionCount = noteVersionMapper.selectCount(
                new LambdaQueryWrapper<NoteVersion>().eq(NoteVersion::getNoteId, id)
        );
        NoteVersion version = new NoteVersion();
        version.setNoteId(id);
        version.setVersionNum(versionCount.intValue() + 1); // 版本号递增
        version.setContent(existingNote.getContent()); // 存老内容
        version.setSnapshotReason("自动存档");
        noteVersionMapper.insert(version);

        // 2. 关键：new 一个空对象！只塞要改的字段！
        Note updateNote = new Note();
        updateNote.setId(id);
        updateNote.setTitle(dto.getTitle());
        updateNote.setContent(dto.getContent());
        updateNote.setOutlineJson(dto.getOutlineJson());
        updateNote.setTags(dto.getTags());
        updateNote.setContentHash(dto.getContentHash());

        noteMapper.updateById(updateNote);
    }

    @Override
    public void deleteNote(Long id) {
        Note existingNote = noteMapper.selectById(id);
        if (existingNote == null || existingNote.getIsDeleted() || !existingNote.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new CustomException("笔记不存在或无权删除");
        }
        Note updateNote = new Note();
        updateNote.setId(id);
        updateNote.setIsDeleted(true);
        noteMapper.updateById(updateNote);
    }

    @Override
    public Page<Note> searchNotes(String tag, String outlineKeyword, int pageNum, int pageSize) {
        Page<Note> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getUserId, StpUtil.getLoginIdAsLong())
                .eq(Note::getIsDeleted, false);

        if (tag != null && !tag.trim().isEmpty()) {
            wrapper.apply("tags @> ARRAY[{0}]", tag);
        }

        // 👉 👉 👉 改动在这里！命中 GIN 索引的包含查询！
        if (outlineKeyword != null && !outlineKeyword.trim().isEmpty()) {
            // 1. 转义双引号，防止破坏 JSON 结构 (防注入)
            String escapedKeyword = outlineKeyword.replace("\"", "\\\"");
            // 2. 构造 JSONB 片段，例如搜 "Spring" -> 构造 [{"t":"Spring"}]
            String jsonFragment = "[{\"t\":\"" + escapedKeyword + "\"}]";
            // 3. 使用 @> 操作符，完美命中 idx_note_outline GIN 索引
            wrapper.apply("outline_json @> {0}::jsonb", jsonFragment);
        }

        wrapper.orderByDesc(Note::getUpdatedAt);
        return noteMapper.selectPage(page, wrapper);
    }

    @Override
    public Note getNoteDetail(Long id) {
        Note note = noteMapper.selectById(id);
        if (note == null || note.getIsDeleted()) {
            throw new CustomException("笔记不存在");
        }
        if (!note.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new CustomException("无权访问该笔记");
        }
        return note;
    }

    @Override
    public Page<Note> listByFolder(Long folderId, Integer page, Integer size) {
        Page<Note> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getUserId, StpUtil.getLoginIdAsLong())
                .eq(Note::getIsDeleted, false)
                .eq(Note::getFolderId, folderId)
                .orderByDesc(Note::getUpdatedAt);
        wrapper.select(Note::getId, Note::getTitle, Note::getWordCount, Note::getTags, Note::getUpdatedAt);
        return noteMapper.selectPage(pageParam, wrapper);
    }

    // 👉 新增：获取笔记版本历史
    @Override
    public List<NoteVersion> getNoteVersions(Long noteId) {
        // 1. 权限校验
        Note note = noteMapper.selectById(noteId);
        if (note == null || note.getIsDeleted() || !note.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new CustomException("笔记不存在或无权访问");
        }
        // 2. 查询版本列表，按版本号倒序 (最新的在前面)
        LambdaQueryWrapper<NoteVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NoteVersion::getNoteId, noteId)
                .orderByDesc(NoteVersion::getVersionNum);
        return noteVersionMapper.selectList(wrapper);
    }

    // 👉 新增：回滚笔记版本
    @Override
    public void rollbackNote(Long noteId, Long versionId) {
        // 1. 校验主笔记权限
        Note note = noteMapper.selectById(noteId);
        if (note == null || note.getIsDeleted() || !note.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new CustomException("笔记不存在或无权操作");
        }

        // 2. 查出要回滚的目标版本
        NoteVersion targetVersion = noteVersionMapper.selectById(versionId);
        if (targetVersion == null || !targetVersion.getNoteId().equals(noteId)) {
            throw new CustomException("历史版本不存在");
        }

        // 3. 🌟 架构文档规定：回滚时，先存档当前最新状态（防后悔）
        Long versionCount = noteVersionMapper.selectCount(
                new LambdaQueryWrapper<NoteVersion>().eq(NoteVersion::getNoteId, noteId)
        );
        NoteVersion currentSnapshot = new NoteVersion();
        currentSnapshot.setNoteId(noteId);
        currentSnapshot.setVersionNum(versionCount.intValue() + 1);
        currentSnapshot.setContent(note.getContent());
        currentSnapshot.setSnapshotReason("回滚前自动存档");
        noteVersionMapper.insert(currentSnapshot);

        // 4. 把老版本的内容覆盖到主表
        Note updateNote = new Note();
        updateNote.setId(noteId);
        updateNote.setContent(targetVersion.getContent());
        // 清空 Hash，因为内容变了，前端下次保存会重新算
        updateNote.setContentHash("");
        noteMapper.updateById(updateNote);
    }

    @Override
    public void addRelation(RelationDTO dto) {
        // 1. 校验这两篇笔记都是当前用户的
        Note noteA = noteMapper.selectById(dto.getNoteIdA());
        Note noteB = noteMapper.selectById(dto.getNoteIdB());
        if (noteA == null || noteB == null ||
                !noteA.getUserId().equals(StpUtil.getLoginIdAsLong()) ||
                !noteB.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new CustomException("笔记不存在或无权操作");
        }

        // 2. 🌟 核心逻辑：强制保证 A < B，否则 PG 的 CHECK 约束会直接报错！
        Long minId = Math.min(dto.getNoteIdA(), dto.getNoteIdB());
        Long maxId = Math.max(dto.getNoteIdA(), dto.getNoteIdB());

        // 3. 防重：如果已经存在这条关系，就不重复插入了
        Long count = noteRelationMapper.selectCount(
                new LambdaQueryWrapper<NoteRelation>()
                        .eq(NoteRelation::getNoteIdA, minId)
                        .eq(NoteRelation::getNoteIdB, maxId)
                        .eq(NoteRelation::getRelationType, dto.getRelationType())
        );
        if (count > 0) {
            return; // 已存在，直接当做成功处理
        }

        // 4. 插入关联
        NoteRelation relation = new NoteRelation();
        relation.setNoteIdA(minId);
        relation.setNoteIdB(maxId);
        relation.setRelationType(dto.getRelationType());
        // 如果是手动双链，权重给最高 1.0；如果是系统算的，由算法赋值
        relation.setSimilarityScore("REFERENCE".equals(dto.getRelationType()) ? 1.0f : 0.0f);

        noteRelationMapper.insert(relation);
    }
}
