package com.hahaxueche.model.user.coach;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangshirui on 2017/3/8.
 */

public class ClassType implements Parcelable {

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

    protected ClassType(Parcel in) {
        name = in.readString();
        type = in.readInt();
        price = in.readInt();
        isForceInsurance = in.readByte() != 0;
        desc = in.readString();
        licenseType = in.readInt();
    }

    public static final Creator<ClassType> CREATOR = new Creator<ClassType>() {
        @Override
        public ClassType createFromParcel(Parcel in) {
            return new ClassType(in);
        }

        @Override
        public ClassType[] newArray(int size) {
            return new ClassType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(type);
        dest.writeInt(price);
        dest.writeByte((byte) (isForceInsurance ? 1 : 0));
        dest.writeString(desc);
        dest.writeInt(licenseType);
    }
}
