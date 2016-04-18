package com.hahaxueche.api.student;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.model.response.CoachListResponse;
import com.hahaxueche.model.review.ReviewInfo;
import com.hahaxueche.model.student.StudentModel;
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
    public StudentModel getStudent(String student_id, String access_token) {
        Type type = new TypeToken<StudentModel>() {
        }.getType();
        try {
            Response response = httpEngine.getHandle("students/" + student_id, access_token);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                StudentModel retModel = new StudentModel();
                retModel.setCode(String.valueOf(response.code()));
                retModel.setMessage(response.message());
                retModel.setIsSuccess(false);
                return retModel;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public StudentModel getStudentForever(String student_id, String access_token) {
        Type type = new TypeToken<StudentModel>() {
        }.getType();
        StudentModel retStudent = new StudentModel();
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
        try {
            Response response = httpEngine.getHandle(url, access_token);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                CoachListResponse retModel = new CoachListResponse();
                retModel.setCode(String.valueOf(response.code()));
                retModel.setMessage(response.message());
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
        try {
            Response response = httpEngine.getHandleByUrl(url, access_token);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                CoachListResponse retModel = new CoachListResponse();
                retModel.setCode(String.valueOf(response.code()));
                retModel.setMessage(response.message());
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
                ReviewInfo retModel = new ReviewInfo();
                retModel.setCode(String.valueOf(response.code()));
                retModel.setMessage(response.message());
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
        Map<String, String> paramMap = new HashMap<String, String>();
        try {
            Response response = httpEngine.deleteHandle(paramMap, "sessions/" + session_id, access_token);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                BaseApiResponse retModel = new BaseApiResponse();
                retModel.setCode(String.valueOf(response.code()));
                retModel.setMessage(response.message());
                retModel.setIsSuccess(false);
                return retModel;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }
}
