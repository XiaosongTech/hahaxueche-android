package com.hahaxueche.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.examLib.Question;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.model.user.User;

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
        ArrayList<String> collectList = mGson.fromJson(prefs.getString(examType + "collectList", ""), ArrayList.class);
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

    public ArrayList<Question> getQuestions1() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return mGson.fromJson(prefs.getString("questions1Serialize", ""), ArrayList.class);
    }

    public void setQuestions1(ArrayList<Question> questions1) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putString("questions1Serialize", mGson.toJson(questions1)).apply();
    }

    public ArrayList<Question> getQuestions4() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return mGson.fromJson(prefs.getString("questions4Serialize", ""), ArrayList.class);
    }

    public void setQuestions4(ArrayList<Question> questions4) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putString("questions4Serialize", mGson.toJson(questions4)).apply();
    }
}
