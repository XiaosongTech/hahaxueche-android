package com.hahaxueche;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.base.Location;
import com.hahaxueche.model.examLib.Question;
import com.hahaxueche.ui.widget.FrescoImageLoader;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.HahaCache;
import com.hahaxueche.util.SharedPrefUtil;
import com.hahaxueche.util.share.ShareConstants;
import com.microquation.linkedme.android.LinkedME;
import com.qiyukf.unicorn.api.SavePowerConfig;
import com.qiyukf.unicorn.api.StatusBarNotificationConfig;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFOptions;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangshirui on 16/9/8.
 */
public class HHBaseApplication extends Application {
    private HHApiService apiService;
    private HHApiService apiServiceNoConverter;
    private Scheduler defaultSubscribeScheduler;
    private Constants constants;
    private SharedPrefUtil spUtil;
    private Observable sessionObservable;
    private Location myLocation;
    private IWXAPI wxApi;
    private Tencent mTencent;
    private IWeiboShareAPI mWeiboShareAPI;
    private ArrayList<Question> questions1;
    private ArrayList<Question> questions4;
    private Subscription subscription;

    public static HHBaseApplication get(Context context) {
        return (HHBaseApplication) context.getApplicationContext();
    }

    public HHApiService getApiService() {
        if (apiService == null) {
            apiService = HHApiService.Factory.create();
        }
        return apiService;
    }

    public HHApiService getApiServiceNoConverter() {
        if (apiServiceNoConverter == null) {
            apiServiceNoConverter = HHApiService.Factory.createWithNoConverter();
        }
        return apiServiceNoConverter;
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
        if (constants == null) {
            SharedPrefUtil spUtil = new SharedPrefUtil(this);
            constants = spUtil.getConstants();
        }
        return constants;
    }

    public ArrayList<Question> getQuestions1() {
        if (questions1 == null) {
            SharedPrefUtil spUtil = new SharedPrefUtil(this);
            questions1 = spUtil.getQuestions1();
        }
        return questions1;
    }

    public ArrayList<Question> getQuestions4() {
        if (questions4 == null) {
            SharedPrefUtil spUtil = new SharedPrefUtil(this);
            questions4 = spUtil.getQuestions4();
        }
        return questions4;
    }

    public void setConstants(Constants constants) {
        this.constants = constants;
        SharedPrefUtil spUtil = new SharedPrefUtil(this);
        spUtil.setConstants(constants);
    }

    public SharedPrefUtil getSharedPrefUtil() {
        if (spUtil == null) {
            spUtil = new SharedPrefUtil(this);
        }
        return spUtil;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initCloudChannel(this);//aliyun push
        Fresco.initialize(this);
        spUtil = new SharedPrefUtil(this);
        HahaCache.context = getApplicationContext();
        Unicorn.init(this, "2f328da38ac77ce6d796c2977248f7e2", options(), new FrescoImageLoader());
        try {
            if (BuildConfig.DEBUG) {
                //设置debug模式下打印LinkedME日志
                LinkedME.getInstance(this).setDebug();
            } else {
                LinkedME.getInstance(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        subscription = getApiService().getQuestions(0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(defaultSubscribeScheduler())
                .subscribe(new Subscriber<ArrayList<Question>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<Question> questions) {
                        questions1 = questions;
                        SharedPrefUtil spUtil = new SharedPrefUtil(getApplicationContext());
                        spUtil.setQuestions1(questions1);
                    }
                });
        subscription = getApiService().getQuestions(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(defaultSubscribeScheduler())
                .subscribe(new Subscriber<ArrayList<Question>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<Question> questions) {
                        questions4 = questions;
                        SharedPrefUtil spUtil = new SharedPrefUtil(getApplicationContext());
                        spUtil.setQuestions4(questions4);
                    }
                });
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

    public IWXAPI getIWXAPI() {
        if (wxApi == null) {
            wxApi = WXAPIFactory.createWXAPI(this, ShareConstants.APP_ID, true);
            wxApi.registerApp(ShareConstants.APP_ID);
        }
        return wxApi;
    }

    public Tencent getTencentAPI() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(ShareConstants.APP_ID_QQ, this);
        }
        return mTencent;
    }

    public IWeiboShareAPI getWeiboAPI() {
        if (mWeiboShareAPI == null) {
            mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, ShareConstants.WEIBO_APP_KEY);
            mWeiboShareAPI.registerApp();
        }
        return mWeiboShareAPI;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

    /**
     * 初始化云推送通道
     *
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                HHLog.v("init cloudchannel success");
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                HHLog.v("init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
        HHLog.v("pushService.getDeviceId -> " + pushService.getDeviceId());
        // 注册方法会自动判断是否支持小米系统推送，如不支持会跳过注册。
        MiPushRegister.register(applicationContext, BuildConfig.MI_PUSH_APP_ID, BuildConfig.MI_PUSH_APP_KEY);
        // 注册方法会自动判断是否支持华为系统推送，如不支持会跳过注册。
        HuaWeiRegister.register(applicationContext);
    }
}
