package com.hahaxueche.api.student;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.model.coach.ScheduleEvent;
import com.hahaxueche.model.response.CoachListResponse;
import com.hahaxueche.model.response.ScheduleEventListResponse;
import com.hahaxueche.model.review.ReviewInfo;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.base.BaseApiResponse;
import com.hahaxueche.utils.JsonUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

/**
 * Created by gibxin on 2016/2/29.
 */
public class StudentApiImpl implements StudentApi {
    private HttpEngine httpEngine;
    private String TAG = "StudentApiImpl";

    public StudentApiImpl() {
        httpEngine = HttpEngine.getInstance();
    }

    @Override
    public Student getStudent(String student_id, String access_token) {
        Type type = new TypeToken<Student>() {
        }.getType();
        Type typeBase = new TypeToken<BaseApiResponse>() {
        }.getType();
        try {
            Response response = httpEngine.getHandle("students/" + student_id, access_token);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                Student retModel = new Student();
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, typeBase);
                retModel.setCode(baseApiResponse.getCode());
                retModel.setMessage(baseApiResponse.getMessage());
                retModel.setIsSuccess(false);
                return retModel;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public Student getStudentForever(String student_id, String access_token) {
        Type type = new TypeToken<Student>() {
        }.getType();
        Student retStudent = new Student();
        try {
            while (true) {
                if (retStudent != null && !TextUtils.isEmpty(retStudent.getCurrent_coach_id())) {
                    break;
                }
                Response response = httpEngine.getHandle("students/" + student_id, access_token);
                String body = response.body().string();
                Log.v("gibxin", "body -> " + body);
                if (response.isSuccessful()) {
                    retStudent = JsonUtils.deserialize(body, type);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return retStudent;
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public CoachListResponse getFollowCoachList(String page, String per_page, String access_token) {
        String url = "users/follows/";
        if (!TextUtils.isEmpty(page)) {
            url += "?page=" + page;
        }
        if (!TextUtils.isEmpty(per_page)) {
            url += url.indexOf("?") > 0 ? ("&per_page=" + per_page) : ("?per_page=" + per_page);
        }
        Type type = new TypeToken<CoachListResponse>() {
        }.getType();
        Type typeBase = new TypeToken<BaseApiResponse>() {
        }.getType();
        try {
            Response response = httpEngine.getHandle(url, access_token);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                CoachListResponse retModel = new CoachListResponse();
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, typeBase);
                retModel.setCode(baseApiResponse.getCode());
                retModel.setMessage(baseApiResponse.getMessage());
                retModel.setIsSuccess(false);
                return retModel;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public CoachListResponse getFollowCoachList(String url, String access_token) {
        Type type = new TypeToken<CoachListResponse>() {
        }.getType();
        Type typeBase = new TypeToken<BaseApiResponse>() {
        }.getType();
        try {
            Response response = httpEngine.getHandleByUrl(url, access_token);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, typeBase);
                CoachListResponse retModel = new CoachListResponse();
                retModel.setCode(baseApiResponse.getCode());
                retModel.setMessage(baseApiResponse.getMessage());
                retModel.setIsSuccess(false);
                return retModel;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public ReviewInfo makeReview(String coach_user_id, String payment_stage, String rating, String comment, String access_token) {
        Type type = new TypeToken<ReviewInfo>() {
        }.getType();
        Type typeBase = new TypeToken<BaseApiResponse>() {
        }.getType();
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("payment_stage", payment_stage);
        paramMap.put("rating", rating);
        paramMap.put("comment", comment);
        try {
            Response response = httpEngine.postHandle(paramMap, "/users/reviews/" + coach_user_id, access_token);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, typeBase);
                ReviewInfo retModel = new ReviewInfo();
                retModel.setCode(baseApiResponse.getCode());
                retModel.setMessage(baseApiResponse.getMessage());
                retModel.setIsSuccess(false);
                return retModel;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public BaseApiResponse loginOff(String session_id, String access_token) {
        Type type = new TypeToken<BaseApiResponse>() {
        }.getType();
        Type typeBase = new TypeToken<BaseApiResponse>() {
        }.getType();
        Map<String, String> paramMap = new HashMap<String, String>();
        try {
            Response response = httpEngine.deleteHandle(paramMap, "sessions/" + session_id, access_token);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, typeBase);
                BaseApiResponse retModel = new BaseApiResponse();
                retModel.setCode(baseApiResponse.getCode());
                retModel.setMessage(baseApiResponse.getMessage());
                retModel.setIsSuccess(false);
                return retModel;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public ScheduleEvent bookScheduleEvent(String studentId, String courseScheduleEventId, String accessToken) {
        Type type = new TypeToken<ScheduleEvent>() {
        }.getType();
        Type typeBase = new TypeToken<BaseApiResponse>() {
        }.getType();
        Map<String, String> paramMap = new HashMap<String, String>();
        try {
            Response response = httpEngine.postHandle(paramMap, "students/" + studentId + "/" + courseScheduleEventId + "/schedule", accessToken);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, typeBase);
                ScheduleEvent retModel = new ScheduleEvent();
                retModel.setCode(baseApiResponse.getCode());
                retModel.setMessage(baseApiResponse.getMessage());
                retModel.setIsSuccess(false);
                return retModel;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public ScheduleEventListResponse fetchCourseSchedule(String studentId, String page, String perPage, String booked, String accessToken) {
        Type type = new TypeToken<ScheduleEventListResponse>() {
        }.getType();
        Type typeBase = new TypeToken<BaseApiResponse>() {
        }.getType();
        String url = "students/" + studentId + "/course_schedules";
        if (!TextUtils.isEmpty(page)) {
            url += url.indexOf("?") > 0 ? ("&page=" + page) : ("?page=" + page);
        }
        if (!TextUtils.isEmpty(perPage)) {
            url += url.indexOf("?") > 0 ? ("&per_page=" + perPage) : ("?per_page=" + perPage);
        }
        if (!TextUtils.isEmpty(booked)) {
            url += url.indexOf("?") > 0 ? ("&booked=" + booked) : ("?booked=" + booked);
        }
        try {
            Response response = httpEngine.getHandle(url, accessToken);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                ScheduleEventListResponse retModel = new ScheduleEventListResponse();
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, typeBase);
                retModel.setCode(baseApiResponse.getCode());
                retModel.setMessage(baseApiResponse.getMessage());
                retModel.setIsSuccess(false);
                return retModel;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public ScheduleEventListResponse fetchCourseSchedule(String url, String accessToken) {
        Type type = new TypeToken<ScheduleEventListResponse>() {
        }.getType();
        Type typeBase = new TypeToken<BaseApiResponse>() {
        }.getType();
        try {
            Response response = httpEngine.getHandleByUrl(url, accessToken);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                ScheduleEventListResponse retModel = new ScheduleEventListResponse();
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, typeBase);
                retModel.setCode(baseApiResponse.getCode());
                retModel.setMessage(baseApiResponse.getMessage());
                retModel.setIsSuccess(false);
                return retModel;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public BaseApiResponse cancelCourseSchedule(String studentId, String scheduleEventId, String accessToken) {
        Type type = new TypeToken<BaseApiResponse>() {
        }.getType();
        Map<String, String> paramMap = new HashMap<String, String>();
        try {
            Response response = httpEngine.postHandle(paramMap, "students/" + studentId + "/" + scheduleEventId + "/unschedule", accessToken);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                BaseApiResponse retModel =JsonUtils.deserialize(body, type);
                retModel.setIsSuccess(false);
                return retModel;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public ReviewInfo reviewSchedule(String studentId, String scheduleEventId, String rating, String accessToken) {
        Type type = new TypeToken<ReviewInfo>() {
        }.getType();
        Type typeBase = new TypeToken<BaseApiResponse>() {
        }.getType();
        Map<String, String> paramMap = new HashMap<String, String>();
        try {
            Response response = httpEngine.postHandle(paramMap, "students/" + studentId + "/" + scheduleEventId + "/review_schedule_event?rating=" + rating, accessToken);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                ReviewInfo retModel = new ReviewInfo();
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, typeBase);
                retModel.setCode(baseApiResponse.getCode());
                retModel.setMessage(baseApiResponse.getMessage());
                retModel.setIsSuccess(false);
                return retModel;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }


}
