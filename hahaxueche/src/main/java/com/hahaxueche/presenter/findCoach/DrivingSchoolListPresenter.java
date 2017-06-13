package com.hahaxueche.presenter.findCoach;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.CityConstants;
import com.hahaxueche.model.base.LocalSettings;
import com.hahaxueche.model.responseList.DrivingSchoolResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.DrivingSchoolListView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 2017/5/3.
 */

public class DrivingSchoolListPresenter extends HHBasePresenter implements Presenter<DrivingSchoolListView> {
    private DrivingSchoolListView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String nextLink;
    //-----筛选参数-----
    private String filterDistance;
    private String licenseType;
    private String sortBy = "";
    private String order = "";
    private String businessArea = "";
    private String zone = "";
    private int startMoney = Common.NO_LIMIT;
    private int endMoney = Common.NO_LIMIT;
    //-----end-----

    public void attachView(DrivingSchoolListView view) {
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
        if (sortBy == 1) {
            //距离最近
            this.sortBy = "distance";
            this.order = "asc";
        } else if (sortBy == 5) {
            //评论最多
            this.sortBy = "review_count";
            this.order = "desc";
        } else if (sortBy == 3) {
            //价格最低
            this.sortBy = "price";
            this.order = "asc";
        } else {
            //综合排序
            this.sortBy = "";
            this.order = "asc";
        }
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
        //默认综合排序
        sortBy = "";
        this.order = "asc";
        filterDistance = "";
        licenseType = "";
        businessArea = "";
        zone = "";
        startMoney = Common.NO_LIMIT;
        endMoney = Common.NO_LIMIT;
    }

    public void fetchDrivingSchool() {
        int cityId = 0;
        LocalSettings localSettings = application.getSharedPrefUtil().getLocalSettings();
        if (localSettings.cityId > -1) {
            cityId = localSettings.cityId;
        }
        ArrayList<String> locations = null;
        if (application.getMyLocation() != null) {
            locations = new ArrayList<>();
            locations.add(String.valueOf(application.getMyLocation().lat));
            locations.add(String.valueOf(application.getMyLocation().lng));
        }
        HHApiService apiService = application.getApiService();
        subscription = apiService.getDrivingSchools(Common.START_PAGE, Common.PER_PAGE,
                TextUtils.isEmpty(licenseType) ? null : licenseType, cityId,
                TextUtils.isEmpty(filterDistance) ? null : filterDistance, locations,
                TextUtils.isEmpty(sortBy) ? null : sortBy,
                startMoney > 0 ? String.valueOf(startMoney) : null,
                endMoney > 0 ? String.valueOf(endMoney) : null,
                TextUtils.isEmpty(businessArea) ? null : businessArea,
                TextUtils.isEmpty(zone) ? null : zone,
                TextUtils.isEmpty(order) ? null : order)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<DrivingSchoolResponseList>() {
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
                    public void onNext(DrivingSchoolResponseList drivingSchoolResponseList) {
                        if (drivingSchoolResponseList.data != null) {
                            mView.refreshDrivingSchoolList(drivingSchoolResponseList.data);
                            nextLink = drivingSchoolResponseList.links.next;
                            mView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                            mView.showHelp(true);
                        } else {
                            mView.showHelp(false);
                        }

                    }
                });
    }

    public void addMoreDrivingSchools() {
        if (TextUtils.isEmpty(nextLink)) return;
        HHApiService apiService = application.getApiService();
        subscription = apiService.getDrivingSchools(nextLink)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<DrivingSchoolResponseList>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(DrivingSchoolResponseList drivingSchoolResponseList) {
                        if (drivingSchoolResponseList.data != null) {
                            mView.addMoreDrivingSchoolList(drivingSchoolResponseList.data);
                            nextLink = drivingSchoolResponseList.links.next;
                            mView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }
                    }
                });
    }

    public void setLocation(double lat, double lng) {
        application.setMyLocation(lat, lng);
    }

    public int[][] getPriceRanges() {
        CityConstants cityConstants = application.getCityConstants();
        return cityConstants.filters.prices;
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

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }

    public void clickFilterCount(int index) {
        //筛选点击
        HashMap<String, String> map = new HashMap();
        map.put("index", String.valueOf(index));
        addDataTrack("find_school_filter_tapped", mView.getContext(), map);
    }

    public void setZone(String zone) {
        filterDistance = "";
        businessArea = "";
        this.zone = zone;
    }

    public void resetFilter() {
        initDefaultFilters();
        fetchDrivingSchool();
    }
}
