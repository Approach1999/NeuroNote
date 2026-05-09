package com.neuro.backend.common;

import cn.dev33.satoken.exception.NotLoginException;
import com.neuro.backend.exception.CustomException;
import org.springframework.dao.DataAccessException; // 👉 引入数据库访问异常
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 拦截校验不通过 (比如 @NotBlank 校验失败)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleValidationException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return R.fail(400, errorMsg);
    }

    // 2. 拦截未登录
    @ExceptionHandler(NotLoginException.class)
    public R<Void> handleNotLoginException(NotLoginException e) {
        return R.fail(401, "请先登录");
    }

    // 3. 🌟 拦截自定义业务异常（我们主动抛出的，包含具体的业务提示）
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public R<?> handleCustomException(CustomException e) {
        return R.fail(e.getCode(), e.getMessage());
    }

    // 4. 🌟 新增：拦截数据库异常（防止 SQL 和表结构泄露！）
    @ExceptionHandler(DataAccessException.class)
    public R<Void> handleDataAccessException(DataAccessException e) {
        e.printStackTrace(); // 后台打印，方便排错
        // 前端只给友好提示，绝对不能给 e.getMessage()
        return R.fail(500, "数据库操作失败，可能存在数据冲突或关联限制，请检查操作是否合规");
    }

    // 5. 🌟 修改：兜底拦截所有的 RuntimeException
    // 注意：不再把 e.getMessage() 给前端，因为未知的 RuntimeException 可能包含敏感信息
    @ExceptionHandler(RuntimeException.class)
    public R<Void> handleRuntimeException(RuntimeException e) {
        e.printStackTrace(); // 后台打印日志
        return R.fail(500, "系统业务处理异常，请稍后再试");
    }

    // 6. 兜底拦截（最顶层的 Exception，如 NullPointerException 等）
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        e.printStackTrace(); // 后台打印日志排错
        return R.fail(500, "服务器内部开小差了，请稍后再试~");
    }
}
