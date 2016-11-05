package com.hahaxueche.presenter.findCoach;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.responseList.CoachResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.CoachListView;
import com.hahaxueche.util.HHLog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/10/1.
 */

public class CoachListPresenter implements Presenter<CoachListView> {
    private static final int PAGE = 1;
    private static final int PER_PAGE = 10;
    private CoachListView mCoachListView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String nextLink;
    private String filterDistance;
    private String filterPrice;
    private String goldenCoachOnly;
    private int vipOnly = 0;
    private String licenseType;
    private int cityId = 0;
    private int sortBy = 0;
    private ArrayList<Field> selectFields;

    public void attachView(CoachListView view) {
        this.mCoachListView = view;
        application = HHBaseApplication.get(mCoachListView.getContext());
        initFilters();
    }

    public void detachView() {
        this.mCoachListView = null;
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

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
    }

    private void initFilters() {
        HHBaseApplication application = HHBaseApplication.get(mCoachListView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user != null) {
            cityId = user.student.city_id;
        }
        final City myCity = application.getConstants().getCity(cityId);
        //String distance = String.valueOf(myCity.filters.radius[myCity.filters.radius.length - 1]);
        //String price = String.valueOf(myCity.filters.prices[myCity.filters.prices.length - 1]);
        setFilters("", "", false, false, false, false);
    }

    public void fetchCoaches() {
        ArrayList<String> fieldIds = null;
        if (selectFields != null && selectFields.size() > 0) {
            fieldIds = new ArrayList<>();
            for (Field field : selectFields) {
                fieldIds.add(field.id);
            }
        }
        String studentId = null;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            studentId = user.student.id;
        }
        ArrayList<String> locations = null;
        if (application.getMyLocation() != null) {
            locations = new ArrayList<>();
            locations.add(String.valueOf(application.getMyLocation().lat));
            locations.add(String.valueOf(application.getMyLocation().lng));
        }
        HHApiService apiService = application.getApiService();
        subscription = apiService.getCoaches(PAGE, PER_PAGE, TextUtils.isEmpty(goldenCoachOnly) ? null : goldenCoachOnly,
                TextUtils.isEmpty(licenseType) ? null : licenseType, TextUtils.isEmpty(filterPrice) ? null : filterPrice, cityId,
                fieldIds, TextUtils.isEmpty(filterDistance) ? null : filterDistance, locations, sortBy, vipOnly, studentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<CoachResponseList>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mCoachListView.showProgressDialog("查找中，请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mCoachListView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        mCoachListView.dismissProgressDialog();
                    }

                    @Override
                    public void onNext(CoachResponseList coachResponseList) {
                        if (coachResponseList.data != null) {
                            mCoachListView.refreshCoachList(coachResponseList.data);
                            nextLink = coachResponseList.links.next;
                            mCoachListView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }

                    }
                });
    }

    public void addMoreCoaches() {
        if (TextUtils.isEmpty(nextLink)) return;
        HHApiService apiService = application.getApiService();
        subscription = apiService.getCoaches(nextLink)
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
                            mCoachListView.addMoreCoachList(coachResponseList.data);
                            nextLink = coachResponseList.links.next;
                            mCoachListView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }
                    }
                });
    }

    public ArrayList<Field> getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(ArrayList<Field> selectFields) {
        this.selectFields = selectFields;
    }

    public void setLocation(double lat, double lng) {
        application.setMyLocation(lat, lng);
    }

    public void clickFilterCount() {
        //筛选点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mCoachListView.getContext(), "find_coach_page_filter_tapped_tapped", map);
        } else {
            MobclickAgent.onEvent(mCoachListView.getContext(), "find_coach_page_filter_tapped_tapped");
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
        MobclickAgent.onEvent(mCoachListView.getContext(), "find_coach_page_sort_tapped", map);
    }

    public void clickCoach(String coachId) {
        //筛选点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        map.put("coach_id", coachId);
        MobclickAgent.onEvent(mCoachListView.getContext(), "find_coach_page_coach_tapped", map);
    }
}