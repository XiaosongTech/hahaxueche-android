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
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.base.Location;
import com.hahaxueche.model.responseList.FieldResponseList;
import com.hahaxueche.ui.widget.FrescoImageLoader;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.HahaCache;
import com.hahaxueche.util.SharedPrefUtil;
import com.microquation.linkedme.android.LinkedME;
import com.qiyukf.unicorn.api.SavePowerConfig;
import com.qiyukf.unicorn.api.StatusBarNotificationConfig;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFOptions;
import com.taobao.hotfix.HotFixManager;
import com.taobao.hotfix.PatchLoadStatusListener;
import com.taobao.hotfix.util.PatchStatusCode;

import java.util.ArrayList;
import java.util.List;

import me.shaohui.shareutil.ShareConfig;
import me.shaohui.shareutil.ShareManager;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
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
    private Subscription subscription;
    public static String appVersion;
    public static String appId;
    private List<FieldResponseList> mFields;

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
        initApp();
        initHotfix();
        //aliyun push
        initCloudChannel(this);
        //fresco
        Fresco.initialize(this);
        spUtil = new SharedPrefUtil(this);
        HahaCache.context = getApplicationContext();
        //七鱼客服
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
        //shareUtil
        ShareConfig config = ShareConfig.instance()
                .qqId("1104872131")
                .wxId("wxdf5f23aa517b1a96")
                .weiboId("4186780524");
        ShareManager.init(config);
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            MultiDex.install(this);
        } catch (RuntimeException multiDexException) {
            // Work around Robolectric causing multi dex installation to fail, see
            // https://code.google.com/p/android/issues/detail?id=82007.
            boolean isUnderUnitTest;

            try {
                Class<?> robolectric = Class.forName("org.robolectric.Robolectric");
                isUnderUnitTest = (robolectric != null);
            } catch (ClassNotFoundException e) {
                isUnderUnitTest = false;
            }

            if (!isUnderUnitTest) {
                // Re-throw if this does not seem to be triggered by Robolectric.
                throw multiDexException;
            }
        }

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
        HahaCache.deviceId = pushService.getDeviceId();
        // 注册方法会自动判断是否支持小米系统推送，如不支持会跳过注册。
        MiPushRegister.register(applicationContext, BuildConfig.MI_PUSH_APP_ID, BuildConfig.MI_PUSH_APP_KEY);
        // 注册方法会自动判断是否支持华为系统推送，如不支持会跳过注册。
        HuaWeiRegister.register(applicationContext);
    }

    private void initApp() {
        this.appId = "36969-1"; //替换掉自己应用的appId
        try {
            this.appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (Exception e) {
            this.appVersion = "1.0.0";
        }
    }

    /**
     * 建议在Application.onCreate方法中执行initialize和queryNewHotPatch操作, 尽可能早的执行
     * 本demo只是为了测试的方便, 所以把这两个操作放在了Activity中
     */
    private void initHotfix() {
        //此处不再需要调用queryNewHotPatch方法, initialize方法内部会调用queryNewHotPatch方法.
        HotFixManager.getInstance().setContext(this)
                .setAppVersion(appVersion)
                .setAppId(appId)
                .setSupportHotpatch(true)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onload(final int mode, final int code, final String info, final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatusCode.CODE_SUCCESS_LOAD) {
                            HHLog.v("patch load success");
                        } else if (code == PatchStatusCode.CODE_ERROR_NEEDRESTART) {
                            // TODO: 10/24/16 表明新补丁生效需要重启. 业务方可自行实现逻辑, 提示用户或者强制重启, 建议: 用户可以监听进入后台事件, 然后应用自杀
                        } else if (code == PatchStatusCode.CODE_ERROR_INNERENGINEFAIL) {
                            // 内部引擎加载异常, 推荐此时清空本地补丁, 但是不清空本地版本号, 防止失败补丁重复加载
                            HotFixManager.getInstance().cleanPatches(false);
                        } else {
                            //其它错误信息, 查看PatchStatusCode类说明
                        }
                    }
                }).initialize();
    }

    public FieldResponseList getCachedFieldByCityId(int cityId) {
        if (mFields == null || mFields.size() < 1)
            return null;
        for (FieldResponseList field : mFields) {
            if (field.cityId == cityId) {
                return field;
            }
        }
        return null;
    }

    public void cacheField(FieldResponseList fieldResponseList, int cityId) {
        Constants constants = getConstants();
        constants.fields = fieldResponseList.data;
        setConstants(constants);
        fieldResponseList.cityId = cityId;
        if (mFields == null) {
            mFields = new ArrayList<>();
            mFields.add(fieldResponseList);

            return;
        }
        boolean isExists = false;
        for (FieldResponseList field : mFields) {
            if (field.cityId == cityId) {
                isExists = true;
                field = fieldResponseList;
                break;
            }
        }
        if (!isExists) {
            mFields.add(fieldResponseList);
        }
    }
}
