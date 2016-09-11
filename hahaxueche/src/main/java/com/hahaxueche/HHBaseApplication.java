package com.hahaxueche;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.SharedPrefUtil;

import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangshirui on 16/9/8.
 */
public class HHBaseApplication extends Application {
    private HHApiService apiService;
    private Scheduler defaultSubscribeScheduler;
    private Constants constants;
    private SharedPrefUtil spUtil;

    public static HHBaseApplication get(Context context) {
        return (HHBaseApplication) context.getApplicationContext();
    }

    public HHApiService getApiService() {
        if (apiService == null) {
            apiService = HHApiService.Factory.create();
        }
        return apiService;
    }

    public Scheduler defaultSubscribeScheduler() {
        if (defaultSubscribeScheduler == null) {
            defaultSubscribeScheduler = Schedulers.io();
        }
        return defaultSubscribeScheduler;
    }

    public Constants getConstants() {
        return constants;
    }

    public SharedPrefUtil getSharedPrefUtil() {
        return spUtil;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        HHApiService apiService = getApiService();
        spUtil = new SharedPrefUtil(this);
        apiService.getConstants()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(defaultSubscribeScheduler())
                .subscribe(new Subscriber<Constants>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Constants _constants) {
                        constants = _constants;
                    }
                });
    }

}
