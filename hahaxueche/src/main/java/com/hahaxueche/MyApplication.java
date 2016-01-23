package com.hahaxueche;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.api.signupLogin.SLApi;
import com.hahaxueche.model.signupLogin.CitiesModel;
import com.hahaxueche.model.util.BaseApiResponse;
import com.hahaxueche.presenter.signupLogin.SLPresenter;
import com.hahaxueche.presenter.signupLogin.SLPresenterImpl;
import com.hahaxueche.utils.JsonUtils;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Application类�
 * Created by gibxin on 2016/1/19.
 */
public class MyApplication extends Application {
    private SLPresenter sLPresenter;

    @Override
    public void onCreate() {
        super.onCreate();
        sLPresenter = new SLPresenterImpl(this);
        new Thread(new Runnable() {

            @Override
            public void run() {
                HttpEngine httpEngine = HttpEngine.getInstance();
                Type type = new TypeToken<CitiesModel>() {
                }.getType();
                try {
                    CitiesModel citiesModel = httpEngine.getHandle(type, SLApi.CONSTANTS);
                    SharedPreferences sharedPreferences = getSharedPreferences("constants", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("constants", JsonUtils.serialize(citiesModel));
                    editor.commit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public SLPresenter getPresenter() {
        return sLPresenter;
    }
}
