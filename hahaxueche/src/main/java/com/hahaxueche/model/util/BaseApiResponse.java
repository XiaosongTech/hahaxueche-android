package com.hahaxueche.model.util;

/**
 * Api响应基础数据模型
 * Created by gibxin on 2016/1/22.
 */
public class BaseApiResponse {
    private String code;
    private String message;

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

    public boolean isSuccess(){
        return code.equals("20000");
    }
}
