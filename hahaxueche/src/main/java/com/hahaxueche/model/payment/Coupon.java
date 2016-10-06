package com.hahaxueche.model.payment;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/9/10.
 */
public class Coupon implements Parcelable{
    public ArrayList<String> content;
    public int status;
    public String channel_name;
    public String promo_code;

    protected Coupon(Parcel in) {
        content = in.createStringArrayList();
        status = in.readInt();
        channel_name = in.readString();
        promo_code = in.readString();
    }

    public static final Creator<Coupon> CREATOR = new Creator<Coupon>() {
        @Override
        public Coupon createFromParcel(Parcel in) {
            return new Coupon(in);
        }

        @Override
        public Coupon[] newArray(int size) {
            return new Coupon[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(content);
        dest.writeInt(status);
        dest.writeString(channel_name);
        dest.writeString(promo_code);
    }
}
