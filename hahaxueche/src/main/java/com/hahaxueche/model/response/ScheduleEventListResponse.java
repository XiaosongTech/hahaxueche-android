package com.hahaxueche.model.response;

import com.hahaxueche.model.base.LinksModel;
import com.hahaxueche.model.coach.ScheduleEvent;

import java.util.ArrayList;

/**
 * Created by gibxin on 2016/4/18.
 */
public class ScheduleEventListResponse {
    private LinksModel links;
    private ArrayList<ScheduleEvent> data;
    private String code;
    private String message;
    private boolean isSuccess = true;

    public LinksModel getLinks() {
        return links;
    }

    public void setLinks(LinksModel links) {
        this.links = links;
    }

    public ArrayList<ScheduleEvent> getData() {
        return data;
    }

    public void setData(ArrayList<ScheduleEvent> data) {
        this.data = data;
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
}
