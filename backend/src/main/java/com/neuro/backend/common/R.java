package com.neuro.backend.common;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class R<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> R<T> ok(T data) {
        return new R<T>().setCode(200).setMsg("success").setData(data);
    }

    public static <T> R<T> ok() {
        return ok(null);
    }

    // 原来的：默认返回 500（留给未知系统异常）
    public static <T> R<T> fail(String msg) {
        return new R<T>().setCode(500).setMsg(msg).setData(null);
    }

    // 👇 新增：支持自定义错误码的 fail
    public static <T> R<T> fail(int code, String msg) {
        return new R<T>().setCode(code).setMsg(msg).setData(null);
    }

}
