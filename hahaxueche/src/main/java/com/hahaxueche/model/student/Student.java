package com.hahaxueche.model.student;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生数据模型
 * Created by gibxin on 2016/1/22.
 */
public class Student implements Serializable{
    private String id;
    private String cell_phone;
    private String name;
    private String city_id;
    private String user_id;
    private String avatar;
    private String current_coach_id;
    private List<PurchasedService> purchased_services;
    private String current_course;
    private String reviewed;
    private String reviewed_at;
    private String status;
    private String by_referal;
    private String bonus_balance;
    private ArrayList<Coupon> coupons;
    private BankCard bank_card;
    private String code;
    private String message;
    private boolean isSuccess = true;

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

    public String getCurrent_course() {
        return current_course;
    }

    public void setCurrent_course(String current_course) {
        this.current_course = current_course;
    }

    public String getReviewed() {
        return reviewed;
    }

    public void setReviewed(String reviewed) {
        this.reviewed = reviewed;
    }

    public String getReviewed_at() {
        return reviewed_at;
    }

    public void setReviewed_at(String reviewed_at) {
        this.reviewed_at = reviewed_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBy_referal() {
        return by_referal;
    }

    public void setBy_referal(String by_referal) {
        this.by_referal = by_referal;
    }

    public String getBonus_balance() {
        return bonus_balance;
    }

    public void setBonus_balance(String bonus_balance) {
        this.bonus_balance = bonus_balance;
    }

    public ArrayList<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(ArrayList<Coupon> coupons) {
        this.coupons = coupons;
    }

    public BankCard getBank_card() {
        return bank_card;
    }

    public void setBank_card(BankCard bank_card) {
        this.bank_card = bank_card;
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
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean hasPurchasedService() {
        if (purchased_services == null)
            return false;
        if (purchased_services.size() == 0)
            return false;
        return true;
    }
}
