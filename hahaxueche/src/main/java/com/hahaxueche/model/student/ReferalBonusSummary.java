package com.hahaxueche.model.student;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/5/2.
 */
public class ReferalBonusSummary implements Serializable {
    private String pending_add_to_account;
    private String available_to_redeem;
    private String redeemed;
    private String code;
    private String message;
    private boolean isSuccess = true;

    public String getPending_add_to_account() {
        return pending_add_to_account;
    }

    public void setPending_add_to_account(String pending_add_to_account) {
        this.pending_add_to_account = pending_add_to_account;
    }

    public String getAvailable_to_redeem() {
        return available_to_redeem;
    }

    public void setAvailable_to_redeem(String available_to_redeem) {
        this.available_to_redeem = available_to_redeem;
    }

    public String getRedeemed() {
        return redeemed;
    }

    public void setRedeemed(String redeemed) {
        this.redeemed = redeemed;
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
