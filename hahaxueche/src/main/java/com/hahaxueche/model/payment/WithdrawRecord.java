package com.hahaxueche.model.payment;

/**
 * Created by wangshirui on 2016/11/1.
 */

public class WithdrawRecord {
    public String name;
    public String card_number;
    public String open_bank_code;
    public int amount;
    public int status;
    public String withdrawed_at;

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
