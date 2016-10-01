package com.hahaxueche;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.base.Location;
import com.hahaxueche.ui.widget.FrescoImageLoader;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.HahaCache;
import com.hahaxueche.util.SharedPrefUtil;
import com.qiyukf.unicorn.api.SavePowerConfig;
import com.qiyukf.unicorn.api.StatusBarNotificationConfig;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFOptions;

import rx.Observable;
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
    private Observable sessionObservable;
    private Location myLocation;

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

    public Observable getSessionObservable() {
        if (sessionObservable == null) {
            sessionObservable = Observable.create(new Observable.OnSubscribe<Object>() {
                @Override
                public void call(Subscriber<? super Object> subscriber) {
                    subscriber.onError(new Throwable(ErrorUtil.INVALID_SESSION));
                }
            });
        }
        return sessionObservable;
    }

    public Constants getConstants() {
        return constants;
    }

    public void setConstants(Constants constants) {
        this.constants = constants;
    }

    public SharedPrefUtil getSharedPrefUtil() {
        return spUtil;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        spUtil = new SharedPrefUtil(this);
        HahaCache.context = getApplicationContext();
        Unicorn.init(this, "2f328da38ac77ce6d796c2977248f7e2", options(), new FrescoImageLoader());
    }

    // 如果返回值为null，则全部使用默认参数。
    private YSFOptions options() {
        YSFOptions options = new YSFOptions();
        options.statusBarNotificationConfig = new StatusBarNotificationConfig();
        options.savePowerConfig = new SavePowerConfig();
        return options;
    }

    public Location getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(double lat, double lng) {
        if (myLocation == null) {
            myLocation = new Location();
        }
        myLocation.lat = lat;
        myLocation.lng = lng;
    }

}
