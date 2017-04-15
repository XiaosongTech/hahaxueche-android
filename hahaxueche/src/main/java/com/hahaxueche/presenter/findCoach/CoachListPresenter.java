package com.hahaxueche.presenter.findCoach;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.base.LocalSettings;
import com.hahaxueche.model.responseList.CoachResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.CoachListView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.WebViewUrl;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/10/1.
 */

public class CoachListPresenter extends HHBasePresenter implements Presenter<CoachListView> {
    private CoachListView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String nextLink;
    //-----筛选参数-----
    private String filterDistance;
    private String filterPrice;
    private String goldenCoachOnly;
    private int vipOnly = 0;
    private String licenseType;
    private int cityId = 0;
    private int sortBy = 0;
    private ArrayList<Field> selectFields;
    //-----end-----

    public void attachView(CoachListView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        initDefaultFilters();
    }

    public void detachView() {
        this.mView = null;
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

    private void initDefaultFilters() {
        LocalSettings localSettings = application.getSharedPrefUtil().getLocalSettings();
        if (localSettings.cityId > -1) {
            cityId = localSettings.cityId;
        }
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
        subscription = apiService.getCoaches(Common.START_PAGE, Common.PER_PAGE,
                TextUtils.isEmpty(goldenCoachOnly) ? null : goldenCoachOnly,
                TextUtils.isEmpty(licenseType) ? null : licenseType,
                TextUtils.isEmpty(filterPrice) ? null : filterPrice, cityId,
                fieldIds, TextUtils.isEmpty(filterDistance) ? null : filterDistance,
                locations, sortBy, vipOnly, studentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<CoachResponseList>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mView.showProgressDialog("查找中，请稍后...");
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
                    public void onNext(CoachResponseList coachResponseList) {
                        if (coachResponseList.data != null) {
                            mView.refreshCoachList(coachResponseList.data);
                            nextLink = coachResponseList.links.next;
                            mView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                            mView.showRedBag(true);
                        } else {
                            mView.showRedBag(false);
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
                            mView.addMoreCoachList(coachResponseList.data);
                            nextLink = coachResponseList.links.next;
                            mView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
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
            MobclickAgent.onEvent(mView.getContext(), "find_coach_page_filter_tapped_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "find_coach_page_filter_tapped_tapped");
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
        MobclickAgent.onEvent(mView.getContext(), "find_coach_page_sort_tapped", map);
    }

    public void clickCoach(String coachId) {
        //筛选点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        map.put("coach_id", coachId);
        MobclickAgent.onEvent(mView.getContext(), "find_coach_page_coach_tapped", map);
    }

    public void clickRedBag() {
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
