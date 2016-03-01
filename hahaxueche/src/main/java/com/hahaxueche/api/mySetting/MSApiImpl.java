package com.hahaxueche.api.mySetting;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.model.findCoach.CoachListResponse;
import com.hahaxueche.model.findCoach.GetReviewsResponse;
import com.hahaxueche.model.signupLogin.StudentModel;

import java.io.IOException;
import java.lang.reflect.Type;

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
}
