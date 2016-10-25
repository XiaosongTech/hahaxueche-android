package com.hahaxueche.model.user.coach;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangshirui on 2016/10/24.
 */

public class PartnerPrice implements Parcelable {
    public int license_type;
    public int duration;
    public int price;

    protected PartnerPrice(Parcel in) {
        license_type = in.readInt();
        duration = in.readInt();
        price = in.readInt();
    }

    public static final Creator<PartnerPrice> CREATOR = new Creator<PartnerPrice>() {
        @Override
        public PartnerPrice createFromParcel(Parcel in) {
            return new PartnerPrice(in);
        }

        @Override
        public PartnerPrice[] newArray(int size) {
            return new PartnerPrice[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(license_type);
        dest.writeInt(duration);
        dest.writeInt(price);
    }
}
