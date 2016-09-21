package com.hahaxueche.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hahaxueche.BuildConfig;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.util.HHLog;


import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
    @PUT("students/{id}")
    Observable<Student> completeUserInfo(@Path("id") String studentId, @Header("X-Access-Token") String accessToken, @FieldMap HashMap<String, Object> map);

    @GET("students/{id}")
    Observable<Student> getStudent(@Path("id") String studentId, @Header("X-Access-Token") String accessToken);

    @DELETE("sessions/{id}")
    Observable<BaseModel> logOut(@Path("id") String sessionId, @Header("X-Access-Token") String accessToken);

    @FormUrlEncoded
    @POST("sessions/access_token/valid")
    Observable<BaseValid> isValidToken(@Header("X-Access-Token") String accessToken, @FieldMap HashMap<String, Object> map);

    class Factory {
        public static HHApiService create() {
            HHLog.v("baseUrl -> " + baseUrl);
            OkHttpClient httpClient = new OkHttpClient();
            if (BuildConfig.DEBUG) {
                //添加网络请求日志拦截
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
            }
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(httpClient)
                    .build();
            return retrofit.create(HHApiService.class);
        }
    }
}
