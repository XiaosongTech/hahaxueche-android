package com.hahaxueche.model.findCoach;

import com.hahaxueche.model.signupLogin.StudentModel;

import java.util.List;

/**
 * Created by gibxin on 2016/2/28.
 */
public class StuPurchaseResponse {
    private String id;
    private String transaction_id;
    private List<CoachModel> coaches;
    private String total_amount;
    private String current_payment_stage;
    private String service_stage;
    private CoachGroupModel coach_group;
    private StudentModel student;
    private String code;
    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public List<CoachModel> getCoaches() {
        return coaches;
    }

    public void setCoaches(List<CoachModel> coaches) {
        this.coaches = coaches;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getCurrent_payment_stage() {
        return current_payment_stage;
    }

    public void setCurrent_payment_stage(String current_payment_stage) {
        this.current_payment_stage = current_payment_stage;
    }

    public String getService_stage() {
        return service_stage;
    }

    public void setService_stage(String service_stage) {
        this.service_stage = service_stage;
    }

    public CoachGroupModel getCoach_group() {
        return coach_group;
    }

    public void setCoach_group(CoachGroupModel coach_group) {
        this.coach_group = coach_group;
    }

    public StudentModel getStudent() {
        return student;
    }

    public void setStudent(StudentModel student) {
        this.student = student;
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
