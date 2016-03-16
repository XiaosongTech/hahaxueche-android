package com.hahaxueche;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.api.signupLogin.SLApi;
import com.hahaxueche.model.util.ConstantsModel;
import com.hahaxueche.presenter.findCoach.FCPresenter;
import com.hahaxueche.presenter.findCoach.FCPresenterImpl;
import com.hahaxueche.presenter.mySetting.MSPresenter;
import com.hahaxueche.presenter.mySetting.MSPresenterImpl;
import com.hahaxueche.presenter.signupLogin.SLPresenter;
import com.hahaxueche.presenter.signupLogin.SLPresenterImpl;
import com.hahaxueche.utils.JsonUtils;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Locale;

/**
 * Application类�
 * Created by gibxin on 2016/1/19.
 */
public class MyApplication extends Application {
    private SLPresenter sLPresenter;
    private FCPresenter fcPresenter;
    private MSPresenter msPresenter;

    @Override
    public void onCreate() {
        super.onCreate();
        sLPresenter = new SLPresenterImpl(this);
        fcPresenter = new FCPresenterImpl(this);
        msPresenter = new MSPresenterImpl(this);
        final SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        new Thread(new Runnable() {

            @Override
            public void run() {
                HttpEngine httpEngine = HttpEngine.getInstance();
                Type type = new TypeToken<ConstantsModel>() {
                }.getType();
                try {
                    ConstantsModel constants = httpEngine.getHandle(type, SLApi.CONSTANTS);
                    spUtil.setConstants(constants);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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


}
