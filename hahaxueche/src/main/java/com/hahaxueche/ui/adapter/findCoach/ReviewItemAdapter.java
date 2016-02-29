package com.hahaxueche.ui.adapter.findCoach;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.findCoach.ReviewInfo;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by gibxin on 2016/2/27.
 */
public class ReviewItemAdapter extends BaseAdapter {
    private List<ReviewInfo> mReviewInfoList;
    private int mResource;   //item的布局
    private Context mContext;
    private LayoutInflater inflator;
    private TextView tvReviewerName;//评论人名称
    private CircleImageView civReviewerAvatar;//评论人头像
    private TextView tvReviewDate;//评论日期
    private ScoreView svReviewRating;//评分
    private TextView tvReviewComment;//评论

    public ReviewItemAdapter(Context context, List<ReviewInfo> reviewInfoList, int resource) {
        mContext = context;
        mReviewInfoList = reviewInfoList;
        mResource = resource;
    }

    @Override
    public int getCount() {
        return mReviewInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mReviewInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(mResource, null);
            tvReviewerName = (TextView) convertView.findViewById(R.id.tv_reviewer_name);//评论人名称
            civReviewerAvatar = (CircleImageView) convertView.findViewById(R.id.cir_reviewer_avatar);//评论人头像
            tvReviewDate = (TextView) convertView.findViewById(R.id.tv_review_date);//评论日期
            svReviewRating = (ScoreView) convertView.findViewById(R.id.sv_review_rating);//评分
            tvReviewComment = (TextView) convertView.findViewById(R.id.tv_review_comment);
        }
        ReviewInfo reviewInfo = mReviewInfoList.get(position);
        tvReviewerName.setText(reviewInfo.getReviewer().getName());
        tvReviewComment.setText(reviewInfo.getComment());
        final int iconWidth = Util.instence(mContext).dip2px(40);
        final int iconHeight = iconWidth;
        Picasso.with(mContext).load(reviewInfo.getReviewer().getAvatar_url()).resize(iconWidth, iconHeight)
                .into(civReviewerAvatar);
        String reviewDate = "";
        if (!TextUtils.isEmpty(reviewInfo.getUpdated_at())) {
            reviewDate = reviewInfo.getUpdated_at().substring(0, 10);
        }
        tvReviewDate.setText(reviewDate);
        float reviewerRating = 0;
        if (!TextUtils.isEmpty(reviewInfo.getRating())) {
            reviewerRating = Float.parseFloat(reviewInfo.getRating());
        }
        if (reviewerRating > 5) {
            reviewerRating = 5;
        }
        svReviewRating.setScore(reviewerRating, false);
        return convertView;
    }
}
