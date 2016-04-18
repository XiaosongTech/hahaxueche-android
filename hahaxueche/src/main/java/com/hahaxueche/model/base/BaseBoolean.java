package com.hahaxueche.model.base;

/**
 * Created by gibxin on 2016/2/25.
 */
public class BaseBoolean {
    private String result;
    private String code;
    private String message;
    private boolean isSuccess = true;

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
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isTrue() {
        return result != null && result.equals("true");
    }
}
