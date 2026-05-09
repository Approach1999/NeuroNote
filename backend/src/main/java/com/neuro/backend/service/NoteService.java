package com.neuro.backend.service;

import com.neuro.backend.dto.NoteDTO;
import com.neuro.backend.entity.Note;
import java.util.List;

public interface NoteService {
    void createNote(NoteDTO dto);
    List<Note> getMyNotes();
    void updateNote(Long id, NoteDTO dto);
    void deleteNote(Long id);
}
