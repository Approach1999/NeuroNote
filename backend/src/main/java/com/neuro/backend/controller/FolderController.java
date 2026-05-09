package com.neuro.backend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.neuro.backend.common.Result;
import com.neuro.backend.dto.FolderDTO;
import com.neuro.backend.entity.Folder;
import com.neuro.backend.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folder")
@SaCheckLogin
public class FolderController {

    @Autowired
    private FolderService folderService;

    @PostMapping
    public Result<Folder> create(@RequestBody FolderDTO dto) {
        return Result.success(folderService.createFolder(dto));
    }

    @GetMapping
    public Result<List<Folder>> list() {
        return Result.success(folderService.getFolders());
    }

    @PutMapping("/{id}")
    public Result<Folder> update(@PathVariable Long id, @RequestBody FolderDTO dto) {
        return Result.success(folderService.updateFolder(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        folderService.deleteFolder(id);
        return Result.success(null);
    }
}
