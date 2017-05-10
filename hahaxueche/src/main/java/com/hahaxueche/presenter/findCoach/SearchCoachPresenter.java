package com.hahaxueche.presenter.findCoach;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.LocalSettings;
import com.hahaxueche.model.responseList.DrivingSchoolResponseList;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.SearchCoachView;
import com.hahaxueche.util.Common;
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
    private int mSearchType;

    @Override
    public void attachView(SearchCoachView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        spUtil = application.getSharedPrefUtil();
        //默认驾校搜索
        selectSearchType(Common.SEARCH_TYPE_DRIVING_SCHOOL);
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
        int cityId = 0;
        LocalSettings localSettings = application.getSharedPrefUtil().getLocalSettings();
        if (localSettings.cityId > -1) {
            cityId = localSettings.cityId;
        }
        HHApiService apiService = application.getApiService();
        if (mSearchType == Common.SEARCH_TYPE_DRIVING_SCHOOL) {
            subscription = apiService.getDrivingSchoolsByKeyword(keyword, cityId, Common.START_PAGE, 100)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<DrivingSchoolResponseList>() {
                        @Override
                        public void onCompleted() {
                            mView.enableButton();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.enableButton();
                        }

                        @Override
                        public void onNext(DrivingSchoolResponseList responseList) {
                            spUtil.addSearchDrivingSchoolHistory(keyword);
                            mView.loadDrivingSchoolList(responseList.data);
                        }
                    });
        } else {
            subscription = apiService.getCoachesByKeyword(keyword, cityId)
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
                            spUtil.addSearchCoachHistory(keyword);
                            mView.loadCoachList(coachArrayList);
                        }
                    });
        }
    }


    private void loadHistory() {
        if (mSearchType == Common.SEARCH_TYPE_DRIVING_SCHOOL) {
            mView.loadSearchHistory(spUtil.getSearchDrivingSchoolHistory());
        } else {
            mView.loadSearchHistory(spUtil.getSearchCoachHistory());
        }
    }

    public void clearHistory() {
        if (mSearchType == Common.SEARCH_TYPE_DRIVING_SCHOOL) {
            spUtil.clearSearchDrivingSchoolHistory();
        } else {
            spUtil.clearSearchCoachHistory();
        }
        mView.loadSearchHistory(null);
    }

    public void searchTextChange(String s) {
        if (!TextUtils.isEmpty(s)) {
            mView.setRightSearch();
        } else {
            mView.setRightCancel();
        }
    }

    public void selectSearchType(int type) {
        mSearchType = type;
        if (mSearchType == Common.SEARCH_TYPE_DRIVING_SCHOOL) {
            mView.setSearch("驾校");
        } else {
            mView.setSearch("教练");
        }
        loadHistory();
    }
}
