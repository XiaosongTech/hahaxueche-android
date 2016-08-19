package com.hahaxueche.model.student;

import java.io.Serializable;

/**
 * Created by wangshirui on 16/8/18.
 */
public class Bank implements Serializable {
    private String code;
    private String name;
    private boolean is_popular;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean is_popular() {
        return is_popular;
    }

    public void setIs_popular(boolean is_popular) {
        this.is_popular = is_popular;
    }
}
