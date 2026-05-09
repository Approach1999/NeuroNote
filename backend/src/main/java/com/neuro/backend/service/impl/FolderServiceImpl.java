package com.neuro.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neuro.backend.dto.FolderDTO;
import com.neuro.backend.entity.Folder;
import com.neuro.backend.entity.Note;
import com.neuro.backend.mapper.FolderMapper;
import com.neuro.backend.mapper.NoteMapper;
import com.neuro.backend.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderServiceImpl implements FolderService {

    @Autowired
    private FolderMapper folderMapper;

    @Autowired
    private NoteMapper noteMapper; // 注入 NoteMapper 用来检查文件夹下是否有笔记

    @Override
    public Folder createFolder(FolderDTO dto) {
        Folder folder = new Folder();
        folder.setUserId(StpUtil.getLoginIdAsLong());
        folder.setParentId(dto.getParentId());
        folder.setName(dto.getName());
        folder.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        folderMapper.insert(folder);
        return folder;
    }

    @Override
    public List<Folder> getFolders() {
        LambdaQueryWrapper<Folder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Folder::getUserId, StpUtil.getLoginIdAsLong())
                .orderByAsc(Folder::getSortOrder); // 按排序字段升序
        return folderMapper.selectList(wrapper);
    }

    @Override
    public Folder updateFolder(Long id, FolderDTO dto) {
        Folder existing = folderMapper.selectById(id);
        if (existing == null || !existing.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new RuntimeException("文件夹不存在或无权操作");
        }

        // 只更新传进来的字段
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getParentId() != null) existing.setParentId(dto.getParentId());
        if (dto.getSortOrder() != null) existing.setSortOrder(dto.getSortOrder());

        folderMapper.updateById(existing);
        return existing;
    }

    @Override
    public void deleteFolder(Long id) {
        Folder existing = folderMapper.selectById(id);
        if (existing == null || !existing.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new RuntimeException("文件夹不存在或无权操作");
        }

        // 🌟 核心防御 1：检查该文件夹下是否有【未删除】的笔记
        // 因为 PG 外键是 ON DELETE RESTRICT，有笔记硬删会直接让数据库报错 500
        LambdaQueryWrapper<Note> noteWrapper = new LambdaQueryWrapper<>();
        noteWrapper.eq(Note::getFolderId, id)
                .eq(Note::getIsDeleted, false);
        Long noteCount = noteMapper.selectCount(noteWrapper);
        if (noteCount > 0) {
            throw new RuntimeException("该文件夹下存在笔记，无法删除！请先清空或移动笔记");
        }

        // 🌟 核心防御 2：检查是否存在子文件夹
        // 因为子文件夹是 ON DELETE CASCADE，如果子文件夹里有笔记，PG 也会报错
        LambdaQueryWrapper<Folder> subFolderWrapper = new LambdaQueryWrapper<>();
        subFolderWrapper.eq(Folder::getParentId, id);
        Long subFolderCount = folderMapper.selectCount(subFolderWrapper);
        if (subFolderCount > 0) {
            throw new RuntimeException("该文件夹下存在子文件夹，请先删除子文件夹");
        }

        // 两层防御都通过，安全物理删除
        folderMapper.deleteById(id);
    }
}
