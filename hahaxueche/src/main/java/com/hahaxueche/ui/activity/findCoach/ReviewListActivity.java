package com.hahaxueche.ui.activity.findCoach;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.model.findCoach.GetReviewsResponse;
import com.hahaxueche.model.findCoach.ReviewInfo;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.ui.adapter.findCoach.ReviewItemAdapter;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 评论列表activity
 * Created by gibxin on 2016/2/27.
 */
public class ReviewListActivity extends FCBaseActivity implements XListView.IXListViewListener {
    private List<ReviewInfo> mReviewInfoList;
    private ReviewItemAdapter mReviewItemAdapter;
    private XListView xlvReviewList;
    private String linkSelf;
    private String linkNext;
    private String linkPrevious;
    private String page;
    private String per_page = "10";
    private String coach_user_id;
    private boolean isOnLoadMore = false;
    private TextView tvRlCommentCounts;//学员评价数量
    private ScoreView svRlAverageRating;//综合得分
    private CoachModel mCoach;
    private ImageButton ibtnReviewListBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);
        Intent intent = getIntent();
        GetReviewsResponse getReviewsResponse = (GetReviewsResponse) intent.getSerializableExtra("getReviewsResponse");
        mCoach = (CoachModel) intent.getSerializableExtra("coach");
        linkSelf = getReviewsResponse.getLinks().getSelf();
        linkNext = getReviewsResponse.getLinks().getNext();
        linkPrevious = getReviewsResponse.getLinks().getPrevious();
        mReviewInfoList = getReviewsResponse.getData();
        initView();
        initEvent();
        xlvReviewList.setPullRefreshEnable(true);
        xlvReviewList.setPullLoadEnable(true);
        xlvReviewList.setAutoLoadEnable(true);
        xlvReviewList.setXListViewListener(this);
        xlvReviewList.setRefreshTime(getTime());
        if (TextUtils.isEmpty(linkNext)) {
            xlvReviewList.setPullLoadEnable(false);
        } else {
            xlvReviewList.setPullLoadEnable(true);
        }
        mReviewItemAdapter = new ReviewItemAdapter(this, mReviewInfoList, R.layout.view_review_list_item);
        xlvReviewList.setAdapter(mReviewItemAdapter);
    }

    private void initView() {
        xlvReviewList = Util.instence(this).$(this, R.id.xlv_review_list);
        tvRlCommentCounts = Util.instence(this).$(this, R.id.tv_rl_comments_count);
        svRlAverageRating = Util.instence(this).$(this, R.id.sv_rl_average_rating);
        ibtnReviewListBack = Util.instence(this).$(this, R.id.ibtn_review_list_back);
        //学员评价数量
        tvRlCommentCounts.setText("学员评价（" + mCoach.getReview_count() + "）");
        //综合得分
        float averageRating = 0;
        if (!TextUtils.isEmpty(mCoach.getAverage_rating())) {
            averageRating = Float.parseFloat(mCoach.getAverage_rating());
        }
        if (averageRating > 5) {
            averageRating = 5;
        }
        svRlAverageRating.setScore(averageRating, true);
    }

    private void initEvent() {
        ibtnReviewListBack.setOnClickListener(mClickListener);
    }

    @Override
    public void onRefresh() {
        isOnLoadMore = false;
        getReviewList();
        if (TextUtils.isEmpty(linkNext)) {
            xlvReviewList.setPullLoadEnable(false);
        } else {
            xlvReviewList.setPullLoadEnable(true);
        }
    }

    @Override
    public void onLoadMore() {
        if (!TextUtils.isEmpty(linkNext) && !isOnLoadMore) {
            isOnLoadMore = true;
            getReviewList(linkNext);
        } else {
            onLoad();
        }
    }

    private void onLoad() {
        xlvReviewList.stopRefresh();
        xlvReviewList.stopLoadMore();
        xlvReviewList.setRefreshTime(getTime());
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
    }

    private void getReviewList() {
        this.fcPresenter.getReviewList(coach_user_id, page, per_page, new FCCallbackListener<GetReviewsResponse>() {
            @Override
            public void onSuccess(GetReviewsResponse data) {
                mReviewInfoList = data.getData();
                linkSelf = data.getLinks().getSelf();
                linkNext = data.getLinks().getNext();
                linkPrevious = data.getLinks().getPrevious();
                if (TextUtils.isEmpty(linkNext)) {
                    xlvReviewList.setPullLoadEnable(false);
                } else {
                    xlvReviewList.setPullLoadEnable(true);
                }
                mReviewItemAdapter = new ReviewItemAdapter(ReviewListActivity.this, mReviewInfoList, R.layout.view_review_list_item);
                xlvReviewList.setAdapter(mReviewItemAdapter);
                onLoad();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getReviewList(String url) {
        this.fcPresenter.getReviewList(url, new FCCallbackListener<GetReviewsResponse>() {
            @Override
            public void onSuccess(GetReviewsResponse data) {
                ArrayList<ReviewInfo> newReviewList = data.getData();
                if (newReviewList != null && newReviewList.size() > 0) {
                    mReviewInfoList.addAll(newReviewList);
                }
                linkSelf = data.getLinks().getSelf();
                linkNext = data.getLinks().getNext();
                linkPrevious = data.getLinks().getPrevious();
                if (TextUtils.isEmpty(linkNext)) {
                    xlvReviewList.setPullLoadEnable(false);
                } else {
                    xlvReviewList.setPullLoadEnable(true);
                }
                if (mReviewItemAdapter != null) {
                    mReviewItemAdapter.notifyDataSetChanged();
                } else {
                    mReviewItemAdapter = new ReviewItemAdapter(ReviewListActivity.this, mReviewInfoList, R.layout.view_review_list_item);
                    xlvReviewList.setAdapter(mReviewItemAdapter);
                }
                onLoad();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ibtn_review_list_back:
                    ReviewListActivity.this.finish();
                    break;
            }
        }
    };

}
