package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.EventData;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.base.UserIdentityParam;
import com.hahaxueche.model.responseList.CoachResponseList;
import com.hahaxueche.model.responseList.FieldResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.UserIdentityInfo;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.FieldFilterView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.WebViewUrl;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 2016/10/17.
 */

public class FieldFilterPresenter extends HHBasePresenter implements Presenter<FieldFilterView> {
    private FieldFilterView mView;
    private Subscription subscription;
    HHBaseApplication application;

    @Override
    public void attachView(FieldFilterView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());

    }

    public void getFields() {
        FieldResponseList fieldResponseList = application.getFieldResponseList();
        if (fieldResponseList != null) {
            mView.initMap(fieldResponseList.data);
        }
    }

    @Override
    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
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
        param.driving_school_id = coach.driving_school_id;
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
}
