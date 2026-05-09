package com.neuro.backend.dto;

import lombok.Data;

@Data
public class LoginVO {
    private String accessToken;
    private String refreshToken;
    private UserInfoVO userInfo;

    public LoginVO(String accessToken, String refreshToken, UserInfoVO userInfo) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userInfo = userInfo;
    }
}
