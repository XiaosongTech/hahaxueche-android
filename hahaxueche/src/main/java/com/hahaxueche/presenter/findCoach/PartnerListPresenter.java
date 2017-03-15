package com.hahaxueche.presenter.findCoach;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.responseList.PartnerResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PartnerListView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.WebViewUrl;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 2016/10/19.
 */

public class PartnerListPresenter extends HHBasePresenter implements Presenter<PartnerListView> {
    private PartnerListView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String nextLink;
    private String filterPrice;
    private String licenseType;
    private int cityId = 0;
    private int sortBy = 0;

    public void attachView(PartnerListView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        initFilters();
    }

    public void detachView() {
        this.mView = null;
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
        HHBaseApplication application = HHBaseApplication.get(mView.getContext());
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
        subscription = apiService.getPartners(Common.START_PAGE, Common.PER_PAGE,
                TextUtils.isEmpty(licenseType) ? null : licenseType,
                TextUtils.isEmpty(filterPrice) ? null : filterPrice, cityId, sortBy, studentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<PartnerResponseList>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        mView.dismissProgressDialog();
                        mView.showRedBag(false);
                    }

                    @Override
                    public void onNext(PartnerResponseList PartnerResponseList) {
                        if (PartnerResponseList.data != null) {
                            mView.refreshPartnerList(PartnerResponseList.data);
                            nextLink = PartnerResponseList.links.next;
                            mView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                            mView.showRedBag(true);
                        } else {
                            mView.showRedBag(false);
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
                            mView.addMorePartnerList(PartnerResponseList.data);
                            nextLink = PartnerResponseList.links.next;
                            mView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }
                    }
                });
    }

    public void clickFilterCount() {
        //筛选点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "find_coach_page_filter_personal_coach_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "find_coach_page_filter_personal_coach_tapped");
        }

    }

    public void clickSortCount(int sortBy) {
        //筛选点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        map.put("sort_type", String.valueOf(sortBy));
        MobclickAgent.onEvent(mView.getContext(), "find_coach_page_sort_personal_coach_tapped", map);
    }

    public void clickPartner(String partnerId) {
        //筛选点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        map.put("partner_id", partnerId);
        MobclickAgent.onEvent(mView.getContext(), "find_coach_page_personal_coach_tapped", map);
    }

    public void clickRedBag() {
        //筛选点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "find_coach_flying_envelop_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "find_coach_flying_envelop_tapped");
        }
        mView.openWebView(WebViewUrl.WEB_URL_DALIBAO);
    }
}
