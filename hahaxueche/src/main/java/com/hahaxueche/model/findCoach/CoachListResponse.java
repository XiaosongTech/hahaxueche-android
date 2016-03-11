package com.hahaxueche.model.findCoach;

import com.hahaxueche.model.util.LinksModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gibxin on 2016/2/21.
 */
public class CoachListResponse {
    private LinksModel links;
    private ArrayList<CoachModel> data;
    private String message;
    private String code;

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

    public boolean isSuccess(){
        return code == null;
    }
}
