package com.hahaxueche.presenter.homepage;

import android.text.TextUtils;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.EventData;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.base.UserIdentityParam;
import com.hahaxueche.model.cluster.FieldItem;
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

    public void setDrivingSchoolId(int drivingSchoolIdd) {
        mSelectDrivingSchoolId = drivingSchoolIdd;
        getFields();
    }

    public void setZone(String zone) {
        mSelectDistance = Common.NO_LIMIT;
        mSelectZone = zone;
        getFields();
    }

    public void setDistance(int distance) {
        mSelectZone = "";
        mSelectDistance = distance;
        getFields();
    }

    public void selectField(Field field) {
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
                        mView.loadCoaches(coachResponseList.data);
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
        if (mSelectDrivingSchoolId < 0 && TextUtils.isEmpty(mSelectZone) && mSelectDistance == Common.NO_LIMIT) {
            mFilteredFields = fieldList;
            return;
        }
        List<Field> retList = new ArrayList<>();
        for (Field field : fieldList) {
            if (mSelectDrivingSchoolId >= 0) {
                //选择驾校
                boolean isExist = false;
                for (int fieldDrivingSchoolId : field.driving_school_ids) {
                    if (fieldDrivingSchoolId == mSelectDrivingSchoolId) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist) {
                    retList.add(field);
                    continue;
                }
            } else if (!TextUtils.isEmpty(mSelectZone)) {
                //选择区域
                if (field.zone.equals(mSelectZone)) {
                    retList.add(field);
                    continue;
                }
            } else if (mSelectDistance != Common.NO_LIMIT) {
                //选择距离
                if (application.getMyLocation() != null) {
                    //如果有定位信息，再计算
                    LatLng myLocation = new LatLng(application.getMyLocation().lat, application.getMyLocation().lng);
                    LatLng fieldLocation = new LatLng(field.lat, field.lng);
                    double distance = AMapUtils.calculateLineDistance(myLocation, fieldLocation);
                    if (distance < mSelectDistance * 1000) {
                        retList.add(field);
                        continue;
                    }
                } else {
                    //没有定位信息，直接当成无限距离
                    retList.add(field);
                    continue;
                }
            }
        }
        mFilteredFields = retList;
    }

    public LatLngBounds getFieldBounds() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Field field : mFilteredFields) {
            builder.include(new LatLng(field.lat, field.lng));
        }
        return builder.build();
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
}
