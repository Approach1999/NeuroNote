package com.neuro.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuro.backend.entity.Note;
import com.neuro.backend.entity.NoteRelation;
import com.neuro.backend.mapper.NoteMapper;
import com.neuro.backend.mapper.NoteRelationMapper;
import com.neuro.backend.service.GraphService;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GraphServiceImpl implements GraphService {

    private final NoteMapper noteMapper;
    private final NoteRelationMapper noteRelationMapper;
    private final ObjectMapper objectMapper; // Spring 自带的 JSON 解析器

    @Override
    public Map<String, Object> getGlobalGraph(Long folderId) {
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> internalEdges = new ArrayList<>();
        List<Map<String, Object>> relationEdges = new ArrayList<>();

        // 1. 查出用户的笔记
        LambdaQueryWrapper<Note> noteWrapper = new LambdaQueryWrapper<>();
        noteWrapper.eq(Note::getUserId, StpUtil.getLoginIdAsLong())
                .eq(Note::getIsDeleted, false)
                .eq(folderId != null, Note::getFolderId, folderId);
        List<Note> notes = noteMapper.selectList(noteWrapper);

        // 2. 遍历笔记，拼装 nodes 和 internal_edges
        for (Note note : notes) {
            // 笔记根节点
            Map<String, Object> rootNode = new HashMap<>();
            rootNode.put("id", "note-" + note.getId());
            rootNode.put("label", note.getTitle());
            rootNode.put("type", "note_root");
            nodes.add(rootNode);

            // 🌟 解析 outline_json，生成标题节点和内部边
            if (note.getOutlineJson() != null) {
                try {
                    // 将 JSONB 解析为 List<Map>
                    List<Map<String, Object>> outlines = objectMapper.convertValue(note.getOutlineJson(), new TypeReference<List<Map<String, Object>>>() {});
                    if (outlines != null) {
                        for (int i = 0; i < outlines.size(); i++) {
                            Map<String, Object> item = outlines.get(i);
                            String headingId = "h-" + note.getId() + "-" + i; // 给大纲一个唯一ID
                            String text = (String) item.getOrDefault("t", "");

                            // 标题节点
                            Map<String, Object> headingNode = new HashMap<>();
                            headingNode.put("id", headingId);
                            headingNode.put("label", text);
                            headingNode.put("type", "heading");
                            headingNode.put("note_id", note.getId());
                            nodes.add(headingNode);

                            // 内部边 (笔记 -> 标题)
                            Map<String, Object> edge = new HashMap<>();
                            edge.put("source", "note-" + note.getId());
                            edge.put("target", headingId);
                            edge.put("type", "contains");
                            internalEdges.add(edge);
                        }
                    }
                } catch (Exception e) {
                    // JSON 解析异常忽略，不影响全局
                }
            }
        }

        // 3. 查出笔记间的关联关系 (relation_edges)
        // 提取所有笔记 ID，用于批量查询
        List<Long> noteIds = new ArrayList<>();
        for (Note note : notes) noteIds.add(note.getId());

        if (!noteIds.isEmpty()) {
            LambdaQueryWrapper<NoteRelation> relWrapper = new LambdaQueryWrapper<>();
            // 查出涉及当前用户笔记的所有关联 (A在列表中 或 B在列表中)
            relWrapper.and(w -> w.in(NoteRelation::getNoteIdA, noteIds).or().in(NoteRelation::getNoteIdB, noteIds));
            List<NoteRelation> relations = noteRelationMapper.selectList(relWrapper);

            for (NoteRelation rel : relations) {
                Map<String, Object> edge = new HashMap<>();
                edge.put("source", "note-" + rel.getNoteIdA());
                edge.put("target", "note-" + rel.getNoteIdB());
                edge.put("type", rel.getRelationType().toLowerCase()); // similar 或 reference
                edge.put("weight", rel.getSimilarityScore());
                relationEdges.add(edge);
            }
        }

        // 4. 组装最终结果
        Map<String, Object> result = new HashMap<>();
        result.put("nodes", nodes);
        result.put("internal_edges", internalEdges);
        result.put("relation_edges", relationEdges);

        return result;
    }
}
