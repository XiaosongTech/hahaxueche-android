package com.hahaxueche.model.payment;

/**
 * Created by wangshirui on 2016/10/11.
 */

public class PaymentMethod {
    public int id;
    public int drawableLogo;
    public String name;
    public String remark;

    public PaymentMethod(int id, int drawableLogo, String name, String remark) {
        this.id = id;
        this.drawableLogo = drawableLogo;
        this.name = name;
        this.remark = remark;
    }
}
