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
    public String[] business_areas;
    public List<Coach> coaches;
    public int[] driving_school_ids;
    public double zone_center_lng;
    public double zone_center_lat;

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
        business_areas = in.createStringArray();
        coaches = in.createTypedArrayList(Coach.CREATOR);
        driving_school_ids = in.createIntArray();
        zone_center_lng = in.readDouble();
        zone_center_lat = in.readDouble();
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(section);
        dest.writeString(street);
        dest.writeString(province);
        dest.writeString(zip_code);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(deleted_at);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeInt(city_id);
        dest.writeString(image);
        dest.writeString(location);
        dest.writeInt(coach_count);
        dest.writeString(zone);
        dest.writeString(consult_phone);
        dest.writeString(display_address);
        dest.writeInt(lowest_price);
        dest.writeStringList(schools);
        dest.writeStringArray(business_areas);
        dest.writeTypedList(coaches);
        dest.writeIntArray(driving_school_ids);
        dest.writeDouble(zone_center_lng);
        dest.writeDouble(zone_center_lat);
    }
}
