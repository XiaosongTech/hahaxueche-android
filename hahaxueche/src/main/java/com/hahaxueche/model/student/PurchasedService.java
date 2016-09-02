package com.hahaxueche.model.student;

import com.hahaxueche.model.coach.CoachGroupModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gibxin on 2016/2/29.
 */
public class PurchasedService implements Serializable{
    private String id;
    private String charge_id;
    private List<Assignment> assignments;
    private String total_amount;
    private int current_payment_stage;
    private String paid_amount;
    private String unpaid_amount;
    private String paid_at;
    private String order_no;
    private CoachGroupModel coach_group;
    private List<PaymentStage> payment_stages;
    private String code;
    private String message;
    private boolean isSuccess = true;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCharge_id() {
        return charge_id;
    }

    public void setCharge_id(String charge_id) {
        this.charge_id = charge_id;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public int getCurrent_payment_stage() {
        return current_payment_stage;
    }

    public void setCurrent_payment_stage(int current_payment_stage) {
        this.current_payment_stage = current_payment_stage;
    }

    public String getPaid_amount() {
        return paid_amount;
    }

    public void setPaid_amount(String paid_amount) {
        this.paid_amount = paid_amount;
    }

    public String getUnpaid_amount() {
        return unpaid_amount;
    }

    public void setUnpaid_amount(String unpaid_amount) {
        this.unpaid_amount = unpaid_amount;
    }

    public String getPaid_at() {
        return paid_at;
    }

    public void setPaid_at(String paid_at) {
        this.paid_at = paid_at;
    }

    public CoachGroupModel getCoach_group() {
        return coach_group;
    }

    public void setCoach_group(CoachGroupModel coach_group) {
        this.coach_group = coach_group;
    }

    public List<PaymentStage> getPayment_stages() {
        return payment_stages;
    }

    public void setPayment_stages(List<PaymentStage> payment_stages) {
        this.payment_stages = payment_stages;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
