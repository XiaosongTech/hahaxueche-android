package com.hahaxueche.model.user.coach;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/10/19.
 */

public class Partner implements Parcelable {
    public String id;
    public int city_id;
    public String name;
    public String phone;
    public String experiences;
    public String description;
    public String avatar;
    public ArrayList<String> images;
    public int like_count;
    public ArrayList<PartnerPrice> prices;
    public int liked;

    protected Partner(Parcel in) {
        id = in.readString();
        city_id = in.readInt();
        name = in.readString();
        phone = in.readString();
        experiences = in.readString();
        description = in.readString();
        avatar = in.readString();
        images = in.createStringArrayList();
        like_count = in.readInt();
        prices = in.createTypedArrayList(PartnerPrice.CREATOR);
        liked = in.readInt();
    }

    public static final Creator<Partner> CREATOR = new Creator<Partner>() {
        @Override
        public Partner createFromParcel(Parcel in) {
            return new Partner(in);
        }

        @Override
        public Partner[] newArray(int size) {
            return new Partner[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(city_id);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(experiences);
        dest.writeString(description);
        dest.writeString(avatar);
        dest.writeStringList(images);
        dest.writeInt(like_count);
        dest.writeTypedList(prices);
        dest.writeInt(liked);
    }
}
