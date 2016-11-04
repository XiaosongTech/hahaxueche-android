package com.hahaxueche.model.community;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/9/22.
 */

public class Article implements Parcelable {
    public String id;
    public String title;
    public String intro;
    public int is_popular;
    public String cover_image;
    public int category;
    public String created_at;
    public int view_count;
    public int like_count;
    public int liked;
    public ArrayList<Comment> comments;

    protected Article(Parcel in) {
        id = in.readString();
        title = in.readString();
        intro = in.readString();
        is_popular = in.readInt();
        cover_image = in.readString();
        category = in.readInt();
        created_at = in.readString();
        view_count = in.readInt();
        like_count = in.readInt();
        liked = in.readInt();
        comments = in.createTypedArrayList(Comment.CREATOR);
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(intro);
        dest.writeInt(is_popular);
        dest.writeString(cover_image);
        dest.writeInt(category);
        dest.writeString(created_at);
        dest.writeInt(view_count);
        dest.writeInt(like_count);
        dest.writeInt(liked);
        dest.writeTypedList(comments);
    }
}
