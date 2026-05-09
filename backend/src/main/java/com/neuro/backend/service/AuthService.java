package com.neuro.backend.service;

import com.neuro.backend.dto.UserLoginDTO;
import com.neuro.backend.dto.UserRegisterDTO;
import com.neuro.backend.common.R;

public interface AuthService {
    R<Void> register(UserRegisterDTO dto);
    R<String> login(UserLoginDTO dto);
}
