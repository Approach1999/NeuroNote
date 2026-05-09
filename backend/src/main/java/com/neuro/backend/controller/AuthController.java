package com.neuro.backend.controller;

import com.neuro.backend.common.R;
import com.neuro.backend.dto.LoginVO;
import com.neuro.backend.dto.UserLoginDTO;
import com.neuro.backend.dto.UserRegisterDTO;
import com.neuro.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public R<Void> register(@RequestBody @Validated UserRegisterDTO dto) {
        return authService.register(dto);
    }

    @PostMapping("/login")
    public R<LoginVO> login(@RequestBody @Validated UserLoginDTO dto) {
        return authService.login(dto);
    }

    // 👉 新增：无感刷新接口
    @PostMapping("/refresh")
    public R<String> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refresh_token");
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return R.fail(400, "refresh_token 不能为空");
        }
        return authService.refresh(refreshToken);
    }
}
