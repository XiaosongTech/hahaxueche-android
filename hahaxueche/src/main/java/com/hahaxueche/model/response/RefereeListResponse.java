package com.hahaxueche.model.response;

import com.hahaxueche.model.base.LinksModel;
import com.hahaxueche.model.student.Referee;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/2.
 */
public class RefereeListResponse {
    private LinksModel links;
    private ArrayList<Referee> data;
    private String code;
    private String message;
    private boolean isSuccess = true;

    public LinksModel getLinks() {
        return links;
    }

    public void setLinks(LinksModel links) {
        this.links = links;
    }

    public ArrayList<Referee> getData() {
        return data;
    }

    public void setData(ArrayList<Referee> data) {
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

    public void setIsSuccess(boolean success) {
        isSuccess = success;
    }
}
