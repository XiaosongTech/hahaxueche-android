package com.hahaxueche.api;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.util.HHLog;


import java.util.HashMap;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by wangshirui on 16/9/8.
 */
public interface HHApiService {
    String baseUrl = BuildConfig.SERVER_URL + "/api/v1/";

    @GET("constants")
    Observable<Constants> getConstants();

    @FormUrlEncoded
    @POST("send_auth_token")
    Observable<BaseModel> getAuthToken(@FieldMap HashMap<String, Object> map);

    @FormUrlEncoded
    @POST("sessions")
    Observable<User> login(@FieldMap HashMap<String, Object> map);

    @FormUrlEncoded
    @POST("users/reset_password")
    Observable<BaseModel> resetPassword(@FieldMap HashMap<String, Object> map);

    @FormUrlEncoded
    @POST("users")
    Observable<User> createSession(@FieldMap HashMap<String, Object> map);

    @FormUrlEncoded
    @POST("students/{id}")
    Observable<Student> completeUserInfo(@Path("id") String studentId, @Header("X-Access-Token") String accessToken, @FieldMap HashMap<String, Object> map);

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
