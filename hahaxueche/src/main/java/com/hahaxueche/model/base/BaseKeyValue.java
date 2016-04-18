package com.hahaxueche.model.base;

/**
 * 基本键值对类型
 * Created by gibxin on 2016/2/24.
 */
public class BaseKeyValue {
    private String id;
    private String type;
    private String readable_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReadable_name() {
        return readable_name;
    }

    public void setReadable_name(String readable_name) {
        this.readable_name = readable_name;
    }
}
