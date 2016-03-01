package com.hahaxueche.model.signupLogin;

import com.hahaxueche.model.mySetting.PurchasedService;

import java.util.List;

/**
 * 学生数据模型
 * Created by gibxin on 2016/1/22.
 */
public class StudentModel {
    private String id;
    private String cell_phone;
    private String name;
    private String city_id;
    private String user_id;
    private String avatar;
    private String current_coach_id;
    private List<PurchasedService> purchased_services;
    private String code;
    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCell_phone() {
        return cell_phone;
    }

    public void setCell_phone(String cell_phone) {
        this.cell_phone = cell_phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCurrent_coach_id() {
        return current_coach_id;
    }

    public void setCurrent_coach_id(String current_coach_id) {
        this.current_coach_id = current_coach_id;
    }

    public List<PurchasedService> getPurchased_services() {
        return purchased_services;
    }

    public void setPurchased_services(List<PurchasedService> purchased_services) {
        this.purchased_services = purchased_services;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return null == code;
    }
}
