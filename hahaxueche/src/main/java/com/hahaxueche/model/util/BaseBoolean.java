package com.hahaxueche.model.util;

/**
 * Created by gibxin on 2016/2/25.
 */
public class BaseBoolean {
    private String result;
    private String code;
    private String message;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public boolean isSuccess() {
        return code == null;
    }

    public boolean isTrue() {
        return result != null && result.equals("true");
    }
}
