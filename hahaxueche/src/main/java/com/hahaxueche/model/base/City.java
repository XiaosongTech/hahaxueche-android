package com.hahaxueche.model.base;

import com.hahaxueche.model.payment.OtherFee;
import com.hahaxueche.model.payment.PaymentStage;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/9/8.
 */
public class City {
    public int id = -1;
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
    public ArrayList<OtherFee> other_fee;

    public int getTotalFixedFee() {
        int ret = 0;
        for (FixedCostItem fixedFee : fixed_cost_itemizer) {
            ret += fixedFee.cost;
        }
        return ret;
    }
}
