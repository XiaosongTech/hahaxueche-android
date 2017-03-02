package com.hahaxueche.model.user.coach;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangshirui on 16/9/10.
 */
public class CoachGroup implements Parcelable {
    public String id;
    public String name;
    public String field_id;
    public boolean active;
    public int unit_training_cost;
    public int training_cost;
    public int market_price;
    public int other_fee;
    public int vip_price;
    public int vip_market_price;
    public int c2_price;
    public int c2_vip_price;
    public int group_type;

    protected CoachGroup(Parcel in) {
        id = in.readString();
        name = in.readString();
        field_id = in.readString();
        active = in.readByte() != 0;
        unit_training_cost = in.readInt();
        training_cost = in.readInt();
        market_price = in.readInt();
        other_fee = in.readInt();
        vip_price = in.readInt();
        vip_market_price = in.readInt();
        c2_price = in.readInt();
        c2_vip_price = in.readInt();
        group_type = in.readInt();
    }

    public static final Creator<CoachGroup> CREATOR = new Creator<CoachGroup>() {
        @Override
        public CoachGroup createFromParcel(Parcel in) {
            return new CoachGroup(in);
        }

        @Override
        public CoachGroup[] newArray(int size) {
            return new CoachGroup[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(field_id);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeInt(unit_training_cost);
        dest.writeInt(training_cost);
        dest.writeInt(market_price);
        dest.writeInt(other_fee);
        dest.writeInt(vip_price);
        dest.writeInt(vip_market_price);
        dest.writeInt(c2_price);
        dest.writeInt(c2_vip_price);
        dest.writeInt(group_type);
    }
}
