package com.hahaxueche.presenter.findCoach;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.responseList.CoachResponseList;
import com.hahaxueche.model.user.User;
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
    private String filterDistance;
    private String filterPrice;
    private String goldenCoachOnly;
    private int vipOnly = 0;
    private String licenseType;
    private int cityId = 0;

    public void attachView(FindCoachView view) {
        this.mFindCoachView = view;
        application = HHBaseApplication.get(mFindCoachView.getContext());
        initFilters();
    }

    public void detachView() {
        this.mFindCoachView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void setFilters(String distance, String price, boolean isGoldenCoachOnly,
                           boolean isVipOnly, boolean C1Checked, boolean C2Checked) {
        filterDistance = distance;
        filterPrice = price;
        if (isGoldenCoachOnly) {
            goldenCoachOnly = "1";
        } else {
            goldenCoachOnly = "";
        }
        vipOnly = isVipOnly ? 1 : 0;
        if (C1Checked && C2Checked) {
            licenseType = "";
        } else if (C1Checked) {
            licenseType = "1";
        } else if (C2Checked) {
            licenseType = "2";
        }
    }

    private void initFilters() {
        HHBaseApplication application = HHBaseApplication.get(mFindCoachView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user != null) {
            cityId = user.student.city_id;
        }
        final City myCity = application.getConstants().getMyCity(cityId);
        String distance = String.valueOf(myCity.filters.radius[myCity.filters.radius.length - 2]);
        String price = String.valueOf(myCity.filters.prices[myCity.filters.prices.length - 1]);
        setFilters(distance, price, false, false, false, false);
    }

    public void fetchCoaches() {
        HHApiService apiService = application.getApiService();
        apiService.getCoaches(PAGE, PER_PAGE, TextUtils.isEmpty(goldenCoachOnly) ? null : goldenCoachOnly,
                TextUtils.isEmpty(licenseType) ? null : licenseType, TextUtils.isEmpty(filterPrice) ? null : filterPrice, cityId,
                null, TextUtils.isEmpty(filterDistance) ? null : filterDistance, null, 0, vipOnly, null)
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
