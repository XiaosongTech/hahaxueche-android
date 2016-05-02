package com.hahaxueche.model.student;

/**
 * Created by Administrator on 2016/5/2.
 */
public class Referee {
    private String id;
    private String referee_bonus_amount;
    private String referer_bonus_amount;
    private String created_at;
    private RefereeStatus referee_status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferee_bonus_amount() {
        return referee_bonus_amount;
    }

    public void setReferee_bonus_amount(String referee_bonus_amount) {
        this.referee_bonus_amount = referee_bonus_amount;
    }

    public String getReferer_bonus_amount() {
        return referer_bonus_amount;
    }

    public void setReferer_bonus_amount(String referer_bonus_amount) {
        this.referer_bonus_amount = referer_bonus_amount;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public RefereeStatus getReferee_status() {
        return referee_status;
    }

    public void setReferee_status(RefereeStatus referee_status) {
        this.referee_status = referee_status;
    }
}
