package com.hahaxueche.presenter.findCoach;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hahaxueche.api.coach.CoachApi;
import com.hahaxueche.api.coach.CoachApiImpl;
import com.hahaxueche.api.util.ApiError;
import com.hahaxueche.model.response.CoachListResponse;
import com.hahaxueche.model.coach.Coach;
import com.hahaxueche.model.response.FollowResponse;
import com.hahaxueche.model.response.GetReviewsResponse;
import com.hahaxueche.model.response.StuPurchaseResponse;
import com.hahaxueche.model.response.TrailResponse;
import com.hahaxueche.model.student.PurchasedService;
import com.hahaxueche.model.base.BaseApiResponse;
import com.hahaxueche.model.base.BaseBoolean;
import com.hahaxueche.presenter.util.ErrorEvent;

import java.util.ArrayList;

/**
 * Created by gibxin on 2016/2/21.
 */
public class FCPresenterImpl implements FCPresenter {
    private Context context;
    private CoachApi coachApi;

    public FCPresenterImpl(Context context) {
        this.context = context;
        this.coachApi = new CoachApiImpl();
    }

    @Override
    public void getCoachList(final String page, final String per_page, final String golden_coach_only, final String license_type, final String price,
                             final String city_id, final ArrayList<String> training_field_ids, final String distance, final ArrayList<String> user_location, final String sort_by,
                             final FCCallbackListener<CoachListResponse> listener) {
        new AsyncTask<Void, Void, CoachListResponse>() {

            @Override
            protected CoachListResponse doInBackground(Void... params) {
                return coachApi.getCoachList(page, per_page, golden_coach_only, license_type, price, city_id, training_field_ids,
                        distance, user_location, sort_by);
            }

            @Override
            protected void onPostExecute(CoachListResponse coachListResponse) {
                if (listener != null && coachListResponse != null) {
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
                return coachApi.getCoachList(url);
            }

            @Override
            protected void onPostExecute(CoachListResponse coachListResponse) {
                if (coachListResponse != null) {
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
    public void getCoach(final String coach_id, final FCCallbackListener<Coach> listener) {
        new AsyncTask<Void, Void, Coach>() {

            @Override
            protected Coach doInBackground(Void... params) {
                return coachApi.getCoach(coach_id);
            }

            @Override
            protected void onPostExecute(Coach coachResponse) {
                if (coachResponse != null) {
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
                return coachApi.follow(followee_user_id, content, access_token);
            }

            @Override
            protected void onPostExecute(FollowResponse followResponse) {
                if (followResponse != null) {
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
                return coachApi.cancelFollow(followee_user_id, access_token);
            }

            @Override
            protected void onPostExecute(BaseApiResponse baseApiResponse) {
                if (baseApiResponse != null) {
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
                return coachApi.isFollow(followee_user_id, access_token);
            }

            @Override
            protected void onPostExecute(BaseBoolean baseBoolean) {
                if (baseBoolean != null) {
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
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        boolean isValidPhoneNumber;
        try {
            Phonenumber.PhoneNumber chNumberProto = phoneUtil.parse(phone_number, "CN");
            isValidPhoneNumber = phoneUtil.isValidNumber(chNumberProto);
        } catch (NumberParseException e) {
            listener.onFailure(ErrorEvent.PARAM_ILLEGAL, "您的手机号码格式有误");
            return;
        }
        if (!isValidPhoneNumber) {
            listener.onFailure(ErrorEvent.PARAM_ILLEGAL, "您的手机号码格式有误");
            return;
        }
        new AsyncTask<Void, Void, TrailResponse>() {
            @Override
            protected TrailResponse doInBackground(Void... params) {
                return coachApi.createTrail(coach_id, name, phone_number, first_time_option, second_time_option);
            }

            @Override
            protected void onPostExecute(TrailResponse trailResponse) {
                if (trailResponse != null) {
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
                return coachApi.getReviewList(coach_user_id, page, per_page);
            }

            @Override
            protected void onPostExecute(GetReviewsResponse getReviewsResponse) {
                if (getReviewsResponse != null) {
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
                return coachApi.getReviewList(url);
            }

            @Override
            protected void onPostExecute(GetReviewsResponse getReviewsResponse) {
                if (getReviewsResponse != null) {
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
                return coachApi.createPurchaseStu(coach_id, access_token, current_payment_stage, service_stage, total_amount);
            }

            @Override
            protected void onPostExecute(StuPurchaseResponse stuPurchaseResponse) {
                if (stuPurchaseResponse != null) {
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
                return coachApi.createCharge(coach_id, access_token);
            }

            @Override
            protected void onPostExecute(String charge) {
                if (charge != null) {
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
                return coachApi.purchasedService(payment_stage, access_token);
            }

            @Override
            protected void onPostExecute(PurchasedService purchasedService) {
                if (purchasedService != null) {
                    if (purchasedService.isSuccess()) {
                        listener.onSuccess(purchasedService);
                    } else {
                        if(purchasedService.getCode().equals("400003")){
                            listener.onFailure(purchasedService.getCode(), "亲，完成课程后才付款哦~");
                        }else {
                            listener.onFailure(purchasedService.getCode(), "打款失败");
                        }
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void oneKeyFindCoach(final String lat, final String lng, final FCCallbackListener<Coach> listener) {
        new AsyncTask<Void, Void, Coach>() {

            @Override
            protected Coach doInBackground(Void... params) {
                return coachApi.oneKeyFindCoach(lat, lng);
            }

            @Override
            protected void onPostExecute(Coach coach) {
                if (coach != null) {
                    if (coach != null) {
                        if (coach.isSuccess()) {
                            listener.onSuccess(coach);
                        } else {
                            listener.onFailure(coach.getCode(), coach.getMessage());
                        }
                    } else {
                        listener.onFailure("0000", "未找到合适的教练");
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }
}
