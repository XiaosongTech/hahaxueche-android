package com.hahaxueche.presenter.findCoach;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.hahaxueche.api.findCoach.FCApi;
import com.hahaxueche.api.findCoach.FCApiImpl;
import com.hahaxueche.api.util.ApiError;
import com.hahaxueche.model.findCoach.CoachListResponse;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.model.findCoach.FollowResponse;
import com.hahaxueche.model.findCoach.GetReviewsResponse;
import com.hahaxueche.model.findCoach.StuPurchaseResponse;
import com.hahaxueche.model.findCoach.TrailResponse;
import com.hahaxueche.model.mySetting.PurchasedService;
import com.hahaxueche.model.util.BaseApiResponse;
import com.hahaxueche.model.util.BaseBoolean;

import java.util.ArrayList;

/**
 * Created by gibxin on 2016/2/21.
 */
public class FCPresenterImpl implements FCPresenter {
    private Context context;
    private FCApi api;

    public FCPresenterImpl(Context context) {
        this.context = context;
        this.api = new FCApiImpl();
    }

    @Override
    public void getCoachList(final String page, final String per_page, final String golden_coach_only, final String license_type, final String price,
                             final String city_id, final ArrayList<String> training_field_ids, final String distance, final String user_location, final String sort_by,
                             final FCCallbackListener<CoachListResponse> listener) {
        new AsyncTask<Void, Void, CoachListResponse>() {

            @Override
            protected CoachListResponse doInBackground(Void... params) {
                return api.getCoachList(page, per_page, golden_coach_only, license_type, price, city_id, training_field_ids,
                        distance, user_location, sort_by);
            }

            @Override
            protected void onPostExecute(CoachListResponse coachListResponse) {
                if (listener != null) {
                    if (coachListResponse.isSuccess()) {
                        listener.onSuccess(coachListResponse);
                    } else {
                        listener.onFailure(coachListResponse.getCode(), coachListResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void getCoachList(final String url, final FCCallbackListener<CoachListResponse> listener) {
        new AsyncTask<Void, Void, CoachListResponse>() {

            @Override
            protected CoachListResponse doInBackground(Void... params) {
                return api.getCoachList(url);
            }

            @Override
            protected void onPostExecute(CoachListResponse coachListResponse) {
                if (listener != null) {
                    if (coachListResponse.isSuccess()) {
                        listener.onSuccess(coachListResponse);
                    } else {
                        listener.onFailure(coachListResponse.getCode(), coachListResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void getCoach(final String coach_id, final FCCallbackListener<CoachModel> listener) {
        new AsyncTask<Void, Void, CoachModel>() {

            @Override
            protected CoachModel doInBackground(Void... params) {
                return api.getCoach(coach_id);
            }

            @Override
            protected void onPostExecute(CoachModel coachResponse) {
                if (listener != null) {
                    if (coachResponse.isSuccess()) {
                        listener.onSuccess(coachResponse);
                    } else {
                        listener.onFailure(coachResponse.getCode(), coachResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void follow(final String followee_user_id, final String content, final String access_token, final FCCallbackListener<FollowResponse> listener) {
        new AsyncTask<Void, Void, FollowResponse>() {

            @Override
            protected FollowResponse doInBackground(Void... params) {
                return api.follow(followee_user_id, content, access_token);
            }

            @Override
            protected void onPostExecute(FollowResponse followResponse) {
                if (listener != null) {
                    if (followResponse.isSuccess()) {
                        listener.onSuccess(followResponse);
                    } else {
                        listener.onFailure(followResponse.getCode(), followResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void cancelFollow(final String followee_user_id, final String access_token, final FCCallbackListener<BaseApiResponse> listener) {
        new AsyncTask<Void, Void, BaseApiResponse>() {
            @Override
            protected BaseApiResponse doInBackground(Void... params) {
                return api.cancelFollow(followee_user_id, access_token);
            }

            @Override
            protected void onPostExecute(BaseApiResponse baseApiResponse) {
                if (listener != null) {
                    if (baseApiResponse.isSuccess()) {
                        listener.onSuccess(baseApiResponse);
                    } else {
                        listener.onFailure(baseApiResponse.getCode(), baseApiResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void isFollow(final String followee_user_id, final String access_token, final FCCallbackListener<BaseBoolean> listener) {
        new AsyncTask<Void, Void, BaseBoolean>() {
            @Override
            protected BaseBoolean doInBackground(Void... params) {
                return api.isFollow(followee_user_id, access_token);
            }

            @Override
            protected void onPostExecute(BaseBoolean baseBoolean) {
                if (listener != null) {
                    if (baseBoolean.isSuccess()) {
                        listener.onSuccess(baseBoolean);
                    } else {
                        listener.onFailure(baseBoolean.getCode(), baseBoolean.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void createTrail(final String coach_id, final String name, final String phone_number, final String first_time_option,
                            final String second_time_option, final FCCallbackListener<TrailResponse> listener) {
        new AsyncTask<Void, Void, TrailResponse>() {
            @Override
            protected TrailResponse doInBackground(Void... params) {
                return api.createTrail(coach_id, name, phone_number, first_time_option, second_time_option);
            }

            @Override
            protected void onPostExecute(TrailResponse trailResponse) {
                if (listener != null) {
                    if (trailResponse.isSuccess()) {
                        listener.onSuccess(trailResponse);
                    } else {
                        if (trailResponse.getCode().equals("400003")) {
                            trailResponse.setMessage("预约日期需要大于当前日期");
                            listener.onFailure(trailResponse.getCode(), trailResponse.getMessage());
                        } else {
                            listener.onFailure(trailResponse.getCode(), trailResponse.getMessage());
                        }
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void getReviewList(final String coach_user_id, final String page, final String per_page,
                              final FCCallbackListener<GetReviewsResponse> listener) {
        new AsyncTask<Void, Void, GetReviewsResponse>() {
            @Override
            protected GetReviewsResponse doInBackground(Void... params) {
                return api.getReviewList(coach_user_id, page, per_page);
            }

            @Override
            protected void onPostExecute(GetReviewsResponse getReviewsResponse) {
                if (listener != null) {
                    if (getReviewsResponse.isSuccess()) {
                        listener.onSuccess(getReviewsResponse);
                    } else {
                        listener.onFailure(getReviewsResponse.getCode(), getReviewsResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void getReviewList(final String url, final FCCallbackListener<GetReviewsResponse> listener) {
        new AsyncTask<Void, Void, GetReviewsResponse>() {

            @Override
            protected GetReviewsResponse doInBackground(Void... params) {
                return api.getReviewList(url);
            }

            @Override
            protected void onPostExecute(GetReviewsResponse getReviewsResponse) {
                if (listener != null) {
                    if (getReviewsResponse.isSuccess()) {
                        listener.onSuccess(getReviewsResponse);
                    } else {
                        listener.onFailure(getReviewsResponse.getCode(), getReviewsResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void createPurchaseStu(final String coach_id, final String access_token, final String current_payment_stage, final String service_stage,
                                  final String total_amount, final FCCallbackListener<StuPurchaseResponse> listener) {
        new AsyncTask<Void, Void, StuPurchaseResponse>() {

            @Override
            protected StuPurchaseResponse doInBackground(Void... params) {
                return api.createPurchaseStu(coach_id, access_token, current_payment_stage, service_stage, total_amount);
            }

            @Override
            protected void onPostExecute(StuPurchaseResponse stuPurchaseResponse) {
                if (listener != null) {
                    if (stuPurchaseResponse.isSuccess()) {
                        listener.onSuccess(stuPurchaseResponse);
                    } else {
                        listener.onFailure(stuPurchaseResponse.getCode(), stuPurchaseResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void createCharge(final String coach_id, final String access_token, final FCCallbackListener<String> listener) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return api.createCharge(coach_id, access_token);
            }

            @Override
            protected void onPostExecute(String charge) {
                if (listener != null) {
                    if (!TextUtils.isEmpty(charge)) {
                        listener.onSuccess(charge);
                    } else {
                        listener.onFailure("400", "获取charge失败");
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void purchasedService(final String payment_stage, final String access_token, final FCCallbackListener<PurchasedService> listener) {
        new AsyncTask<Void, Void, PurchasedService>() {

            @Override
            protected PurchasedService doInBackground(Void... params) {
                return api.purchasedService(payment_stage, access_token);
            }

            @Override
            protected void onPostExecute(PurchasedService purchasedService) {
                if (listener != null) {
                    if (purchasedService.isSuccess()) {
                        listener.onSuccess(purchasedService);
                    } else {
                        listener.onFailure(purchasedService.getCode(), purchasedService.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }
}
