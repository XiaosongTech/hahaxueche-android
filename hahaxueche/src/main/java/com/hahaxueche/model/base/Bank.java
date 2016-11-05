package com.hahaxueche.model.base;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangshirui on 16/9/8.
 */
public class Bank implements Parcelable {
    public String code;
    public String name;
    public boolean is_popular;

    public Bank() {

    }

    protected Bank(Parcel in) {
        code = in.readString();
        name = in.readString();
        is_popular = in.readByte() != 0;
    }

    public static final Creator<Bank> CREATOR = new Creator<Bank>() {
        @Override
        public Bank createFromParcel(Parcel in) {
            return new Bank(in);
        }

        @Override
        public Bank[] newArray(int size) {
            return new Bank[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
        dest.writeByte((byte) (is_popular ? 1 : 0));
    }
}
