package com.hahaxueche.model.student;

import java.io.Serializable;

/**
 * Created by wangshirui on 16/8/19.
 */
public class WithdrawRecord implements Serializable {
    private String name;
    private String card_number;
    private String open_bank_code;
    private int amount;
    private int status;
    private String withdrawed_at;
    private String code;
    private String message;
    private boolean isSuccess = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getOpen_bank_code() {
        return open_bank_code;
    }

    public void setOpen_bank_code(String open_bank_code) {
        this.open_bank_code = open_bank_code;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getWithdrawed_at() {
        return withdrawed_at;
    }

    public void setWithdrawed_at(String withdrawed_at) {
        this.withdrawed_at = withdrawed_at;
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

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getStatusLabel() {
        switch (status) {
            case 0:
                return "处理中";
            case 1:
                return "成功";
            case 2:
                return "失败";
            default:
                return "";
        }
    }
}
