package com.hahaxueche.model.findCoach;

import java.io.Serializable;

/**
 * Created by gibxin on 2016/2/21.
 */
public class BriefCoachInfo implements Serializable {
    private String id;
    private String name;
    private String avatar_url;

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

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
