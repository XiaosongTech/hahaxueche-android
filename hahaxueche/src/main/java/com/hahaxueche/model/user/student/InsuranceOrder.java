package com.hahaxueche.model.user.student;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangshirui on 2017/3/1.
 */

public class InsuranceOrder implements Parcelable {
    public String policy_no;
    public int total_amount;
    public String paid_at;

    protected InsuranceOrder(Parcel in) {
        policy_no = in.readString();
        total_amount = in.readInt();
        paid_at = in.readString();
    }

    public static final Creator<InsuranceOrder> CREATOR = new Creator<InsuranceOrder>() {
        @Override
        public InsuranceOrder createFromParcel(Parcel in) {
            return new InsuranceOrder(in);
        }

        @Override
        public InsuranceOrder[] newArray(int size) {
            return new InsuranceOrder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(policy_no);
        dest.writeInt(total_amount);
        dest.writeString(paid_at);
    }
}
