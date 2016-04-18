package com.hahaxueche.model.coach;

import java.io.Serializable;

/**
 * Created by gibxin on 2016/2/21.
 */
public class BriefCoachInfo implements Serializable {
    private String id;
    private String name;
    private String avatar;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
