package com.hahaxueche.presenter.findCoach;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.CityConstants;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.base.LocalSettings;
import com.hahaxueche.model.responseList.CoachResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.CoachListView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;

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
    private String licenseType;
    private int sortBy = 0;
    private int startMoney = Common.NO_LIMIT;
    private int endMoney = Common.NO_LIMIT;
    private ArrayList<Field> selectFields;
    private String businessArea;
    private String zone;
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

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
    }

    public void setLicenseType(int license) {
        if (license == Common.LICENSE_TYPE_C1) {
            licenseType = "1";
        } else if (license == Common.LICENSE_TYPE_C2) {
            licenseType = "2";
        } else {
            licenseType = "";
        }
    }

    private void initDefaultFilters() {
        //默认价格最低
        sortBy = 3;
        filterDistance = "";
        licenseType = "";
        startMoney = Common.NO_LIMIT;
        endMoney = Common.NO_LIMIT;
        selectFields = null;
        businessArea = "";
        zone = "";
    }

    public void fetchCoaches() {
        int cityId = 0;
        LocalSettings localSettings = application.getSharedPrefUtil().getLocalSettings();
        if (localSettings.cityId > -1) {
            cityId = localSettings.cityId;
        }
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
                TextUtils.isEmpty(licenseType) ? null : licenseType, cityId,
                fieldIds, TextUtils.isEmpty(filterDistance) ? null : filterDistance,
                locations, sortBy, studentId, startMoney > 0 ? String.valueOf(startMoney) : null,
                endMoney > 0 ? String.valueOf(endMoney) : null,
                TextUtils.isEmpty(businessArea) ? null : businessArea, TextUtils.isEmpty(zone) ? null : zone)
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
                        mView.showHelp(false);
                    }

                    @Override
                    public void onNext(CoachResponseList coachResponseList) {
                        if (coachResponseList.data != null) {
                            mView.refreshCoachList(coachResponseList.data);
                            nextLink = coachResponseList.links.next;
                            mView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                            mView.showHelp(true);
                        } else {
                            mView.showHelp(false);
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

    public void clickFilterCount(int index) {
        //筛选点击
        HashMap<String, String> map = new HashMap();
        map.put("index", String.valueOf(index));
        addDataTrack("find_coach_filter_tapped", mView.getContext(), map);
    }

    public void clickSortCount(int sortBy) {
        //筛选点击
        HashMap<String, String> map = new HashMap();
        map.put("sort_type", String.valueOf(sortBy));
        addDataTrack("find_coach_page_sort_tapped", mView.getContext(), map);
    }

    public void clickCoach(String coachId) {
        //筛选点击
        HashMap<String, String> map = new HashMap();
        map.put("coach_id", coachId);
        addDataTrack("find_coach_page_coach_tapped", mView.getContext(), map);
    }

    public void setPriceRange(int startMoney, int endMoney) {
        this.startMoney = startMoney;
        this.endMoney = endMoney;
    }

    public void setDistance(int distance) {
        businessArea = "";
        zone = "";
        if (distance == Common.NO_LIMIT) {
            filterDistance = "";
        } else {
            filterDistance = String.valueOf(distance);
        }
    }

    public void setBusinessArea(String businessArea) {
        filterDistance = "";
        zone = "";
        this.businessArea = businessArea;
    }

    public void setZone(String zone) {
        filterDistance = "";
        businessArea = "";
        this.zone = zone;
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }

    public void resetFilter() {
        initDefaultFilters();
        fetchCoaches();
    }
}
