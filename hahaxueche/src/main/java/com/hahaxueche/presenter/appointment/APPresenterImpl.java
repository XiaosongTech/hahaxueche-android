package com.hahaxueche.presenter.appointment;

import android.content.Context;
import android.os.AsyncTask;

import com.hahaxueche.api.student.StudentApi;
import com.hahaxueche.api.student.StudentApiImpl;
import com.hahaxueche.api.util.ApiError;
import com.hahaxueche.model.base.BaseApiResponse;
import com.hahaxueche.model.coach.ScheduleEvent;
import com.hahaxueche.model.response.ScheduleEventListResponse;
import com.hahaxueche.model.review.ReviewInfo;
import com.hahaxueche.presenter.BaseCallbackListener;

/**
 * Created by gibxin on 2016/4/18.
 */
public class APPresenterImpl implements APPresenter {

    private StudentApi studentApi;

    public APPresenterImpl(Context context) {
        this.studentApi = new StudentApiImpl();
    }

    @Override
    public void bookCourseSchedule(final String studentId, final String courseScheduleEventId, final String accessToken, final BaseCallbackListener<ScheduleEvent> listener) {
        new AsyncTask<Void, Void, ScheduleEvent>() {

            @Override
            protected ScheduleEvent doInBackground(Void... params) {
                return studentApi.bookScheduleEvent(studentId, courseScheduleEventId, accessToken);
            }

            @Override
            protected void onPostExecute(ScheduleEvent scheduleEvent) {
                if (listener != null && scheduleEvent != null) {
                    if (scheduleEvent.isSuccess()) {
                        listener.onSuccess(scheduleEvent);
                    } else {
                        //40006 有未完成的课程
                        //40005 有待评级的课程
                        listener.onFailure(scheduleEvent.getCode(), scheduleEvent.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void fetchCourseSchedule(final String studentId, final String page, final String perPage, final String booked, final String accessToken, final BaseCallbackListener<ScheduleEventListResponse> listener) {
        new AsyncTask<Void, Void, ScheduleEventListResponse>() {

            @Override
            protected ScheduleEventListResponse doInBackground(Void... params) {
                return studentApi.fetchCourseSchedule(studentId, page, perPage, booked, accessToken);
            }

            @Override
            protected void onPostExecute(ScheduleEventListResponse scheduleEventListResponse) {
                if (listener != null && scheduleEventListResponse != null) {
                    if (scheduleEventListResponse.isSuccess()) {
                        listener.onSuccess(scheduleEventListResponse);
                    } else {
                        listener.onFailure(scheduleEventListResponse.getCode(), scheduleEventListResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void fetchCourseSchedule(final String url, final String accessToken, final BaseCallbackListener<ScheduleEventListResponse> listener) {
        new AsyncTask<Void, Void, ScheduleEventListResponse>() {

            @Override
            protected ScheduleEventListResponse doInBackground(Void... params) {
                return studentApi.fetchCourseSchedule(url, accessToken);
            }

            @Override
            protected void onPostExecute(ScheduleEventListResponse scheduleEventListResponse) {
                if (listener != null && scheduleEventListResponse != null) {
                    if (scheduleEventListResponse.isSuccess()) {
                        listener.onSuccess(scheduleEventListResponse);
                    } else {
                        listener.onFailure(scheduleEventListResponse.getCode(), scheduleEventListResponse.getMessage());
                    }
                } else {
                    listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                }
            }
        }.execute();
    }

    @Override
    public void cancelCourseSchedule(final String studentId, final String scheduleEventId, final String accessToken, final BaseCallbackListener<BaseApiResponse> listener) {
        new AsyncTask<Void, Void, BaseApiResponse>() {

            @Override
            protected BaseApiResponse doInBackground(Void... params) {
                return studentApi.cancelCourseSchedule(studentId, scheduleEventId, accessToken);
            }

            @Override
            protected void onPostExecute(BaseApiResponse baseApiResponse) {
                if (listener != null && baseApiResponse != null) {
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
    public void reviewSchedule(final String studentId, final String scheduleEventId, final String rating, final String accessToken, final BaseCallbackListener<ReviewInfo> listener) {
        new AsyncTask<Void, Void, ReviewInfo>() {

            @Override
            protected ReviewInfo doInBackground(Void... params) {
                return studentApi.reviewSchedule(studentId, scheduleEventId, rating, accessToken);
            }

            @Override
            protected void onPostExecute(ReviewInfo reviewInfo) {
                if (listener != null && reviewInfo != null) {
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
}
