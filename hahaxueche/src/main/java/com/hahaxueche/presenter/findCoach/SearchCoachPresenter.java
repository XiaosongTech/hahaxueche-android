package com.hahaxueche.presenter.findCoach;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.SearchCoachView;
import com.hahaxueche.util.SharedPrefUtil;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/10/4.
 */

public class SearchCoachPresenter implements Presenter<SearchCoachView> {
    private SearchCoachView mSearchCoachView;
    private Subscription subscription;
    private HHBaseApplication application;
    private SharedPrefUtil spUtil;

    @Override
    public void attachView(SearchCoachView view) {
        this.mSearchCoachView = view;
        application = HHBaseApplication.get(mSearchCoachView.getContext());
        spUtil = application.getSharedPrefUtil();
        loadHistory();
    }

    @Override
    public void detachView() {
        this.mSearchCoachView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        spUtil = null;
    }


    public void searchCoach(final String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            mSearchCoachView.showMessage("请输入搜索内容");
            return;
        }
        mSearchCoachView.disableButton();
        HHApiService apiService = application.getApiService();
        subscription = apiService.getCoachesByKeyword(keyword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ArrayList<Coach>>() {
                    @Override
                    public void onCompleted() {
                        mSearchCoachView.enableButton();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSearchCoachView.enableButton();
                    }

                    @Override
                    public void onNext(ArrayList<Coach> coachArrayList) {
                        spUtil.addSearchHistory(keyword);
                        mSearchCoachView.loadCoachList(coachArrayList);
                    }
                });
    }


    public void loadHistory() {
        mSearchCoachView.loadSearchHistory(spUtil.getSearchHistory());
    }

    public void clearHistory() {
        spUtil.clearSearchHistory();
        mSearchCoachView.loadSearchHistory(null);
    }

    public void searchTextChange(String s) {
        if (!TextUtils.isEmpty(s)) {
            mSearchCoachView.setRightSearch();
        } else {
            mSearchCoachView.setRightCancel();
        }
    }
}
