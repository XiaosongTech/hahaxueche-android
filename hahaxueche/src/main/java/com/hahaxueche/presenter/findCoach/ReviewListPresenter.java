package com.hahaxueche.presenter.findCoach;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.responseList.ReviewResponseList;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.ReviewListView;
import com.hahaxueche.util.HHLog;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 2016/10/8.
 */

public class ReviewListPresenter implements Presenter<ReviewListView> {
    private static final int PAGE = 1;
    private static final int PER_PAGE = 10;
    private ReviewListView mReviewListView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String nextLink;
    private Coach mCoach;

    public void attachView(ReviewListView view) {
        this.mReviewListView = view;
        application = HHBaseApplication.get(mReviewListView.getContext());
    }

    public void detachView() {
        this.mReviewListView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void fetchReviews() {
        if (mCoach == null) return;
        HHApiService apiService = application.getApiService();
        subscription = apiService.getReviews(mCoach.user_id, PAGE, PER_PAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ReviewResponseList>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(ReviewResponseList reviewResponseList) {
                        if (reviewResponseList.data != null) {
                            mReviewListView.refreshReviewList(reviewResponseList.data);
                            nextLink = reviewResponseList.links.next;
                            mReviewListView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }

                    }
                });
    }

    public void addMoreReviews() {
        if (TextUtils.isEmpty(nextLink)) return;
        HHApiService apiService = application.getApiService();
        subscription = apiService.getReviews(nextLink)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ReviewResponseList>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(ReviewResponseList reviewResponseList) {
                        if (reviewResponseList.data != null) {
                            mReviewListView.addMoreReviewList(reviewResponseList.data);
                            nextLink = reviewResponseList.links.next;
                            mReviewListView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }
                    }
                });
    }

    public void setCoach(Coach coach) {
        this.mCoach = coach;
        mReviewListView.setReviewedCount("学员评价（" + mCoach.review_count + "）");
        //综合得分
        float averageRating = 0;
        if (!TextUtils.isEmpty(mCoach.average_rating)) {
            averageRating = Float.parseFloat(mCoach.average_rating);
        }
        if (averageRating > 5) {
            averageRating = 5;
        }
        mReviewListView.setAverageRating(averageRating);
    }
}
