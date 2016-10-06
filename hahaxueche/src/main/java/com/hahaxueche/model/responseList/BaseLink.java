package com.hahaxueche.model.responseList;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangshirui on 16/9/27.
 */

public class BaseLink implements Parcelable{
    public String self;
    public String next;
    public String previous;

    protected BaseLink(Parcel in) {
        self = in.readString();
        next = in.readString();
        previous = in.readString();
    }

    public static final Creator<BaseLink> CREATOR = new Creator<BaseLink>() {
        @Override
        public BaseLink createFromParcel(Parcel in) {
            return new BaseLink(in);
        }

        @Override
        public BaseLink[] newArray(int size) {
            return new BaseLink[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(self);
        dest.writeString(next);
        dest.writeString(previous);
    }
}
