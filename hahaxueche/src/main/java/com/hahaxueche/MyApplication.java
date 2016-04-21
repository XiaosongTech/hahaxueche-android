package com.hahaxueche;

import android.app.Application;
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
import com.hahaxueche.utils.JsonUtils;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.squareup.leakcanary.LeakCanary;

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

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        //Stetho.initializeWithDefaults(this);
        Branch.getAutoInstance(this);
        sLPresenter = new SLPresenterImpl(this);
        fcPresenter = new FCPresenterImpl(this);
        msPresenter = new MSPresenterImpl(this);
        apPresenter = new APPresenterImpl(this);
        final SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
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


}
