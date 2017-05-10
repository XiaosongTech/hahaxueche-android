package com.hahaxueche.model.base;

import android.os.Parcel;
import android.os.Parcelable;

import com.hahaxueche.model.user.coach.Coach;

import java.util.List;

/**
 * Created by wangshirui on 16/9/8.
 */
public class Field implements Parcelable {
    public String id;
    public String name;
    public String description;
    public String section;
    public String street;
    public String province;
    public String zip_code;
    public double lat;
    public double lng;
    public String deleted_at;
    public boolean active;
    public String created_at;
    public String updated_at;
    public int city_id;
    public String image;
    public String location;
    public int coach_count;
    public String zone;
    public String consult_phone;
    public String display_address;
    public int lowest_price;
    public List<String> schools;
    public List<String> business_areas;
    public List<Coach> coaches;

    protected Field(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        section = in.readString();
        street = in.readString();
        province = in.readString();
        zip_code = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        deleted_at = in.readString();
        active = in.readByte() != 0;
        created_at = in.readString();
        updated_at = in.readString();
        city_id = in.readInt();
        image = in.readString();
        location = in.readString();
        coach_count = in.readInt();
        zone = in.readString();
        consult_phone = in.readString();
        display_address = in.readString();
        lowest_price = in.readInt();
        schools = in.createStringArrayList();
        business_areas = in.createStringArrayList();
        coaches = in.createTypedArrayList(Coach.CREATOR);
    }

    public static final Creator<Field> CREATOR = new Creator<Field>() {
        @Override
        public Field createFromParcel(Parcel in) {
            return new Field(in);
        }

        @Override
        public Field[] newArray(int size) {
            return new Field[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(section);
        parcel.writeString(street);
        parcel.writeString(province);
        parcel.writeString(zip_code);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeString(deleted_at);
        parcel.writeByte((byte) (active ? 1 : 0));
        parcel.writeString(created_at);
        parcel.writeString(updated_at);
        parcel.writeInt(city_id);
        parcel.writeString(image);
        parcel.writeString(location);
        parcel.writeInt(coach_count);
        parcel.writeString(zone);
        parcel.writeString(consult_phone);
        parcel.writeString(display_address);
        parcel.writeInt(lowest_price);
        parcel.writeStringList(schools);
        parcel.writeStringList(business_areas);
        parcel.writeTypedList(coaches);
    }
}
