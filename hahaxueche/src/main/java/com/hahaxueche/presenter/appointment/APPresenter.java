package com.hahaxueche.presenter.appointment;

import com.hahaxueche.model.base.BaseApiResponse;
import com.hahaxueche.model.coach.ScheduleEvent;
import com.hahaxueche.model.response.ScheduleEventListResponse;
import com.hahaxueche.model.review.ReviewInfo;
import com.hahaxueche.presenter.BaseCallbackListener;

/**
 * Created by gibxin on 2016/4/18.
 */
public interface APPresenter {
    public void bookCourseSchedule(String studentId, String courseScheduleEventId, String accessToken, BaseCallbackListener<ScheduleEvent> listener);

    public void fetchCourseSchedule(String studentId, String page, String perPage, String booked, String accessToken, BaseCallbackListener<ScheduleEventListResponse> listener);

    public void fetchCourseSchedule(String url, String accessToken, BaseCallbackListener<ScheduleEventListResponse> listener);

    public void cancelCourseSchedule(String studentId, String scheduleEventId, String accessToken, BaseCallbackListener<BaseApiResponse> listener);

    public void reviewSchedule(String studentId, String scheduleEventId, String rating, String accessToken, BaseCallbackListener<ReviewInfo> listener);
}
