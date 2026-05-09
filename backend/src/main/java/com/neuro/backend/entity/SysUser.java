package com.neuro.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String passwordHash;
    private String avatarUrl;
    private String refreshToken; // 👉 新增：长效刷新令牌
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
