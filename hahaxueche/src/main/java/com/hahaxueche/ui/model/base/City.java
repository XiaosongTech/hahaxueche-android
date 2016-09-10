package com.hahaxueche.ui.model.base;

import com.hahaxueche.ui.model.payment.PaymentStage;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/9/8.
 */
public class City {
    public int id;
    public String name;
    public String zip_code;
    public boolean available;
    public ArrayList<FixedCostItem> fixed_cost_itemizer;
    public ArrayList<PaymentStage> payment_stages;
    public ArrayList<Course> courses;
    public int referer_bonus;
    public int referee_bonus;
    public String referral_banner;
    public CoachGroupFilter filters;
}
