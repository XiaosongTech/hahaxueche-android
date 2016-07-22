package com.hahaxueche;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.presenter.appointment.APPresenter;
import com.hahaxueche.presenter.appointment.APPresenterImpl;
import com.hahaxueche.presenter.findCoach.FCPresenter;
import com.hahaxueche.presenter.findCoach.FCPresenterImpl;
import com.hahaxueche.presenter.mySetting.MSPresenter;
import com.hahaxueche.presenter.mySetting.MSPresenterImpl;
import com.hahaxueche.presenter.signupLogin.SLPresenter;
import com.hahaxueche.presenter.signupLogin.SLPresenterImpl;
import com.hahaxueche.service.CheckSessionService;
import com.hahaxueche.share.ShareConstants;
import com.hahaxueche.ui.widget.PicassoImageLoader;
import com.hahaxueche.utils.HahaCache;
import com.hahaxueche.utils.JsonUtils;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.instabug.library.IBGInvocationEvent;
import com.instabug.library.Instabug;
import com.qiyukf.unicorn.api.SavePowerConfig;
import com.qiyukf.unicorn.api.StatusBarNotificationConfig;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFOptions;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import java.io.IOException;
import java.lang.reflect.Type;

import io.branch.referral.Branch;
import okhttp3.Response;

/**
 * Application类�
 * Created by gibxin on 2016/1/19.
 */
public class MyApplication extends Application {
    private SLPresenter sLPresenter;
    private FCPresenter fcPresenter;
    private MSPresenter msPresenter;
    private APPresenter apPresenter;
    private IWXAPI wxApi;
    private Tencent mTencent;
    private IWeiboShareAPI mWeiboShareAPI;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        new Instabug.Builder(this, "e0ea921d16239f1f6b1b45a975de5ea1")
                .setInvocationEvent(IBGInvocationEvent.IBGInvocationEventShake)
                .build();
        //Stetho.initializeWithDefaults(this);
        Branch.getAutoInstance(this);
        sLPresenter = new SLPresenterImpl(this);
        fcPresenter = new FCPresenterImpl(this);
        msPresenter = new MSPresenterImpl(this);
        apPresenter = new APPresenterImpl(this);
        final SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        spUtil.clearRefererId();
        new Thread(new Runnable() {

            @Override
            public void run() {
                HttpEngine httpEngine = HttpEngine.getInstance();
                Type type = new TypeToken<Constants>() {
                }.getType();
                Constants constants;
                try {
                    Response response = httpEngine.getHandle("constants", "");
                    String body = response.body().string();
                    Log.v("gibxin", "body -> " + body);
                    if (response.isSuccessful()) {
                        constants = JsonUtils.deserialize(body, type);
                    } else {
                        Constants retModel = new Constants();
                        retModel.setCode(String.valueOf(response.code()));
                        retModel.setMessage(response.message());
                        retModel.setIsSuccess(false);
                        constants = retModel;
                    }
                    spUtil.setConstants(constants);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        regToShare();
        //七鱼客服
        HahaCache.context = getApplicationContext();
        Unicorn.init(this, "2f328da38ac77ce6d796c2977248f7e2", options(), new PicassoImageLoader());
        startCheckSessionService();
    }

    public SLPresenter getSLPresenter() {
        return sLPresenter;
    }

    public FCPresenter getFCPresenter() {
        return fcPresenter;
    }

    public MSPresenter getMsPresenter() {
        return msPresenter;
    }

    public APPresenter getApPresenter() {
        return apPresenter;
    }

    private void regToShare() {
        wxApi = WXAPIFactory.createWXAPI(this, ShareConstants.APP_ID, true);
        wxApi.registerApp(ShareConstants.APP_ID);
        mTencent = Tencent.createInstance(ShareConstants.APP_ID_QQ, this);
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, ShareConstants.WEIBO_APP_KEY);
        mWeiboShareAPI.registerApp();
    }

    public IWXAPI getIWXAPI() {
        return wxApi;
    }

    public Tencent getTencentAPI() {
        return mTencent;
    }

    public IWeiboShareAPI getWeiboAPI() {
        return mWeiboShareAPI;
    }

    @Override
    public void onTerminate() {
        stopCheckSessionService();
        super.onTerminate();
    }

    /**
     * 开启验证session服务
     */
    private void startCheckSessionService() {
        Intent intent = new Intent(this, CheckSessionService.class);
        startService(intent);
    }

    /**
     * 停止验证session服务
     */
    private void stopCheckSessionService() {
        Intent intent = new Intent(this, CheckSessionService.class);
        stopService(intent);
    }

    // 如果返回值为null，则全部使用默认参数。
    private YSFOptions options() {
        YSFOptions options = new YSFOptions();
        options.statusBarNotificationConfig = new StatusBarNotificationConfig();
        options.savePowerConfig = new SavePowerConfig();
        return options;
    }


}
