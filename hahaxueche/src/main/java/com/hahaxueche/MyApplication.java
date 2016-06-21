package com.hahaxueche;

import android.app.Application;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.growingio.android.sdk.collection.GrowingIO;
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
import com.hahaxueche.share.ShareConstants;
import com.hahaxueche.utils.JsonUtils;
import com.hahaxueche.utils.SharedPreferencesUtil;
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
        //Stetho.initializeWithDefaults(this);
        Branch.getAutoInstance(this);
        GrowingIO.startTracing(this, "becfd5bc56ea7bd1");
        GrowingIO.setScheme("growing.0d688069b7d1994b");
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        regToShare();
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


}
