package com.neuro.backend.service;

import com.neuro.backend.dto.FolderDTO;
import com.neuro.backend.entity.Folder;
import java.util.List;

public interface FolderService {
    Folder createFolder(FolderDTO dto);

    // 获取当前用户的所有文件夹（前端拿到后自己拼装成树形结构）
    List<Folder> getFolders();

    Folder updateFolder(Long id, FolderDTO dto);

    void deleteFolder(Long id);
}
