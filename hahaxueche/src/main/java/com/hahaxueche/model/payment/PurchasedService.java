package com.hahaxueche.model.payment;

import com.hahaxueche.model.user.coach.CoachGroup;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/9/10.
 */
public class PurchasedService {
    public String id;
    public String charge_id;
    public int total_amount;
    public int current_payment_stage;
    public int paid_amount;
    public int unpaid_amount;
    public String paid_at;
    public String order_no;
    public int product_type;
    public CoachGroup coach_group;
    public boolean active;
    public ArrayList<PaymentStage> payment_stages;
}
