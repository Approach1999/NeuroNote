package com.neuro.backend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuro.backend.common.R; // 👉 统一使用 R
import com.neuro.backend.dto.NoteDTO;
import com.neuro.backend.dto.RelationDTO;
import com.neuro.backend.entity.Note;
import com.neuro.backend.entity.NoteVersion;
import com.neuro.backend.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes") // 👉 修改：复数路径
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @SaCheckLogin
    @PostMapping
    public R<Void> create(@RequestBody NoteDTO dto) {
        noteService.createNote(dto);
        return R.ok();
    }

    @GetMapping
    public R<Page<Note>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long folderId) {
        return R.ok(noteService.getMyNotes(folderId, pageNum, pageSize));
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody NoteDTO dto) {
        noteService.updateNote(id, dto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        noteService.deleteNote(id);
        return R.ok();
    }

    @GetMapping("/search")
    public R<Page<Note>> search(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String outlineKeyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(noteService.searchNotes(tag, outlineKeyword, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<Note> getNoteDetail(@PathVariable Long id) {
        return R.ok(noteService.getNoteDetail(id));
    }

    // 👉 修改：更符合 RESTful 子资源语义的路径
    @GetMapping("/by-folder/{folderId}")
    public R<Page<Note>> listByFolder(
            @PathVariable Long folderId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return R.ok(noteService.listByFolder(folderId, page, size));
    }

    @GetMapping("/{id}/versions")
    public R<List<NoteVersion>> getVersions(@PathVariable Long id) {
        return R.ok(noteService.getNoteVersions(id));
    }

    @PostMapping("/{id}/rollback/{versionId}")
    public R<Void> rollback(@PathVariable Long id, @PathVariable Long versionId) {
        noteService.rollbackNote(id, versionId);
        return R.ok();
    }

    @SaCheckLogin
    @PostMapping("/relations")
    public R<Void> addRelation(@RequestBody RelationDTO dto) {
        noteService.addRelation(dto);
        return R.ok();
    }
}
