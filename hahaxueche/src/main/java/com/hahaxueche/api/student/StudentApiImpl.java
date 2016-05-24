package com.hahaxueche.api.student;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.model.coach.ScheduleEvent;
import com.hahaxueche.model.response.CoachListResponse;
import com.hahaxueche.model.response.GroupBuyResponse;
import com.hahaxueche.model.response.ReferalHistoryResponse;
import com.hahaxueche.model.response.RefereeListResponse;
import com.hahaxueche.model.response.ScheduleEventListResponse;
import com.hahaxueche.model.review.ReviewInfo;
import com.hahaxueche.model.student.ReferalBonusSummary;
import com.hahaxueche.model.student.ReferalBonusTransaction;
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
        Map<String, Object> paramMap = new HashMap<>();
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
        Map<String, Object> paramMap = new HashMap<>();
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
        Map<String, Object> paramMap = new HashMap<>();
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
        Map<String, Object> paramMap = new HashMap<>();
        try {
            Response response = httpEngine.postHandle(paramMap, "students/" + studentId + "/" + scheduleEventId + "/unschedule", accessToken);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                BaseApiResponse retModel = JsonUtils.deserialize(body, type);
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
        Map<String, Object> paramMap = new HashMap<>();
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

    @Override
    public RefereeListResponse fetchRefereeList(String studentId, String page, String perPage, String accessToken) {
        Type type = new TypeToken<RefereeListResponse>() {
        }.getType();
        String url = "students/" + studentId + "/referees";
        if (!TextUtils.isEmpty(page)) {
            url += url.indexOf("?") > 0 ? ("&page=" + page) : ("?page=" + page);
        }
        if (!TextUtils.isEmpty(perPage)) {
            url += url.indexOf("?") > 0 ? ("&per_page=" + perPage) : ("?per_page=" + perPage);
        }
        try {
            Response response = httpEngine.getHandle(url, accessToken);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                RefereeListResponse retModel = new RefereeListResponse();
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, BaseApiResponse.class);
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
    public RefereeListResponse fetchRefereeList(String url, String accessToken) {
        Type type = new TypeToken<RefereeListResponse>() {
        }.getType();
        try {
            Response response = httpEngine.getHandleByUrl(url, accessToken);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                RefereeListResponse retModel = new RefereeListResponse();
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, BaseApiResponse.class);
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
    public ReferalHistoryResponse fetchReferalHistoryList(String studentId, String page, String perPage, String accessToken) {
        Type type = new TypeToken<ReferalHistoryResponse>() {
        }.getType();
        String url = "students/" + studentId + "/referal_bonus_transactions";
        if (!TextUtils.isEmpty(page)) {
            url += url.indexOf("?") > 0 ? ("&page=" + page) : ("?page=" + page);
        }
        if (!TextUtils.isEmpty(perPage)) {
            url += url.indexOf("?") > 0 ? ("&per_page=" + perPage) : ("?per_page=" + perPage);
        }
        try {
            Response response = httpEngine.getHandle(url, accessToken);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                ReferalHistoryResponse retModel = new ReferalHistoryResponse();
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, BaseApiResponse.class);
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
    public ReferalHistoryResponse fetchReferalHistoryList(String url, String accessToken) {
        Type type = new TypeToken<ReferalHistoryResponse>() {
        }.getType();
        try {
            Response response = httpEngine.getHandleByUrl(url, accessToken);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                ReferalHistoryResponse retModel = new ReferalHistoryResponse();
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, BaseApiResponse.class);
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
    public ReferalBonusSummary fetchBonusSummary(String studentId, String accessToken) {
        Type type = new TypeToken<ReferalBonusSummary>() {
        }.getType();
        String url = "students/" + studentId + "/referal_bonus_summary";
        try {
            Response response = httpEngine.getHandle(url, accessToken);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                ReferalBonusSummary retModel = new ReferalBonusSummary();
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, BaseApiResponse.class);
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
    public ReferalBonusTransaction withdrawBonus(String studentId, String account, String accountOwnerName, String amount, String accessToken) {
        Type type = new TypeToken<ReferalBonusTransaction>() {
        }.getType();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("account", account);
        paramMap.put("account_owner_name", accountOwnerName);
        paramMap.put("amount", amount);
        try {
            Response response = httpEngine.postHandle(paramMap, "students/" + studentId + "/withdraw_referal_bonus", accessToken);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                ReferalBonusTransaction retModel = new ReferalBonusTransaction();
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, BaseApiResponse.class);
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
    public GroupBuyResponse createGroupBuy(String name, String phone) {
        Type type = new TypeToken<GroupBuyResponse>() {
        }.getType();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        paramMap.put("phone", phone);
        try {
            Response response = httpEngine.postHandle(paramMap, "activity_users", "");
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                GroupBuyResponse retModel = new GroupBuyResponse();
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, BaseApiResponse.class);
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
