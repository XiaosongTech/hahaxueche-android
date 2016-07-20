package com.hahaxueche.model.student;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wangshirui on 16/7/19.
 */
public class Coupon implements Serializable {
    private ArrayList<String> content;
    private int status;
    private String channel_name;
    private String promo_code;

    public ArrayList<String> getContent() {
        return content;
    }

    public void setContent(ArrayList<String> content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getPromo_code() {
        return promo_code;
    }

    public void setPromo_code(String promo_code) {
        this.promo_code = promo_code;
    }
}
