package com.hahaxueche.model.review;

import java.io.Serializable;

/**
 * 评论人
 * Created by gibxin on 2016/2/27.
 */
public class Reviewer implements Serializable {
    private String student_id;
    private String name;
    private String avatar_url;

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
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
