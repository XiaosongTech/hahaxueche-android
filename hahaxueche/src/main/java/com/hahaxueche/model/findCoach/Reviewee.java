package com.hahaxueche.model.findCoach;

import java.io.Serializable;

/**
 * Created by gibxin on 2016/2/27.
 */
public class Reviewee implements Serializable {
    private String coach_id;
    private String name;
    private String avatar_url;

    public String getCoach_id() {
        return coach_id;
    }

    public void setCoach_id(String coach_id) {
        this.coach_id = coach_id;
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
