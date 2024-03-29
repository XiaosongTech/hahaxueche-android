package com.hahaxueche.model.user.coach;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/9/27.
 */

public class Coach implements Parcelable {
    public String id;
    public String cell_phone;
    public String name;
    public int city_id;
    public String user_id;
    public String avatar;
    public String bio;
    public String review_count;
    public String average_rating;
    public String total_student_count;
    public String active_student_count;
    public String account_balance;
    public String commission_ratio;
    public int experiences;
    public int skill_level;
    public String license_type;
    public int service_type;
    public String satisfaction_rate;
    public String consultant;
    public CoachGroup coach_group;
    public ArrayList<Coach> peer_coaches;
    public ArrayList<String> images;
    public String assigned_coaches;
    public int vip;
    public int like_count;
    public int liked;
    public String driving_school;
    public int driving_school_id;
    public String stage_two_pass_rate;
    public String stage_three_pass_rate;
    public String average_pass_days;
    public int has_cash_pledge;
    public String distance;
    public String consult_phone;
    public int consult_count;

    public Coach() {

    }

    protected Coach(Parcel in) {
        id = in.readString();
        cell_phone = in.readString();
        name = in.readString();
        city_id = in.readInt();
        user_id = in.readString();
        avatar = in.readString();
        bio = in.readString();
        review_count = in.readString();
        average_rating = in.readString();
        total_student_count = in.readString();
        active_student_count = in.readString();
        account_balance = in.readString();
        commission_ratio = in.readString();
        experiences = in.readInt();
        skill_level = in.readInt();
        license_type = in.readString();
        service_type = in.readInt();
        satisfaction_rate = in.readString();
        consultant = in.readString();
        coach_group = in.readParcelable(CoachGroup.class.getClassLoader());
        peer_coaches = in.createTypedArrayList(Coach.CREATOR);
        images = in.createStringArrayList();
        assigned_coaches = in.readString();
        vip = in.readInt();
        like_count = in.readInt();
        liked = in.readInt();
        driving_school = in.readString();
        driving_school_id = in.readInt();
        stage_two_pass_rate = in.readString();
        stage_three_pass_rate = in.readString();
        average_pass_days = in.readString();
        has_cash_pledge = in.readInt();
        distance = in.readString();
        consult_phone = in.readString();
        consult_count = in.readInt();
    }

    public static final Creator<Coach> CREATOR = new Creator<Coach>() {
        @Override
        public Coach createFromParcel(Parcel in) {
            return new Coach(in);
        }

        @Override
        public Coach[] newArray(int size) {
            return new Coach[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(cell_phone);
        parcel.writeString(name);
        parcel.writeInt(city_id);
        parcel.writeString(user_id);
        parcel.writeString(avatar);
        parcel.writeString(bio);
        parcel.writeString(review_count);
        parcel.writeString(average_rating);
        parcel.writeString(total_student_count);
        parcel.writeString(active_student_count);
        parcel.writeString(account_balance);
        parcel.writeString(commission_ratio);
        parcel.writeInt(experiences);
        parcel.writeInt(skill_level);
        parcel.writeString(license_type);
        parcel.writeInt(service_type);
        parcel.writeString(satisfaction_rate);
        parcel.writeString(consultant);
        parcel.writeParcelable(coach_group, i);
        parcel.writeTypedList(peer_coaches);
        parcel.writeStringList(images);
        parcel.writeString(assigned_coaches);
        parcel.writeInt(vip);
        parcel.writeInt(like_count);
        parcel.writeInt(liked);
        parcel.writeString(driving_school);
        parcel.writeInt(driving_school_id);
        parcel.writeString(stage_two_pass_rate);
        parcel.writeString(stage_three_pass_rate);
        parcel.writeString(average_pass_days);
        parcel.writeInt(has_cash_pledge);
        parcel.writeString(distance);
        parcel.writeString(consult_phone);
        parcel.writeInt(consult_count);
    }
}
