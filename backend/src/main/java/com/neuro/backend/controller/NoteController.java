package com.neuro.backend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.neuro.backend.common.R;
import com.neuro.backend.dto.NoteDTO;
import com.neuro.backend.entity.Note;
import com.neuro.backend.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/note")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    // 加上这个注解，代表这个接口下的所有方法必须登录才能访问！
    @SaCheckLogin
    @PostMapping
    public R<Void> create(@RequestBody NoteDTO dto) {
        noteService.createNote(dto);
        return R.ok();
    }

    @SaCheckLogin
    @GetMapping
    public R<List<Note>> list() {
        return R.ok(noteService.getMyNotes());
    }

    // 修改接口
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody NoteDTO dto) {
        noteService.updateNote(id, dto);
        return R.ok();
    }

    // 删除接口
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        noteService.deleteNote(id);
        return R.ok();
    }
}
