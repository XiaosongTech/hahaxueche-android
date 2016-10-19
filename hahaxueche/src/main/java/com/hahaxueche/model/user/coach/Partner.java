package com.hahaxueche.model.user.coach;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/10/19.
 */

public class Partner implements Parcelable {
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
    public int experiences;
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
    public int liked;
    public String driving_school;
    public String stage_two_pass_rate;
    public String stage_three_pass_rate;
    public String average_pass_days;

    protected Partner(Parcel in) {
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
        experiences = in.readInt();
        skill_level = in.readString();
        skill_level_label = in.readString();
        license_type = in.readString();
        service_type = in.readString();
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
        stage_two_pass_rate = in.readString();
        stage_three_pass_rate = in.readString();
        average_pass_days = in.readString();
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
        dest.writeInt(experiences);
        dest.writeString(skill_level);
        dest.writeString(skill_level_label);
        dest.writeString(license_type);
        dest.writeString(service_type);
        dest.writeString(satisfaction_rate);
        dest.writeString(consultant);
        dest.writeParcelable(coach_group, flags);
        dest.writeTypedList(peer_coaches);
        dest.writeStringList(images);
        dest.writeString(assigned_coaches);
        dest.writeInt(vip);
        dest.writeInt(like_count);
        dest.writeInt(liked);
        dest.writeString(driving_school);
        dest.writeString(stage_two_pass_rate);
        dest.writeString(stage_three_pass_rate);
        dest.writeString(average_pass_days);
    }
}
