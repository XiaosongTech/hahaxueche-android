package com.hahaxueche.model.community;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangshirui on 2016/11/3.
 */

public class Comment implements Parcelable {
    public String student_id;
    public String student_avatar;
    public String student_name;
    public String name;
    public String content;
    public String created_at;

    protected Comment(Parcel in) {
        student_id = in.readString();
        student_avatar = in.readString();
        student_name = in.readString();
        name = in.readString();
        content = in.readString();
        created_at = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(student_id);
        dest.writeString(student_avatar);
        dest.writeString(student_name);
        dest.writeString(name);
        dest.writeString(content);
        dest.writeString(created_at);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
