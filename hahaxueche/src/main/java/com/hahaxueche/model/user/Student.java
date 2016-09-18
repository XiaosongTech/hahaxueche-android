package com.hahaxueche.model.user;

import android.text.TextUtils;

import com.hahaxueche.model.payment.BankCard;
import com.hahaxueche.model.payment.Coupon;
import com.hahaxueche.model.payment.PurchasedService;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/9/10.
 */
public class Student {
    public String id;
    public String cell_phone;
    public String name;
    public int city_id;
    public String user_id;
    public String avatar;
    public String current_coach_id;
    public ArrayList<PurchasedService> purchased_services;
    public int phase;
    public int current_course;
    public int bonus_balance;
    public BankCard bank_card;
    public ArrayList<Coupon> coupons;

    public boolean isCompleted() {
        return (city_id >= 0 && !TextUtils.isEmpty(name));
    }
}
