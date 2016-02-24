package com.hahaxueche.presenter.findCoach;

import android.content.Context;
import android.os.AsyncTask;

import com.hahaxueche.api.findCoach.FCApi;
import com.hahaxueche.api.findCoach.FCApiImpl;
import com.hahaxueche.api.util.ApiError;
import com.hahaxueche.model.findCoach.CoachListResponse;
import com.hahaxueche.model.findCoach.CoachModel;

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
                             final String city_id, final String training_field_ids, final String distance, final String user_location, final String sort_by,
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
}
