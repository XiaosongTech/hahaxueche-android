package com.hahaxueche.presenter.findCoach;

import android.text.Html;
import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.base.LocalSettings;
import com.hahaxueche.model.responseList.CoachResponseList;
import com.hahaxueche.model.responseList.FieldResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.UserIdentityInfo;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.FieldFilterView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        HHApiService apiService = application.getApiService();
        int cityId = 0;
        if (application.getSharedPrefUtil().getLocalSettings().cityId > -1) {
            cityId = application.getSharedPrefUtil().getLocalSettings().cityId;
        }
        final FieldResponseList cacheFields = application.getCachedFieldByCityId(cityId);
        if (cacheFields != null) {
            mView.initMap(cacheFields.data);
        } else {
            final int finalCityId = cityId;
            subscription = apiService.getFields(cityId, null)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<FieldResponseList>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            HHLog.e(e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(FieldResponseList fieldResponseList) {
                            application.cacheField(fieldResponseList, finalCityId);
                            mView.initMap(fieldResponseList.data);
                        }
                    });
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
        subscription = apiService.getCoaches(Common.START_PAGE, 100, null, null, null, cityId, fieldIds, null, null, 5, 0, null)
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

    public void getUserIdentity(String cellPhone) {
        HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", cellPhone);
        map.put("promo_code", "921434");
        subscription = apiService.getUserIdentity(map)
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
}
