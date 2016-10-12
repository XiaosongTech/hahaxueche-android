package com.hahaxueche.model.user.coach;

/**
 * Created by wangshirui on 2016/10/12.
 */

public class ProductType {
    public int price;
    public String name;
    public String label;
    public int nameBackground;
    public String remark;

    public ProductType(int price, String name, String label, int nameBackground, String remark) {
        this.price = price;
        this.name = name;
        this.label = label;
        this.nameBackground = nameBackground;
        this.remark = remark;
    }
}
