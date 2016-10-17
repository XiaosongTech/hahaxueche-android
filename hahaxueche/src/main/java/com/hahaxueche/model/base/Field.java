package com.hahaxueche.model.base;

import android.os.Parcel;
import android.os.Parcelable;

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
    }
}
