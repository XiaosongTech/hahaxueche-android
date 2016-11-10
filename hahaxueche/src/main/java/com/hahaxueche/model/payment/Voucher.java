package com.hahaxueche.model.payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 代金券
 * Created by wangshirui on 2016/11/10.
 */

public class Voucher implements Parcelable {
    public String id;
    public String title;
    public String description;
    public String expired_at;
    public String code;
    public int amount;
    public int status;

    protected Voucher(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        expired_at = in.readString();
        code = in.readString();
        amount = in.readInt();
        status = in.readInt();
    }

    public static final Creator<Voucher> CREATOR = new Creator<Voucher>() {
        @Override
        public Voucher createFromParcel(Parcel in) {
            return new Voucher(in);
        }

        @Override
        public Voucher[] newArray(int size) {
            return new Voucher[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(expired_at);
        dest.writeString(code);
        dest.writeInt(amount);
        dest.writeInt(status);
    }
}
