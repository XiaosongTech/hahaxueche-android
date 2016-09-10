package com.hahaxueche;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.hahaxueche.ui.api.HHApiService;
import com.hahaxueche.ui.model.base.Constants;
import com.hahaxueche.util.HHLog;

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

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        HHApiService apiService = getApiService();
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
