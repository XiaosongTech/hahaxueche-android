package com.hahaxueche.presenter.findCoach;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.responseList.CoachResponseList;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.FindCoachView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/10/1.
 */

public class FindCoachPresenter implements Presenter<FindCoachView> {
    private static final int PAGE = 1;
    private static final int PER_PAGE = 10;
    private FindCoachView mFindCoachView;
    private Subscription subscription;
    private HHBaseApplication application;
    private ArrayList<Coach> mCoachArrayList;
    private String nextLink;

    public void attachView(FindCoachView view) {
        this.mFindCoachView = view;
        application = HHBaseApplication.get(mFindCoachView.getContext());
    }

    public void detachView() {
        this.mFindCoachView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void fetchCoaches() {
        HHApiService apiService = application.getApiService();
        apiService.getCoaches(PAGE, PER_PAGE, 0, 1, 350000, 0, null, 15, null, 0, 0, null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<CoachResponseList>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(CoachResponseList coachResponseList) {
                        if (coachResponseList.data != null) {
                            mFindCoachView.refreshCoachList(coachResponseList.data);
                            nextLink = coachResponseList.links.next;
                            mFindCoachView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }

                    }
                });
    }

    public void addMoreCoaches() {
        if (TextUtils.isEmpty(nextLink)) return;
        HHApiService apiService = application.getApiService();
        apiService.getCoaches(nextLink)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<CoachResponseList>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(CoachResponseList coachResponseList) {
                        if (coachResponseList.data != null) {
                            mFindCoachView.addMoreCoachList(coachResponseList.data);
                            nextLink = coachResponseList.links.next;
                            mFindCoachView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }
                    }
                });
    }
}
