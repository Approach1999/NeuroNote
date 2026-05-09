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

    public static <T> R<T> fail(String msg) {
        return new R<T>().setCode(500).setMsg(msg).setData(null);
    }
}
