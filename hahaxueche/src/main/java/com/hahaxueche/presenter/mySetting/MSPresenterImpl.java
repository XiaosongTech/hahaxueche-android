package com.hahaxueche.presenter.mySetting;

import android.content.Context;
import android.os.AsyncTask;

import com.hahaxueche.api.mySetting.MSApi;
import com.hahaxueche.api.mySetting.MSApiImpl;
import com.hahaxueche.api.util.ApiError;
import com.hahaxueche.model.findCoach.CoachListResponse;
import com.hahaxueche.model.signupLogin.StudentModel;

/**
 * Created by gibxin on 2016/2/29.
 */
public class MSPresenterImpl implements MSPresenter {
    private Context context;
    private MSApi api;

    public MSPresenterImpl(Context context) {
        this.context = context;
        this.api = new MSApiImpl();
    }

    @Override
    public void getStudent(final String student_id, final String access_token, final MSCallbackListener<StudentModel> listener) {
        new AsyncTask<Void, Void, StudentModel>() {

            @Override
            protected StudentModel doInBackground(Void... params) {
                return api.getStudent(student_id, access_token);
            }

            @Override
            protected void onPostExecute(StudentModel studentModel) {
                if (listener != null) {
                    if (studentModel.isSuccess()) {
                        listener.onSuccess(studentModel);
                    } else {
                        listener.onFailure(studentModel.getCode(), studentModel.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void getFollowCoachList(final String page, final String per_page, final String access_token, final MSCallbackListener<CoachListResponse> listener) {
        new AsyncTask<Void, Void, CoachListResponse>() {

            @Override
            protected CoachListResponse doInBackground(Void... params) {
                return api.getFollowCoachList(page, per_page, access_token);
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
    public void getFollowCoachList(final String url, final String access_token, final MSCallbackListener<CoachListResponse> listener) {
        new AsyncTask<Void, Void, CoachListResponse>() {

            @Override
            protected CoachListResponse doInBackground(Void... params) {
                return api.getFollowCoachList(url, access_token);
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
}
