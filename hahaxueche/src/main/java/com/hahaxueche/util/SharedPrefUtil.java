package com.hahaxueche.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.User;

/**
 * Created by wangshirui on 16/9/10.
 */
public class SharedPrefUtil {
    private Context mContext;
    private Gson mGson;

    public SharedPrefUtil(Context mContext) {
        this.mContext = mContext;
        this.mGson = new Gson();
    }

    public User getUser() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        return mGson.fromJson(prefs.getString("usrSerialize", ""), User.class);
    }

    public void setUser(User user) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putString("usrSerialize", mGson.toJson(user)).apply();
    }

    public String getStudentId() {
        String studentId = "";
        try {
            studentId = getUser().student.id;
        } catch (Exception e) {
            HHLog.e(e.getMessage());
        }
        return studentId;
    }

    public String getAccessToken() {
        String accessToken = "";
        try {
            accessToken = getUser().session.access_token;
        } catch (Exception e) {
            HHLog.e(e.getMessage());
        }
        return accessToken;
    }

    public void updateStudent(Student student) {
        User user = getUser();
        user.student = student;
        setUser(user);
    }
}
