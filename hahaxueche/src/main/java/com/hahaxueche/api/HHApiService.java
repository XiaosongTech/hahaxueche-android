package com.hahaxueche.api;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.model.base.BaseBoolean;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.base.BaseSuccess;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.base.CityConstants;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.base.ShortenUrl;
import com.hahaxueche.model.community.Article;
import com.hahaxueche.model.course.ScheduleEvent;
import com.hahaxueche.model.examLib.Question;
import com.hahaxueche.model.payment.BankCard;
import com.hahaxueche.model.payment.PurchasedService;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.model.payment.WithdrawRecord;
import com.hahaxueche.model.responseList.ArticleResponseList;
import com.hahaxueche.model.responseList.CoachResponseList;
import com.hahaxueche.model.responseList.FieldResponseList;
import com.hahaxueche.model.responseList.PartnerResponseList;
import com.hahaxueche.model.responseList.ReferrerResponseList;
import com.hahaxueche.model.responseList.ReviewResponseList;
import com.hahaxueche.model.responseList.ScheduleEventResponseList;
import com.hahaxueche.model.user.IdCardUrl;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.UserIdentityInfo;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.coach.Follow;
import com.hahaxueche.model.user.coach.Partner;
import com.hahaxueche.model.user.coach.Review;
import com.hahaxueche.model.user.employee.Adviser;
import com.hahaxueche.model.user.identity.MarketingInfo;
import com.hahaxueche.model.user.student.BookAddress;
import com.hahaxueche.model.user.student.ExamResult;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.util.HHLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
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
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
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
    Observable<ArrayList<Coach>> getCoachesByKeyword(@Query("keyword") String keyword, @Query("city_id") int cityId);

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
                                                @Query("price_limit") String price, @Query("city_id") int cityId, @Query("sort_by") int sortBy, @Query("student_id") String studentId);

    @GET
    Observable<PartnerResponseList> getPartners(@Url String path);

    @FormUrlEncoded
    @POST("students/{studentId}/liked_training_partners/{partnerId}")
    Observable<Partner> likePartner(@Path("studentId") String studentId, @Path("partnerId") String partnerId, @FieldMap HashMap<String, Object> map, @Header("X-Access-Token") String accessToken);

    @GET("employees/advisers")
    Observable<Adviser> getAdviser(@Query("student_id") String studentId);

    @FormUrlEncoded
    @POST("users/reviews/{coachUserId}")
    Observable<Review> reviewCoach(@Path("coachUserId") String coachUserId, @FieldMap HashMap<String, Object> map, @Header("X-Access-Token") String accessToken);

    @FormUrlEncoded
    @PUT("students/purchased_service")
    Observable<PurchasedService> payStage(@FieldMap HashMap<String, Object> map, @Header("X-Access-Token") String accessToken);

    @GET("training_partners/{id}")
    Observable<Partner> getPartner(@Path("id") String partnerId);

    @FormUrlEncoded
    @POST("students/{id}/withdraw")
    Observable<BaseModel> withdrawBonus(@FieldMap HashMap<String, Object> map, @Path("id") String studentId, @Header("X-Access-Token") String accessToken);

    @GET("bank_cards/withdraw_records")
    Observable<ArrayList<WithdrawRecord>> getWithdrawRecords(@Header("X-Access-Token") String accessToken);

    @FormUrlEncoded
    @POST("bank_cards")
    Observable<BankCard> addBankCard(@FieldMap HashMap<String, Object> map, @Header("X-Access-Token") String accessToken);

    @GET("students/{id}/referees")
    Observable<ReferrerResponseList> getReferrers(@Path("id") String studentId, @Query("page") int page,
                                                  @Query("per_page") int perPage, @Header("X-Access-Token") String accessToken);

    @GET
    Observable<ReferrerResponseList> getReferrers(@Url String path, @Header("X-Access-Token") String accessToken);

    @GET("articles")
    Observable<ArticleResponseList> getArticles(@Query("page") int page, @Query("per_page") int perPage, @Query("is_popular") String isPopular,
                                                @Query("category") String category, @Query("student_id") String studentId);

    @GET
    Observable<ArticleResponseList> getArticles(@Url String path);

    @GET("articles/{id}")
    Observable<Article> getArticle(@Path("id") String articleId);


    @POST("students/{studentId}/liked_articles/{articleId}")
    Observable<Article> likeArticle(@Path("studentId") String studentId, @Path("articleId") String articleId,
                                    @Query("like") int like, @Header("X-Access-Token") String accessToken);

    @POST("articles/{articleId}/comments")
    Observable<Article> commentArticle(@Path("articleId") String articleId, @Query("student_id") String studentId,
                                       @Query("content") String content, @Header("X-Access-Token") String accessToken);

    @GET("articles/headline")
    Observable<Article> getHeadline(@Query("student_id") String studentId);

    @POST("students/{studentId}/{scheduleEventId}/schedule")
    Observable<ScheduleEvent> bookSchedule(@Path("studentId") String studentId, @Path("scheduleEventId") String scheduleEventId,
                                           @Header("X-Access-Token") String accessToken);

    @GET("students/{id}/course_schedules")
    Observable<ScheduleEventResponseList> getSchedules(@Path("id") String studentId, @Query("page") int page,
                                                       @Query("per_page") int perPage, @Query("booked") int booked,
                                                       @Header("X-Access-Token") String accessToken);

    @GET
    Observable<ScheduleEventResponseList> getSchedules(@Url String path, @Header("X-Access-Token") String accessToken);

    @POST("students/{studentId}/{scheduleEventId}/unschedule")
    Observable<BaseModel> cancelSchedule(@Path("studentId") String studentId, @Path("scheduleEventId") String scheduleEventId,
                                         @Header("X-Access-Token") String accessToken);

    @POST("students/{studentId}/{scheduleEventId}/review_schedule_event")
    Observable<Review> reviewSchedule(@Path("studentId") String studentId, @Path("scheduleEventId") String scheduleEventId,
                                      @Query("rating") float rating, @Header("X-Access-Token") String accessToken);

    @GET("students/{id}/vouchers")
    Observable<ArrayList<Voucher>> getAvailableVouchers(@Path("id") String studentId, @Query("coach_id") String coachId,
                                                        @Query("cumulative") String cumulative, @Header("X-Access-Token") String accessToken);

    @FormUrlEncoded
    @POST("vouchers")
    Observable<Voucher> addVoucher(@FieldMap HashMap<String, Object> map, @Header("X-Access-Token") String accessToken);

    @Multipart
    @POST("students/{id}/id_card")
    Observable<Response<IdCardUrl>> uploadIdCard(@Path("id") String studentId, @Header("X-Access-Token") String accessToken, @Part MultipartBody.Part file, @QueryMap HashMap<String, Object> map);

    @GET("students/{id}/agreement")
    Observable<Response<IdCardUrl>> createAgreement(@Path("id") String studentId, @Header("X-Access-Token") String accessToken);

    @POST("students/{id}/agreement")
    Observable<Student> signAgreement(@Path("id") String studentId, @Header("X-Access-Token") String accessToken);

    @FormUrlEncoded
    @POST("students/{id}/agreement_mail")
    Observable<BaseSuccess> sendAgreementEmail(@Path("id") String studentId, @FieldMap HashMap<String, Object> map, @Header("X-Access-Token") String accessToken);

    @FormUrlEncoded
    @POST("students/{id}/exam_results")
    Observable<ExamResult> submitExamResult(@Path("id") String studentId, @FieldMap HashMap<String, Object> map, @Header("X-Access-Token") String accessToken);

    @GET("students/{id}/exam_results")
    Observable<ArrayList<ExamResult>> getExamResults(@Path("id") String studentId, @Query("from") int fromScore, @Query("course") int course, @Header("X-Access-Token") String accessToken);

    @GET("exam_questions")
    Observable<ArrayList<Question>> getQuestions(@Query("course") int course);

    @GET
    Observable<ArrayList<ShortenUrl>> shortenUrl(@Url String url);

    @POST("address_books")
    Observable<String> uploadContacts(@Body BookAddress bookAddress);

    @GET("marketing_information")
    Observable<MarketingInfo> convertPromoCode(@Query("channel_id") String channelId, @Query("promo_code") String promoCode);

    @FormUrlEncoded
    @POST("students/{id}/id_card_info")
    Observable<BaseSuccess> uploadIdCard(@Path("id") String studentId, @FieldMap HashMap<String, Object> map, @Header("X-Access-Token") String accessToken);

    @FormUrlEncoded
    @POST("students/{id}/insurance_services")
    Observable<ResponseBody> createInsuranceCharge(@Path("id") String studentId, @FieldMap HashMap<String, Object> map,
                                                   @Header("X-Access-Token") String accessToken);

    @POST("students/{id}/insurance_services/hmb")
    Observable<Response<Student>> claimInsurance(@Path("id") String studentId, @Header("X-Access-Token") String accessToken);

    @FormUrlEncoded
    @POST("groupons")
    Observable<ResponseBody> getPrepayCharge(@FieldMap HashMap<String, Object> map, @Header("X-Access-Token") String accessToken);

    @GET("cities/{id}")
    Observable<CityConstants> getCityConstant(@Path("id") int cityId);

    @GET("fields")
    Observable<FieldResponseList> getFields(@Query("city_id") int cityId, @Query("driving_school_id") String drivingSchoolId);

    @FormUrlEncoded
    @POST("user_identities")
    Observable<UserIdentityInfo> getUserIdentity(@FieldMap HashMap<String, Object> map);

    class Factory {
        public static Retrofit getRetrofit() {
            HHLog.v("baseUrl -> " + baseUrl);
            OkHttpClient httpClient = new OkHttpClient();
            if (BuildConfig.DEBUG) {
                //添加网络请求日志拦截
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
            }
            return new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(httpClient)
                    .build();
        }

        public static HHApiService create() {
            return getRetrofit().create(HHApiService.class);
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
