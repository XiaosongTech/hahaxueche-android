package com.hahaxueche.presenter.mySetting;

import android.content.Context;
import android.os.AsyncTask;

import com.hahaxueche.api.student.StudentApi;
import com.hahaxueche.api.student.StudentApiImpl;
import com.hahaxueche.api.util.ApiError;
import com.hahaxueche.model.response.CoachListResponse;
import com.hahaxueche.model.review.ReviewInfo;
import com.hahaxueche.model.student.StudentModel;
import com.hahaxueche.model.base.BaseApiResponse;

/**
 * Created by gibxin on 2016/2/29.
 */
public class MSPresenterImpl implements MSPresenter {
    private Context context;
    private StudentApi studentApi;

    public MSPresenterImpl(Context context) {
        this.context = context;
        this.studentApi = new StudentApiImpl();
    }

    @Override
    public void getStudent(final String student_id, final String access_token, final MSCallbackListener<StudentModel> listener) {
        new AsyncTask<Void, Void, StudentModel>() {

            @Override
            protected StudentModel doInBackground(Void... params) {
                return studentApi.getStudent(student_id, access_token);
            }

            @Override
            protected void onPostExecute(StudentModel studentModel) {
                if (studentModel != null) {
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
    public void getStudentForever(final String student_id, final String access_token, final MSCallbackListener<StudentModel> listener) {
        new AsyncTask<Void, Void, StudentModel>() {

            @Override
            protected StudentModel doInBackground(Void... params) {
                return studentApi.getStudentForever(student_id, access_token);
            }

            @Override
            protected void onPostExecute(StudentModel studentModel) {
                if (studentModel != null) {
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
                return studentApi.getFollowCoachList(page, per_page, access_token);
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
                return studentApi.getFollowCoachList(url, access_token);
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
    public void makeReview(final String coach_user_id, final String payment_stage, final String rating, final String comment, final String access_token,
                           final MSCallbackListener<ReviewInfo> listener) {
        new AsyncTask<Void, Void, ReviewInfo>() {

            @Override
            protected ReviewInfo doInBackground(Void... params) {
                try {
                    Thread.sleep(2000);//ping++有延迟
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return studentApi.makeReview(coach_user_id, payment_stage, rating, comment, access_token);
            }

            @Override
            protected void onPostExecute(ReviewInfo reviewInfo) {
                if (listener != null) {
                    if (reviewInfo.isSuccess()) {
                        listener.onSuccess(reviewInfo);
                    } else {
                        listener.onFailure(reviewInfo.getCode(), reviewInfo.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void loginOff(final String session_id, final String access_token, final MSCallbackListener<BaseApiResponse> listener) {
        new AsyncTask<Void, Void, BaseApiResponse>() {

            @Override
            protected BaseApiResponse doInBackground(Void... params) {
                return studentApi.loginOff(session_id, access_token);
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
}
