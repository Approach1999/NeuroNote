package com.neuro.backend.controller;

import com.neuro.backend.common.R;
import com.neuro.backend.dto.UserLoginDTO;
import com.neuro.backend.dto.UserRegisterDTO;
import com.neuro.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public R<String> login(@RequestBody @Validated UserLoginDTO dto) {
        return authService.login(dto);
    }
}
