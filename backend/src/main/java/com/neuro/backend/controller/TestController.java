package com.neuro.backend.controller;

import com.neuro.backend.entity.SysUser;
import com.neuro.backend.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final SysUserMapper sysUserMapper;

    @GetMapping("/db")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 查一下用户表里有几条数据
            List<SysUser> users = sysUserMapper.selectList(null);
            result.put("success", true);
            result.put("message", "数据库连接成功！🎉");
            result.put("userCount", users.size());
            result.put("users", users);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "数据库连接失败: " + e.getMessage());
        }
        return result;
    }
}
