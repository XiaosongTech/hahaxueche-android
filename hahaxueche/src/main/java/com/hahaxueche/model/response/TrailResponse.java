package com.hahaxueche.model.response;

import com.hahaxueche.model.coach.CoachModel;

/**
 * 创建预约教练试学反馈
 * Created by gibxin on 2016/2/26.
 */
public class TrailResponse {
    private String id;
    private String name;
    private String phone_number;
    private String first_time_option;
    private String second_time_option;
    private CoachModel coach;
    private String code;
    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getFirst_time_option() {
        return first_time_option;
    }

    public void setFirst_time_option(String first_time_option) {
        this.first_time_option = first_time_option;
    }

    public String getSecond_time_option() {
        return second_time_option;
    }

    public void setSecond_time_option(String second_time_option) {
        this.second_time_option = second_time_option;
    }

    public CoachModel getCoach() {
        return coach;
    }

    public void setCoach(CoachModel coach) {
        this.coach = coach;
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
        return null == code || code.equals("0");
    }
}
