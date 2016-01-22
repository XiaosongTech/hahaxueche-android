package com.hahaxueche.api;

/**
 * API响应结果封装
 * Created by gibxin on 2016/1/21.
 */
public class ApiResponse<T> {
    private String code;//返回码,0代表成功
    private String message;//消息
    private T obj;//单个对象

    public ApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public boolean isSuccess() {
        return code.equals("0");
    }
}
