package com.hahaxueche.model.student;

/**
 * Created by Administrator on 2016/5/2.
 */
public class ReferalBonusTransaction {
    private String id;
    private String bonus_amount;
    private String account;
    private String account_owner_name;
    private String created_at;
    private String redeemed_at;
    private String code;
    private String message;
    private boolean isSuccess = true;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBonus_amount() {
        return bonus_amount;
    }

    public void setBonus_amount(String bonus_amount) {
        this.bonus_amount = bonus_amount;
    }

    public String getRedeemed_at() {
        return redeemed_at;
    }

    public void setRedeemed_at(String redeemed_at) {
        this.redeemed_at = redeemed_at;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount_owner_name() {
        return account_owner_name;
    }

    public void setAccount_owner_name(String account_owner_name) {
        this.account_owner_name = account_owner_name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
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
