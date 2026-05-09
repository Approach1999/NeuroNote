package com.neuro.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.neuro.backend.common.R;
import com.neuro.backend.dto.UserLoginDTO;
import com.neuro.backend.dto.UserRegisterDTO;
import com.neuro.backend.entity.SysUser;
import com.neuro.backend.mapper.SysUserMapper;
import com.neuro.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    // 实例化 BCrypt 加密器
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public R<Void> register(UserRegisterDTO dto) {
        // 1. 查用户名是否已存在
        SysUser existUser = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>().eq("username", dto.getUsername())
        );
        if (existUser != null) {
            return R.fail("用户名已被注册");
        }

        // 2. 构建实体类并加密密码
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        // 3. 插入数据库
        sysUserMapper.insert(user);
        return R.ok();
    }

    @Override
    public R<String> login(UserLoginDTO dto) {
        // 1. 查用户
        SysUser user = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>().eq("username", dto.getUsername())
        );
        if (user == null) {
            return R.fail("用户名或密码错误");
        }

        // 2. 校验密码 (BCrypt 自动匹配加盐)
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            return R.fail("用户名或密码错误");
        }

        // 3. 调用 Sa-Token 登录，会自动生成 Token
        StpUtil.login(user.getId());

        // 4. 把 Token 返回给前端
        return R.ok(StpUtil.getTokenValue());
    }
}
