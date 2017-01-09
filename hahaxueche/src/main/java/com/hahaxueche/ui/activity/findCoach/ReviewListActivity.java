package com.hahaxueche.ui.activity.findCoach;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.coach.Review;
import com.hahaxueche.presenter.findCoach.ReviewListPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.findCoach.ReviewAdapter;
import com.hahaxueche.ui.view.findCoach.ReviewListView;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/8.
 */

public class ReviewListActivity extends HHBaseActivity implements ReviewListView, XListView.IXListViewListener {
    private ReviewListPresenter mPresenter;
    @BindView(R.id.tv_comments_count)
    TextView mTvCommentsCount;
    @BindView(R.id.rb_average_rating)
    RatingBar mRbAverageRating;
    @BindView(R.id.xlv_reviews)
    XListView mXlvReviews;
    private ReviewAdapter mReviewAdapter;
    private ArrayList<Review> mReviewArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ReviewListPresenter();
        setContentView(R.layout.activity_review_list);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        Intent intent = getIntent();
        if (intent.getParcelableExtra("coach") != null) {
            mPresenter.setCoach((Coach) intent.getParcelableExtra("coach"));
            mXlvReviews.setPullRefreshEnable(true);
            mXlvReviews.setPullLoadEnable(true);
            mXlvReviews.setAutoLoadEnable(true);
            mXlvReviews.setXListViewListener(this);
            mPresenter.fetchReviews();
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("学员评价");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewListActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void setPullLoadEnable(boolean enable) {
        mXlvReviews.setPullLoadEnable(enable);
    }

    @Override
    public void refreshReviewList(ArrayList<Review> reviewArrayList) {
        mReviewArrayList = reviewArrayList;
        mReviewAdapter = new ReviewAdapter(getContext(), mReviewArrayList);
        mXlvReviews.setAdapter(mReviewAdapter);
        mXlvReviews.stopRefresh();
        mXlvReviews.stopLoadMore();
    }

    @Override
    public void addMoreReviewList(ArrayList<Review> reviewArrayList) {
        mReviewArrayList.addAll(reviewArrayList);
        mReviewAdapter.notifyDataSetChanged();
    }

    @Override
    public void setAverageRating(float score) {
        mRbAverageRating.setRating(score);
    }

    @Override
    public void setReviewedCount(String countText) {
        mTvCommentsCount.setText(countText);
    }

    @Override
    public void onRefresh() {
        mPresenter.fetchReviews();
    }

    @Override
    public void onLoadMore() {
        mPresenter.addMoreReviews();
    }
}
