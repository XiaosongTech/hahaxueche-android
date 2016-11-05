package com.hahaxueche.model.payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangshirui on 16/9/10.
 */
public class BankCard implements Parcelable{
    public String name;
    public String card_number;
    public String open_bank_code;
    public String bank_name;

    protected BankCard(Parcel in) {
        name = in.readString();
        card_number = in.readString();
        open_bank_code = in.readString();
        bank_name = in.readString();
    }

    public static final Creator<BankCard> CREATOR = new Creator<BankCard>() {
        @Override
        public BankCard createFromParcel(Parcel in) {
            return new BankCard(in);
        }

        @Override
        public BankCard[] newArray(int size) {
            return new BankCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(card_number);
        dest.writeString(open_bank_code);
        dest.writeString(bank_name);
    }
}
