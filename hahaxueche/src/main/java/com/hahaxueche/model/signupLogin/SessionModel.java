package com.hahaxueche.model.signupLogin;

/**
 * 用户session数据模型
 * Created by gibxin on 2016/1/22.
 */
public class SessionModel {
    private String id;
    private String user_id;
    private String access_token;
    private String log_off_time;
    private String created_at;
    private String updated_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getLog_off_time() {
        return log_off_time;
    }

    public void setLog_off_time(String log_off_time) {
        this.log_off_time = log_off_time;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
