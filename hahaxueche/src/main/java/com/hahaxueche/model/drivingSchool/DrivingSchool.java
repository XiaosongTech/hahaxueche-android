package com.hahaxueche.model.drivingSchool;

import android.os.Parcel;
import android.os.Parcelable;

import com.hahaxueche.model.base.Field;

import java.util.List;

/**
 * Created by wangshirui on 2017/4/13.
 */

public class DrivingSchool implements Parcelable{
    public int id;
    public String name;
    public int coach_count;
    public int field_count;
    public int lowest_price;
    public float rating;
    public int review_count;
    public int like_count;
    public String pass_rate;
    public String avatar;
    public List<String> images;
    public List<String> zones;
    public int lowest_vip_price;
    public int consult_count;
    public String consult_phone;
    public String distance;
    public String closest_zone;
    public String bio;
    public List<String> tag_list;
    public List<Field> fields;
    public int groupon_count;

    protected DrivingSchool(Parcel in) {
        id = in.readInt();
        name = in.readString();
        coach_count = in.readInt();
        field_count = in.readInt();
        lowest_price = in.readInt();
        rating = in.readFloat();
        review_count = in.readInt();
        like_count = in.readInt();
        pass_rate = in.readString();
        avatar = in.readString();
        images = in.createStringArrayList();
        zones = in.createStringArrayList();
        lowest_vip_price = in.readInt();
        consult_count = in.readInt();
        consult_phone = in.readString();
        distance = in.readString();
        closest_zone = in.readString();
        bio = in.readString();
        tag_list = in.createStringArrayList();
        fields = in.createTypedArrayList(Field.CREATOR);
        groupon_count = in.readInt();
    }

    public static final Creator<DrivingSchool> CREATOR = new Creator<DrivingSchool>() {
        @Override
        public DrivingSchool createFromParcel(Parcel in) {
            return new DrivingSchool(in);
        }

        @Override
        public DrivingSchool[] newArray(int size) {
            return new DrivingSchool[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeInt(coach_count);
        parcel.writeInt(field_count);
        parcel.writeInt(lowest_price);
        parcel.writeFloat(rating);
        parcel.writeInt(review_count);
        parcel.writeInt(like_count);
        parcel.writeString(pass_rate);
        parcel.writeString(avatar);
        parcel.writeStringList(images);
        parcel.writeStringList(zones);
        parcel.writeInt(lowest_vip_price);
        parcel.writeInt(consult_count);
        parcel.writeString(consult_phone);
        parcel.writeString(distance);
        parcel.writeString(closest_zone);
        parcel.writeString(bio);
        parcel.writeStringList(tag_list);
        parcel.writeTypedList(fields);
        parcel.writeInt(groupon_count);
    }
}
