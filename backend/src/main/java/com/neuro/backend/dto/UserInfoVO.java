package com.neuro.backend.dto;

import lombok.Data;

@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String avatarUrl;

    public UserInfoVO(Long id, String username, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }
}
