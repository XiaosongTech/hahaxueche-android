package com.hahaxueche;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

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
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.lang.reflect.Type;

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
        //清空session
        SharedPreferences spSession = getSharedPreferences("session", Activity.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = spSession.edit();
        spEditor.clear();
        spEditor.commit();
        new Thread(new Runnable() {

            @Override
            public void run() {
                HttpEngine httpEngine = HttpEngine.getInstance();
                Type type = new TypeToken<ConstantsModel>() {
                }.getType();
                try {
                    ConstantsModel constantsModel = httpEngine.getHandle(type, SLApi.CONSTANTS);
                    SharedPreferences sharedPreferences = getSharedPreferences("constants", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("constants", JsonUtils.serialize(constantsModel));
                    editor.commit();
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
