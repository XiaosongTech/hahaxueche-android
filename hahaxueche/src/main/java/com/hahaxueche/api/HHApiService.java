package com.hahaxueche.api;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.model.base.BaseBoolean;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.responseList.CoachResponseList;
import com.hahaxueche.model.responseList.PartnerResponseList;
import com.hahaxueche.model.responseList.ReviewResponseList;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.coach.Follow;
import com.hahaxueche.model.user.coach.Partner;
import com.hahaxueche.model.user.employee.Adviser;
import com.hahaxueche.util.HHLog;


import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

import static android.R.attr.path;

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

    @Multipart
    @POST("students/{id}/avatar")
    Observable<Student> uploadAvatar(@Path("id") String studentId, @Header("X-Access-Token") String accessToken, @Part MultipartBody.Part file);

    @GET("coaches")
    Observable<CoachResponseList> getCoaches(@Query("page") int page, @Query("per_page") int perPage, @Query("golden_coach_only") String goldenCoachOnly,
                                             @Query("license_type") String licenseType, @Query("price") String price, @Query("city_id") int cityId,
                                             @Query("training_field_ids[]") ArrayList<String> fieldIdList, @Query("distance") String distance,
                                             @Query("user_location[]") ArrayList<String> locations, @Query("sort_by") int sortBy, @Query("vip_only") int vipOnly,
                                             @Query("student_id") String studentId);

    @GET
    Observable<CoachResponseList> getCoaches(@Url String path);

    @GET("coaches")
    Observable<ArrayList<Coach>> getCoachesByKeyword(@Query("keyword") String keyword);

    @GET("coaches/{id}")
    Observable<Coach> getCoach(@Path("id") String coachId, @Query("student_id") String studentId);

    @GET("users/reviews/{id}")
    Observable<ReviewResponseList> getReviews(@Path("id") String coachUserId, @Query("page") int page, @Query("per_page") int perPage);

    @GET
    Observable<ReviewResponseList> getReviews(@Url String path);

    @GET("users/follows/{id}")
    Observable<BaseBoolean> isFollow(@Path("id") String coachUserId, @Header("X-Access-Token") String accessToken);

    @POST("users/follows/{id}")
    Observable<Follow> follow(@Path("id") String coachUserId, @Header("X-Access-Token") String accessToken);

    @DELETE("users/follows/{id}")
    Observable<BaseModel> cancelFollow(@Path("id") String coachUserId, @Header("X-Access-Token") String accessToken);

    @GET("users/follows")
    Observable<CoachResponseList> getFollowList(@Query("page") int page, @Query("per_page") int perPage, @Header("X-Access-Token") String accessToken);

    @GET
    Observable<CoachResponseList> getFollowList(@Url String path);

    @FormUrlEncoded
    @POST("students/{studentId}/like/{coachId}")
    Observable<Coach> like(@Path("studentId") String studentId, @Path("coachId") String coachId, @FieldMap HashMap<String, Object> map, @Header("X-Access-Token") String accessToken);

    @FormUrlEncoded
    @POST("charges")
    Observable<ResponseBody> createCharge(@FieldMap HashMap<String, Object> map, @Header("X-Access-Token") String accessToken);

    @GET("training_partners")
    Observable<PartnerResponseList> getPartners(@Query("page") int page, @Query("per_page") int perPage, @Query("license_type") String licenseType,
                                                @Query("price_from") String priceFrom, @Query("price_to") String priceTo,
                                                @Query("city_id") int cityId, @Query("sort_by") int sortBy, @Query("student_id") String studentId);

    @GET
    Observable<PartnerResponseList> getPartners(@Url String path);

    @FormUrlEncoded
    @POST("students/{studentId}/liked_training_partners/{partnerId}")
    Observable<Partner> likePartner(@Path("studentId") String studentId, @Path("partnerId") String partnerId, @FieldMap HashMap<String, Object> map, @Header("X-Access-Token") String accessToken);

    @GET("employees/advisers")
    Observable<Adviser> getAdviser(@Query("student_id") String studentId);


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

        public static HHApiService createWithNoConverter() {
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
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(httpClient)
                    .build();
            return retrofit.create(HHApiService.class);
        }
    }
}
