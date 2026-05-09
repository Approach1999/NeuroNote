package com.neuro.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.neuro.backend.common.R;
import com.neuro.backend.dto.*;
import com.neuro.backend.entity.SysUser;
import com.neuro.backend.mapper.SysUserMapper;
import com.neuro.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public R<Void> register(UserRegisterDTO dto) {
        SysUser existUser = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>().eq("username", dto.getUsername())
        );
        if (existUser != null) {
            return R.fail(400, "用户名已被注册");
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        sysUserMapper.insert(user);
        return R.ok();
    }

    @Override
    public R<LoginVO> login(UserLoginDTO dto) {
        SysUser user = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>().eq("username", dto.getUsername())
        );
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            return R.fail(400, "用户名或密码错误");
        }

        // 1. Sa-Token 登录，获取短期 Access Token
        StpUtil.login(user.getId());
        String accessToken = StpUtil.getTokenValue();

        // 2. 生成长期 Refresh Token (UUID) 并存入数据库
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        user.setRefreshToken(refreshToken);
        sysUserMapper.updateById(user);

        // 3. 组装符合 PDF 契约的返回体
        UserInfoVO userInfo = new UserInfoVO(user.getId(), user.getUsername(), user.getAvatarUrl());
        LoginVO loginVO = new LoginVO(accessToken, refreshToken, userInfo);

        return R.ok(loginVO);
    }

    @Override
    public R<String> refresh(String refreshToken) {
        // 1. 根据 refreshToken 查用户
        SysUser user = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>().eq("refresh_token", refreshToken)
        );
        if (user == null) {
            return R.fail(401, "无效的刷新令牌");
        }

        // 2. 重新调用 Sa-Token 登录，生成新的 Access Token
        StpUtil.login(user.getId());
        String newAccessToken = StpUtil.getTokenValue();

        // 注意：PDF 契约规定“不返回refresh_token，除非采用滚动刷新策略”
        // 这里我们采用简单策略：refresh_token 不变，只换 access_token
        return R.ok(newAccessToken);
    }
}
