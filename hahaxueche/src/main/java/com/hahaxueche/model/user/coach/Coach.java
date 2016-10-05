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
    public String city_id;
    public String user_id;
    public String avatar;
    public String bio;
    public String review_count;
    public String average_rating;
    public String total_student_count;
    public String active_student_count;
    public String account_balance;
    public String commission_ratio;
    public String experiences;
    public String skill_level;
    public String skill_level_label;
    public String license_type;
    public String service_type;
    public String satisfaction_rate;
    public String consultant;
    public CoachGroup coach_group;
    public ArrayList<Coach> peer_coaches;
    public ArrayList<String> images;
    public String assigned_coaches;
    public int vip;
    public int like_count;
    public String liked;
    public String driving_school;

    protected Coach(Parcel in) {
        id = in.readString();
        cell_phone = in.readString();
        name = in.readString();
        city_id = in.readString();
        user_id = in.readString();
        avatar = in.readString();
        bio = in.readString();
        review_count = in.readString();
        average_rating = in.readString();
        total_student_count = in.readString();
        active_student_count = in.readString();
        account_balance = in.readString();
        commission_ratio = in.readString();
        experiences = in.readString();
        skill_level = in.readString();
        skill_level_label = in.readString();
        license_type = in.readString();
        service_type = in.readString();
        satisfaction_rate = in.readString();
        consultant = in.readString();
        peer_coaches = in.createTypedArrayList(Coach.CREATOR);
        images = in.createStringArrayList();
        assigned_coaches = in.readString();
        vip = in.readInt();
        like_count = in.readInt();
        liked = in.readString();
        driving_school = in.readString();
        coach_group = in.readParcelable(CoachGroup.class.getClassLoader());
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(cell_phone);
        dest.writeString(name);
        dest.writeString(city_id);
        dest.writeString(user_id);
        dest.writeString(avatar);
        dest.writeString(bio);
        dest.writeString(review_count);
        dest.writeString(average_rating);
        dest.writeString(total_student_count);
        dest.writeString(active_student_count);
        dest.writeString(account_balance);
        dest.writeString(commission_ratio);
        dest.writeString(experiences);
        dest.writeString(skill_level);
        dest.writeString(skill_level_label);
        dest.writeString(license_type);
        dest.writeString(service_type);
        dest.writeString(satisfaction_rate);
        dest.writeString(consultant);
        dest.writeTypedList(peer_coaches);
        dest.writeStringList(images);
        dest.writeString(assigned_coaches);
        dest.writeInt(vip);
        dest.writeInt(like_count);
        dest.writeString(liked);
        dest.writeString(driving_school);
        dest.writeParcelable(coach_group, flags);
    }
}
