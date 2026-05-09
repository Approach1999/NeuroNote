package com.neuro.backend.service;

import com.neuro.backend.common.R;
import com.neuro.backend.dto.LoginVO;
import com.neuro.backend.dto.UserLoginDTO;
import com.neuro.backend.dto.UserRegisterDTO;

public interface AuthService {
    R<Void> register(UserRegisterDTO dto);
    R<LoginVO> login(UserLoginDTO dto); // 👉 修改：返回 LoginVO
    R<String> refresh(String refreshToken); // 👉 新增：刷新 Token
}
