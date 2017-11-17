package com.tbea.tb.tbeawaterelectrician.http;

import java.util.Map;

public class RspInfo<T> {
    private Map<String, T> data;
    private String msg;
    private boolean success;

    public T getDateObj(String key) {
        return data.get(key);
    }

    public String getMsg() {
        return msg;
    }

    public boolean isSuccess() {
        return success;
    }
}
