package com.hahaxueche.ui.api;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.ui.model.Constants;
import com.hahaxueche.util.HHLog;


import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by wangshirui on 16/9/8.
 */
public interface HHApiService {
    String baseUrl = BuildConfig.SERVER_URL + "/api/v1/";

    @GET("constants")
    Observable<Constants> getConstants();

    class Factory {
        public static HHApiService create() {
            HHLog.v("baseUrl -> " + baseUrl);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(HHApiService.class);
        }
    }
}
