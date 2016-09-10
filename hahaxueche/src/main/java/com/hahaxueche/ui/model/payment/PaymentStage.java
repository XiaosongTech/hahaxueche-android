package com.hahaxueche.ui.model.payment;

/**
 * Created by wangshirui on 16/9/10.
 */
public class PaymentStage {
    public String id;
    public int stage_number;
    public String stage_name;
    public String description;
    public int stage_amount;
    public boolean reviewable;
    public boolean active;
    public int stage_fee_type;
    public boolean reviewed;
    public String paid_at;
    public String deleted_at;
    public boolean ready_for_review;
    public String coach_user_id;
}
