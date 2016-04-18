package com.hahaxueche.api.mySetting;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.model.response.CoachListResponse;
import com.hahaxueche.model.review.ReviewInfo;
import com.hahaxueche.model.student.StudentModel;
import com.hahaxueche.model.base.BaseApiResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gibxin on 2016/2/29.
 */
public class MSApiImpl implements MSApi {
    private HttpEngine httpEngine;
    private String TAG = "MSApiImpl";

    public MSApiImpl() {
        httpEngine = HttpEngine.getInstance();
    }

    @Override
    public StudentModel getStudent(String student_id, String access_token) {
        Type type = new TypeToken<StudentModel>() {
        }.getType();
        try {
            return httpEngine.getHandle(type, "students/" + student_id, access_token);
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
                if(retStudent != null && !TextUtils.isEmpty(retStudent.getCurrent_coach_id())){
                    break;
                }
                retStudent = httpEngine.getHandle(type, "students/" + student_id, access_token);
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
            return httpEngine.getHandle(type, url, access_token);
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
            return httpEngine.getHandleByUrl(type, url, access_token);
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
            return httpEngine.postHandleWithX(paramMap, type, "/users/reviews/" + coach_user_id, access_token);
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
            return httpEngine.deleteHandle(paramMap, type, "sessions/" + session_id, access_token);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }
}
