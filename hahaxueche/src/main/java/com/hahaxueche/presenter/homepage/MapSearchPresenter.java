package com.hahaxueche.presenter.homepage;

import android.text.TextUtils;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.CityConstants;
import com.hahaxueche.model.base.EventData;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.base.UserIdentityParam;
import com.hahaxueche.model.base.ZoneDetail;
import com.hahaxueche.model.cluster.FieldItem;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.responseList.CoachResponseList;
import com.hahaxueche.model.responseList.FieldResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.UserIdentityInfo;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.homepage.MapSearchView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.WebViewUrl;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 2017/5/16.
 */

public class MapSearchPresenter extends HHBasePresenter implements Presenter<MapSearchView> {
    private MapSearchView mView;
    private Subscription subscription;
    HHBaseApplication application;
    private int mSelectDrivingSchoolId = -1;
    private String mSelectZone = "";
    private int mSelectDistance = Common.NO_LIMIT;
    private List<Field> mFilteredFields;
    private String mSelectBusinessArea = "";

    @Override
    public void attachView(MapSearchView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
    }

    public void getFields() {
        FieldResponseList fieldResponseList = application.getFieldResponseList();
        if (fieldResponseList != null) {
            filterFields(fieldResponseList.data);
            mView.loadFields(convertFieldList());
        }
    }

    @Override
    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void setDrivingSchoolId(int drivingSchoolId) {
        mSelectDrivingSchoolId = drivingSchoolId;
        getFields();
    }

    public void setZone(String zone) {
        mSelectDistance = Common.NO_LIMIT;
        mSelectZone = zone;
        mSelectBusinessArea = "";
        getFields();
    }

    public void setDistance(int distance) {
        mSelectZone = "";
        mSelectBusinessArea = "";
        mSelectDistance = distance;
        getFields();
    }

    public void zoomToCity() {
        mSelectZone = "";
        mSelectBusinessArea = "";
        mSelectDistance = Common.NO_LIMIT;
        mSelectDrivingSchoolId = -1;
        getFields();
    }

    public void selectField(final Field field) {
        int cityId = 0;
        if (application.getSharedPrefUtil().getLocalSettings().cityId > -1) {
            cityId = application.getSharedPrefUtil().getLocalSettings().cityId;
        }
        ArrayList<String> fieldIds = new ArrayList<>();
        fieldIds.add(field.id);
        HHApiService apiService = application.getApiService();
        subscription = apiService.getFieldCoaches(Common.START_PAGE, 100, cityId, fieldIds, 5)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<CoachResponseList>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CoachResponseList coachResponseList) {
                        mView.showCoachesView();
                        mView.loadCoaches(coachResponseList.data, field.driving_school_ids);
                    }
                });
    }

    public void sendLocation(String cellPhone, Field field) {
        UserIdentityParam param = new UserIdentityParam();
        param.phone = cellPhone;
        param.promo_code = "921434";
        param.field_id = field.id;
        param.event_type = "1";
        EventData eventData = new EventData();
        eventData.link = WebViewUrl.WEB_URL_DITU + "?field_id=" + field.id;
        eventData.field_id = field.id;
        param.event_data = eventData;
        getUserIdentity(param);
    }

    public void checkField(String cellPhone, Coach coach) {
        UserIdentityParam param = new UserIdentityParam();
        param.phone = cellPhone;
        param.promo_code = "921434";
        param.coach_id = coach.id;
        param.field_id = coach.coach_group.field_id;
        param.driving_school_id = String.valueOf(coach.driving_school_id);
        getUserIdentity(param);
    }

    private void getUserIdentity(UserIdentityParam param) {
        HHApiService apiService = application.getApiService();
        if (application.getMyLocation() != null) {
            param.lng = application.getMyLocation().lng;
            param.lat = application.getMyLocation().lat;
        }
        subscription = apiService.getUserIdentity(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<UserIdentityInfo>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(UserIdentityInfo userIdentityInfo) {
                    }
                });
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }

    public void setLocation(double lat, double lng) {
        application.setMyLocation(lat, lng);
    }

    private void filterFields(List<Field> fieldList) {
        //没有筛选条件，直接返回全部
        if (mSelectDrivingSchoolId < 0 && TextUtils.isEmpty(mSelectZone)
                && TextUtils.isEmpty(mSelectBusinessArea) && mSelectDistance == Common.NO_LIMIT) {
            mFilteredFields = fieldList;
            return;
        }
        List<Field> retList = new ArrayList<>();
        for (Field field : fieldList) {
            //通过驾校筛选
            boolean isSchoolFiltered = true;
            //通过距离、区域筛选
            boolean isZoneFiltered = true;
            if (mSelectDrivingSchoolId >= 0) {
                //选择驾校
                boolean isExist = false;
                for (int fieldDrivingSchoolId : field.driving_school_ids) {
                    if (fieldDrivingSchoolId == mSelectDrivingSchoolId) {
                        isExist = true;
                        break;
                    }
                }
                isSchoolFiltered = isExist;
            }
            if (!TextUtils.isEmpty(mSelectBusinessArea)) {
                //选择商圈
                boolean isExist = false;
                for (int i = 0; i < field.business_areas.length; i++) {
                    if (mSelectBusinessArea.equals(field.business_areas[i])) {
                        isExist = true;
                        break;
                    }
                }
                isZoneFiltered = isExist;
            } else if (!TextUtils.isEmpty(mSelectZone)) {
                //选择区域
                isZoneFiltered = field.zone.equals(mSelectZone);
            } else if (mSelectDistance != Common.NO_LIMIT) {
                //选择距离
                if (application.getMyLocation() != null) {
                    //如果有定位信息，再计算
                    LatLng myLocation = new LatLng(application.getMyLocation().lat, application.getMyLocation().lng);
                    LatLng fieldLocation = new LatLng(field.lat, field.lng);
                    double distance = AMapUtils.calculateLineDistance(myLocation, fieldLocation);
                    isZoneFiltered = distance < mSelectDistance * 1000;
                }
            }
            if (isSchoolFiltered && isZoneFiltered) {
                retList.add(field);
            }
        }
        mFilteredFields = retList;
    }

    private List<FieldItem> convertFieldList() {
        List<FieldItem> fieldItems = new ArrayList<>();
        for (Field field : mFilteredFields) {
            LatLng latLng = new LatLng(field.lat, field.lng);
            FieldItem fieldItem = new FieldItem(latLng, field);
            fieldItems.add(fieldItem);
        }
        return fieldItems;
    }

    public String getSelectZone() {
        return mSelectZone;
    }

    public void getDrivingSchool(int drivingSchoolId, Marker marker) {
        List<DrivingSchool> drivingSchools = getDrivingSchools(mView.getContext());
        for (DrivingSchool drivingSchool : drivingSchools) {
            if (drivingSchool.id == drivingSchoolId) {
                mView.setInfoWindowDrivingSchool(drivingSchool, marker);
                break;
            }
        }
    }

    public void setBusinessArea(String businessArea) {
        mSelectBusinessArea = businessArea;
        //选择商圈的时候，区域选择条件也加上
        CityConstants cityConstants = application.getCityConstants();
        for (ZoneDetail zoneDetail : cityConstants.zone_details) {
            if (zoneDetail.business_areas.equals(businessArea)) {
                mSelectZone = zoneDetail.zone;
                break;
            }
        }
        mSelectDistance = Common.NO_LIMIT;
        getFields();
    }
}
