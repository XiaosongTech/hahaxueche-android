package com.hahaxueche.model.base;

import java.util.ArrayList;

/**
 * Created by gibxin on 2016/4/18.
 */
public class BannerHighlight {
    private String text;
    private String name;
    private String avatar_url;
    private ArrayList<String> highlights;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public ArrayList<String> getHighlights() {
        return highlights;
    }

    public void setHighlights(ArrayList<String> highlights) {
        this.highlights = highlights;
    }
}
