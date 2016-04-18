package com.hahaxueche.model.response;

import com.hahaxueche.model.coach.CoachModel;
import com.hahaxueche.model.base.LinksModel;

import java.util.ArrayList;

/**
 * Created by gibxin on 2016/2/21.
 */
public class CoachListResponse {
    private LinksModel links;
    private ArrayList<CoachModel> data;
    private String message;
    private String code;
    private boolean isSuccess = true;

    public LinksModel getLinks() {
        return links;
    }

    public void setLinks(LinksModel links) {
        this.links = links;
    }

    public ArrayList<CoachModel> getData() {
        return data;
    }

    public void setData(ArrayList<CoachModel> data) {
        this.data = data;
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
