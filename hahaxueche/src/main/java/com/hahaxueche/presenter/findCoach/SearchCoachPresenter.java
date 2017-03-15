package com.hahaxueche.presenter.findCoach;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.HHBasePresenter;
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

public class SearchCoachPresenter extends HHBasePresenter implements Presenter<SearchCoachView> {
    private SearchCoachView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private SharedPrefUtil spUtil;

    @Override
    public void attachView(SearchCoachView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        spUtil = application.getSharedPrefUtil();
        loadHistory();
    }

    @Override
    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        spUtil = null;
    }


    public void searchCoach(final String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            mView.showMessage("请输入搜索内容");
            return;
        }
        mView.disableButton();
        HHApiService apiService = application.getApiService();
        subscription = apiService.getCoachesByKeyword(keyword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ArrayList<Coach>>() {
                    @Override
                    public void onCompleted() {
                        mView.enableButton();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.enableButton();
                    }

                    @Override
                    public void onNext(ArrayList<Coach> coachArrayList) {
                        spUtil.addSearchHistory(keyword);
                        mView.loadCoachList(coachArrayList);
                    }
                });
    }


    public void loadHistory() {
        mView.loadSearchHistory(spUtil.getSearchHistory());
    }

    public void clearHistory() {
        spUtil.clearSearchHistory();
        mView.loadSearchHistory(null);
    }

    public void searchTextChange(String s) {
        if (!TextUtils.isEmpty(s)) {
            mView.setRightSearch();
        } else {
            mView.setRightCancel();
        }
    }
}
