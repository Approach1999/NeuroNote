package com.neuro.backend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.neuro.backend.common.R;
import com.neuro.backend.service.GraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/graph")
@RequiredArgsConstructor
public class GraphController {

    private final GraphService graphService;

    // 获取全局图谱数据
    @SaCheckLogin
    @GetMapping("/global")
    public R<Map<String, Object>> getGlobalGraph(
            @RequestParam(required = false) Long folderId) { // 可选：按文件夹过滤
        return R.ok(graphService.getGlobalGraph(folderId));
    }
}
