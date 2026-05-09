package com.neuro.backend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.neuro.backend.common.R; // 👉 统一使用 R
import com.neuro.backend.dto.FolderDTO;
import com.neuro.backend.entity.Folder;
import com.neuro.backend.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders") // 👉 修改：复数路径
@SaCheckLogin
public class FolderController {

    @Autowired
    private FolderService folderService;

    @PostMapping
    public R<Folder> create(@RequestBody FolderDTO dto) {
        return R.ok(folderService.createFolder(dto));
    }

    // 👉 修改：PDF 契约要求 GET /api/folders/tree
    @GetMapping("/tree")
    public R<List<Folder>> list() {
        return R.ok(folderService.getFolders());
    }

    @PutMapping("/{id}")
    public R<Folder> update(@PathVariable Long id, @RequestBody FolderDTO dto) {
        return R.ok(folderService.updateFolder(id, dto));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        folderService.deleteFolder(id);
        return R.ok();
    }
}
