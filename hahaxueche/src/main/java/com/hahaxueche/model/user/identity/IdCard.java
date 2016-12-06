package com.hahaxueche.model.user.identity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangshirui on 2016/11/29.
 */

public class IdCard implements Parcelable{
    public String name;
    public String num;
    public String sex;
    public String address;
    public String start_date;
    public String end_date;
    public String issue;

    protected IdCard(Parcel in) {
        name = in.readString();
        num = in.readString();
        sex = in.readString();
        address = in.readString();
        start_date = in.readString();
        end_date = in.readString();
        issue = in.readString();
    }

    public static final Creator<IdCard> CREATOR = new Creator<IdCard>() {
        @Override
        public IdCard createFromParcel(Parcel in) {
            return new IdCard(in);
        }

        @Override
        public IdCard[] newArray(int size) {
            return new IdCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(num);
        dest.writeString(sex);
        dest.writeString(address);
        dest.writeString(start_date);
        dest.writeString(end_date);
        dest.writeString(issue);
    }
}
