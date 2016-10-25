package com.hahaxueche.presenter.findCoach;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.responseList.PartnerResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PartnerListView;
import com.hahaxueche.util.HHLog;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 2016/10/19.
 */

public class PartnerListPresenter implements Presenter<PartnerListView> {
    private static final int PAGE = 1;
    private static final int PER_PAGE = 10;
    private PartnerListView mPartnerListView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String nextLink;
    private String filterPrice;
    private String licenseType;
    private int cityId = 0;
    private int sortBy = 0;

    public void attachView(PartnerListView view) {
        this.mPartnerListView = view;
        application = HHBaseApplication.get(mPartnerListView.getContext());
        initFilters();
    }

    public void detachView() {
        this.mPartnerListView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void setFilters(String price, boolean C1Checked, boolean C2Checked) {
        filterPrice = price;
        if (C1Checked && C2Checked) {
            licenseType = "";
        } else if (C1Checked) {
            licenseType = "1";
        } else if (C2Checked) {
            licenseType = "2";
        }
    }

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
    }

    private void initFilters() {
        HHBaseApplication application = HHBaseApplication.get(mPartnerListView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user != null) {
            cityId = user.student.city_id;
        }
        setFilters("", false, false);
    }

    public void fetchPartners() {
        String studentId = null;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            studentId = user.student.id;
        }
        HHApiService apiService = application.getApiService();
        subscription = apiService.getPartners(PAGE, PER_PAGE, TextUtils.isEmpty(licenseType) ? null : licenseType, "0",
                TextUtils.isEmpty(filterPrice) ? null : filterPrice, cityId, sortBy, studentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<PartnerResponseList>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mPartnerListView.showProgressDialog("查找中，请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mPartnerListView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        mPartnerListView.dismissProgressDialog();
                    }

                    @Override
                    public void onNext(PartnerResponseList PartnerResponseList) {
                        if (PartnerResponseList.data != null) {
                            mPartnerListView.refreshPartnerList(PartnerResponseList.data);
                            nextLink = PartnerResponseList.links.next;
                            mPartnerListView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }

                    }
                });
    }

    public void addMorePartneres() {
        if (TextUtils.isEmpty(nextLink)) return;
        HHApiService apiService = application.getApiService();
        subscription = apiService.getPartners(nextLink)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<PartnerResponseList>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(PartnerResponseList PartnerResponseList) {
                        if (PartnerResponseList.data != null) {
                            mPartnerListView.addMorePartnerList(PartnerResponseList.data);
                            nextLink = PartnerResponseList.links.next;
                            mPartnerListView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }
                    }
                });
    }
}
