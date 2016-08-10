package com.hahaxueche.model.student;

import java.io.Serializable;

/**
 * Created by wangshirui on 16/8/1.
 */
public class Bank implements Serializable{
    private String bank_name;
    private String account_name;
    private String account;
    private boolean isSelect;

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
