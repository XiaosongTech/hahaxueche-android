package com.hahaxueche.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.User;

import java.util.LinkedList;

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
        return mGson.fromJson(prefs.getString("usrSerialize", ""), User.class);
    }

    public void setUser(User user) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putString("usrSerialize", mGson.toJson(user)).apply();
    }

    public void createFakeUser() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        User user = new User();
        user.student = new Student();
        prefs.edit().putString("usrSerialize", mGson.toJson(user)).apply();
    }

    public void setUserCity(int cityId) {
        User user = getUser();
        user.student.city_id = cityId;
        setUser(user);
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

    /**
     * 增加历史搜索记录
     *
     * @param coachName
     */
    public void addSearchHistory(String coachName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        LinkedList<String> searchHistoryList = getSearchHistory();
        if (searchHistoryList != null) {
            if (searchHistoryList.size() >= 4) {
                searchHistoryList.removeFirst();
            }
        } else {
            searchHistoryList = new LinkedList<>();
        }
        searchHistoryList.addLast(coachName);
        prefs.edit().putString("searchHistorySerialize", mGson.toJson(searchHistoryList)).apply();
    }

    public LinkedList getSearchHistory() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return mGson.fromJson(prefs.getString("searchHistorySerialize", ""), LinkedList.class);
    }

    public void clearSearchHistory() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putString("searchHistorySerialize", null).apply();
    }
}
