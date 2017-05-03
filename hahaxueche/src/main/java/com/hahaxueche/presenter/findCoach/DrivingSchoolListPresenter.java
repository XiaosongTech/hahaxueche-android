package com.hahaxueche.presenter.findCoach;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.CityConstants;
import com.hahaxueche.model.base.LocalSettings;
import com.hahaxueche.model.responseList.DrivingSchoolResponseList;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.DrivingSchoolListView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;

import java.util.ArrayList;

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
    private int sortBy = 0;
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
        //默认综合排序
        sortBy = 0;
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
                TextUtils.isEmpty(filterDistance) ? null : filterDistance, locations, null,
                startMoney > 0 ? String.valueOf(startMoney) : null,
                endMoney > 0 ? String.valueOf(endMoney) : null,
                TextUtils.isEmpty(zone) ? null : zone)
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

    public String[] getZones() {
        CityConstants cityConstants = application.getCityConstants();
        return cityConstants.zones;
    }

    public int[] getRadius() {
        CityConstants cityConstants = application.getCityConstants();
        return cityConstants.filters.radius;
    }

    public void setPriceRange(int startMoney, int endMoney) {
        this.startMoney = startMoney;
        this.endMoney = endMoney;
    }

    public void setDistance(int distance) {
        zone = "";
        if (distance == Common.NO_LIMIT) {
            filterDistance = "";
        } else {
            filterDistance = String.valueOf(distance);
        }
    }

    public void setZone(String zone) {
        filterDistance = "";
        this.zone = zone;
    }

}
