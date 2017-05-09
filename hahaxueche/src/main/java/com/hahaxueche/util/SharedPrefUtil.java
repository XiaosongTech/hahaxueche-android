package com.hahaxueche.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.hahaxueche.model.base.CityConstants;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.base.LocalSettings;
import com.hahaxueche.model.examLib.Question;
import com.hahaxueche.model.responseList.FieldResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.student.Student;

import java.util.ArrayList;
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
     * 增加教练历史搜索记录
     *
     * @param coachName
     */
    public void addSearchCoachHistory(String coachName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        LinkedList<String> searchHistoryList = getSearchCoachHistory();
        if (searchHistoryList != null) {
            if (searchHistoryList.contains(coachName)) return;
            if (searchHistoryList.size() >= 4) {
                searchHistoryList.removeFirst();
            }
        } else {
            searchHistoryList = new LinkedList<>();
        }
        searchHistoryList.addLast(coachName);
        prefs.edit().putString("searchCoachHistorySerialize", mGson.toJson(searchHistoryList)).apply();
    }

    public LinkedList getSearchCoachHistory() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return mGson.fromJson(prefs.getString("searchCoachHistorySerialize", ""), LinkedList.class);
    }

    public void clearSearchCoachHistory() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putString("searchCoachHistorySerialize", null).apply();
    }

    /**
     * 增加驾校历史搜索记录
     *
     * @param drivingSchool
     */
    public void addSearchDrivingSchoolHistory(String drivingSchool) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        LinkedList<String> searchHistoryList = getSearchDrivingSchoolHistory();
        if (searchHistoryList != null) {
            if (searchHistoryList.contains(drivingSchool)) return;
            if (searchHistoryList.size() >= 4) {
                searchHistoryList.removeFirst();
            }
        } else {
            searchHistoryList = new LinkedList<>();
        }
        searchHistoryList.addLast(drivingSchool);
        prefs.edit().putString("searchDrivingSchoolHistorySerialize", mGson.toJson(searchHistoryList)).apply();
    }

    public LinkedList getSearchDrivingSchoolHistory() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return mGson.fromJson(prefs.getString("searchDrivingSchoolHistorySerialize", ""), LinkedList.class);
    }

    public void clearSearchDrivingSchoolHistory() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putString("searchDrivingSchoolHistorySerialize", null).apply();
    }

    public void setExamPosition(String examType, int position) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putInt(examType + "Pos", position).apply();
    }


    public int getExamPosition(String examType) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getInt(examType + "Pos", 0);
    }

    public void clearExamPosition(String examType) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putInt(examType + "Pos", 0).apply();
    }

    public void addQuestionCollect(String examType, String id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        ArrayList<String> collectList = mGson.fromJson(prefs.getString(examType + "collectList", ""), ArrayList.class);
        if (collectList != null) {
            if (!collectList.contains(id)) {
                collectList.add(id);
            }
        } else {
            collectList = new ArrayList<>();
            collectList.add(id);
        }
        prefs.edit().putString(examType + "collectList", mGson.toJson(collectList)).apply();
    }

    public void removeQuestionCollect(String examType, String id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        ArrayList<String> collectList = mGson.fromJson(prefs.getString(examType + "collectList", ""), ArrayList.class);
        if (collectList != null && collectList.contains(id)) {
            collectList.remove(id);
        }
        prefs.edit().putString(examType + "collectList", mGson.toJson(collectList)).apply();
    }

    public boolean isQuestionCollect(String examType, String id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        ArrayList<String> collectList = mGson.fromJson(prefs.getString(examType +
                "collectList", ""), ArrayList.class);
        return (collectList != null && collectList.contains(id));
    }

    public ArrayList<Question> getCollectList(ArrayList<Question> questions, String examType) {
        ArrayList<Question> collectList = new ArrayList();
        for (Question question : questions) {
            if (isQuestionCollect(examType, question.question_id)) {
                collectList.add(question);
            }
        }
        return collectList;
    }

    public Constants getConstants() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return mGson.fromJson(prefs.getString("constantsSerialize", ""), Constants.class);
    }

    public void setConstants(Constants constants) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putString("constantsSerialize", mGson.toJson(constants)).apply();
    }

    public CityConstants getCityConstants() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return mGson.fromJson(prefs.getString("cityConstantsSerialize", ""), CityConstants.class);
    }

    public void setCityConstants(CityConstants cityConstants) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putString("cityConstantsSerialize", mGson.toJson(cityConstants)).apply();
    }

    public LocalSettings getLocalSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        LocalSettings localSettings = mGson.fromJson(prefs.getString("localSettingsSerialize", ""), LocalSettings.class);
        if (localSettings == null) {
            localSettings = new LocalSettings();
        }
        return localSettings;
    }

    public void setLocalSettings(LocalSettings localSettings) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putString("localSettingsSerialize", mGson.toJson(localSettings)).apply();
    }

    public FieldResponseList getFieldResponseList() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return mGson.fromJson(prefs.getString("fieldResponseListSerialize", ""), FieldResponseList.class);
    }

    public void setFieldResponseList(FieldResponseList fieldResponseList) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putString("fieldResponseListSerialize", mGson.toJson(fieldResponseList)).apply();
    }
}
