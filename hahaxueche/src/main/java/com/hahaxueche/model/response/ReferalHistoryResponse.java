package com.hahaxueche.model.response;

import com.hahaxueche.model.base.LinksModel;
import com.hahaxueche.model.student.ReferalBonusTransaction;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/2.
 */
public class ReferalHistoryResponse {
    private LinksModel links;
    private ArrayList<ReferalBonusTransaction> data;
    private String code;
    private String message;
    private boolean isSuccess = true;

    public LinksModel getLinks() {
        return links;
    }

    public void setLinks(LinksModel links) {
        this.links = links;
    }

    public ArrayList<ReferalBonusTransaction> getData() {
        return data;
    }

    public void setData(ArrayList<ReferalBonusTransaction> data) {
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
