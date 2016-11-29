package com.hahaxueche.model.user.coach;

import android.os.Parcel;
import android.os.Parcelable;

import com.hahaxueche.model.user.student.Student;

/**
 * Created by wangshirui on 16/10/6.
 */

public class Review implements Parcelable{
    public String id;
    public Student reviewer;
    public Coach reviewee;
    public String comment;
    public String rating;
    public String active;
    public String created_at;
    public String updated_at;
    public String service_type;

    protected Review(Parcel in) {
        id = in.readString();
        reviewee = in.readParcelable(Coach.class.getClassLoader());
        reviewer = in.readParcelable(Student.class.getClassLoader());
        comment = in.readString();
        rating = in.readString();
        active = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
        service_type = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(reviewee, flags);
        dest.writeParcelable(reviewer, flags);
        dest.writeString(comment);
        dest.writeString(rating);
        dest.writeString(active);
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeString(service_type);
    }
}
