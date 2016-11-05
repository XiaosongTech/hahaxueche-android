package com.hahaxueche.ui.adapter.findCoach;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.user.coach.Review;
import com.hahaxueche.ui.widget.scoreView.ScoreView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/8.
 */

public class ReviewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<Review> mReviewList;

    public ReviewAdapter(Context context, ArrayList<Review> reviewArrayList) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mReviewList = reviewArrayList;
    }

    @Override
    public int getCount() {
        return mReviewList.size();
    }

    @Override
    public Object getItem(int position) {
        return mReviewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.adapter_review, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        Review review = mReviewList.get(position);
        holder.ivAvatar.setImageURI(review.reviewer.avatar);
        holder.tvName.setText(review.reviewer.name);
        holder.tvDate.setText(review.updated_at.substring(0, 10));
        float reviewerRating = 0;
        if (!TextUtils.isEmpty(review.rating)) {
            reviewerRating = Float.parseFloat(review.rating);
        }
        if (reviewerRating > 5) {
            reviewerRating = 5;
        }
        holder.svScore.setScore(reviewerRating, false);
        holder.tvComment.setText(review.comment);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_avatar)
        SimpleDraweeView ivAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_score)
        ScoreView svScore;
        @BindView(R.id.tv_comment)
        TextView tvComment;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
