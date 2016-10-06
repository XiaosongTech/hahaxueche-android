package com.hahaxueche.model.responseList;

import android.os.Parcel;
import android.os.Parcelable;

import com.hahaxueche.model.user.coach.Review;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/10/6.
 */

public class ReviewResponseList implements Parcelable {
    public BaseLink links;
    public ArrayList<Review> data;

    protected ReviewResponseList(Parcel in) {
        links = in.readParcelable(BaseLink.class.getClassLoader());
        data = in.createTypedArrayList(Review.CREATOR);
    }

    public static final Creator<ReviewResponseList> CREATOR = new Creator<ReviewResponseList>() {
        @Override
        public ReviewResponseList createFromParcel(Parcel in) {
            return new ReviewResponseList(in);
        }

        @Override
        public ReviewResponseList[] newArray(int size) {
            return new ReviewResponseList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(links, flags);
        dest.writeTypedList(data);
    }
}
