package com.hahaxueche.model.user.coach;

/**
 * Created by wangshirui on 2017/3/8.
 */

public class ClassType {

    public ClassType(String name, int type, int price, boolean isForceInsurance, String desc, int licenseType) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.isForceInsurance = isForceInsurance;
        this.desc = desc;
        this.licenseType = licenseType;
    }

    public String name;
    public int type;
    public int price;
    //是否强制购买赔付宝
    public boolean isForceInsurance;
    public String desc;
    public int licenseType;
}
