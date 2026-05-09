package com.neuro.backend.exception;

import lombok.Getter;

/**
 * 自定义业务异常
 */
@Getter
public class CustomException extends RuntimeException {

    private final int code;

    // 默认 400 业务异常
    public CustomException(String message) {
        super(message);
        this.code = 400;
    }

    // 可以自定义错误码的异常（比如有些特定业务想返回 403 等）
    public CustomException(int code, String message) {
        super(message);
        this.code = code;
    }
}
