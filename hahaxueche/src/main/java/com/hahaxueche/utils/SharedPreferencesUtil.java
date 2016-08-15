package com.hahaxueche.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.model.activity.Event;
import com.hahaxueche.model.city.Bonus;
import com.hahaxueche.model.city.City;
import com.hahaxueche.model.coach.Coach;
import com.hahaxueche.model.city.Location;
import com.hahaxueche.model.course.Course;
import com.hahaxueche.model.student.StudentPhase;
import com.hahaxueche.model.user.Session;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.user.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by gibxin on 2016/3/16.
 */
public class SharedPreferencesUtil {
    private Context mContext;

    public SharedPreferencesUtil(Context context) {
        mContext = context;
    }

    public User getUser() {
        SharedPreferences spSession = mContext.getSharedPreferences("user", Activity.MODE_PRIVATE);
        Type sessionType = new TypeToken<User>() {
        }.getType();
        return JsonUtils.deserialize(spSession.getString("userStr", ""), sessionType);
    }

    public void setUser(User user) {
        SharedPreferences spSession = mContext.getSharedPreferences("user", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spSession.edit();
        editor.putString("userStr", JsonUtils.serialize(user));
        editor.commit();
    }

    public void clearUser() {
        SharedPreferences spSession = mContext.getSharedPreferences("user", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spSession.edit();
        editor.clear();
        editor.commit();
    }

    public Constants getConstants() {
        SharedPreferences spConstants = mContext.getSharedPreferences("constants", Activity.MODE_PRIVATE);
        Type constantsType = new TypeToken<Constants>() {
        }.getType();
        return JsonUtils.deserialize(spConstants.getString("constantsStr", ""), constantsType);
    }

    public void setConstants(Constants constants) {
        SharedPreferences spConstants = mContext.getSharedPreferences("constants", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spConstants.edit();
        editor.putString("constantsStr", JsonUtils.serialize(constants));
        editor.commit();
    }

    public void clearConstants() {
        SharedPreferences spConstants = mContext.getSharedPreferences("constants", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spConstants.edit();
        editor.clear();
        editor.commit();
    }

    public String getPhaseName(String phaseId, String cityId) {
        String phaseName = "";
        Constants constants = this.getConstants();
        if (constants != null && constants.getCities() != null && constants.getCities().size() > 0) {
            for (City city : constants.getCities()) {
                if (city.getId().equals(cityId)) {
                    ArrayList<StudentPhase> studentPhases = city.getStudent_phases();
                    if (studentPhases != null && studentPhases.size() > 0) {
                        for (StudentPhase studentPhase : studentPhases) {
                            if (studentPhase.getId().equals(phaseId)) {
                                phaseName = studentPhase.getDisplay_name();
                                break;
                            }
                        }
                    }
                }
            }
        }
        return phaseName;
    }

    public String getCourseName(String courseId, String cityId) {
        String courseName = "";
        Constants constants = this.getConstants();
        if (constants != null && constants.getCities() != null && constants.getCities().size() > 0) {
            for (City city : constants.getCities()) {
                if (city.getId().equals(cityId)) {
                    ArrayList<Course> courses = city.getCourses();
                    if (courses != null && courses.size() > 0) {
                        for (Course course : courses) {
                            if (course.getId().equals(courseId)) {
                                courseName = course.getDisplay_name();
                                break;
                            }
                        }
                    }
                }
            }
        }
        return courseName;
    }

    public Location getLocation() {
        SharedPreferences spLocation = mContext.getSharedPreferences("location", Activity.MODE_PRIVATE);
        Type locationType = new TypeToken<Location>() {
        }.getType();
        return JsonUtils.deserialize(spLocation.getString("locationStr", ""), locationType);
    }

    public void setLocation(Location location) {
        SharedPreferences spLocation = mContext.getSharedPreferences("location", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spLocation.edit();
        editor.putString("locationStr", JsonUtils.serialize(location));
        editor.commit();
    }

    public void clearLocation() {
        SharedPreferences spLocation = mContext.getSharedPreferences("location", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spLocation.edit();
        editor.clear();
        editor.commit();
    }

    public City getMyCity() {
        User user = getUser();
        int myCityCount = 0;
        Constants constants = getConstants();
        if (constants == null) return null;
        List<City> cityList = constants.getCities();
        if (null != user.getStudent() && !TextUtils.isEmpty(user.getStudent().getCity_id())) {
            String city_id = user.getStudent().getCity_id();
            for (int i = 0; i < cityList.size(); i++) {
                if (cityList.get(i).getId().equals(city_id)) {
                    myCityCount = i;
                    break;
                }
            }
        }
        return cityList.get(myCityCount);
    }

    /**
     * 获取我的城市推荐人的奖励
     *
     * @return
     */
    public int getMyCityRefererBonus() {
        City myCity = getMyCity();
        int amount = 0;
        ArrayList<Bonus> bonusList = myCity.getReferal_bonus();
        if (bonusList != null && bonusList.size() > 0) {
            for (Bonus bonus : bonusList) {
                if (bonus.getName().equals("referer_bonus")) {
                    amount = bonus.getAmount();
                    break;
                }
            }
        }
        return amount;
    }

    /**
     * 获取我的城市被推荐人的奖励
     *
     * @return
     */
    public int getMyCityRefereeBonus() {
        City myCity = getMyCity();
        int amount = 0;
        ArrayList<Bonus> bonusList = myCity.getReferal_bonus();
        if (bonusList != null && bonusList.size() > 0) {
            for (Bonus bonus : bonusList) {
                if (bonus.getName().equals("referee_bonus")) {
                    amount = bonus.getAmount();
                    break;
                }
            }
        }
        return amount;
    }

    public Coach getCurrentCoach() {
        SharedPreferences spCurrentCoach = mContext.getSharedPreferences("currentCoach", Activity.MODE_PRIVATE);
        Type coachType = new TypeToken<Coach>() {
        }.getType();
        return JsonUtils.deserialize(spCurrentCoach.getString("currentCoachStr", ""), coachType);
    }

    public void setCurrentCoach(Coach currentCoach) {
        SharedPreferences spCurrentCoach = mContext.getSharedPreferences("currentCoach", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spCurrentCoach.edit();
        editor.putString("currentCoachStr", JsonUtils.serialize(currentCoach));
        editor.commit();
    }

    public void clearCurrentCoach() {
        SharedPreferences spCurrentCoach = mContext.getSharedPreferences("currentCoach", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spCurrentCoach.edit();
        editor.clear();
        editor.commit();
    }

    public String getRefererId() {
        SharedPreferences spReferer = mContext.getSharedPreferences("refererId", Activity.MODE_PRIVATE);
        return spReferer.getString("refererId", "");
    }

    public void setRefererId(String refererId) {
        SharedPreferences spReferer = mContext.getSharedPreferences("refererId", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spReferer.edit();
        editor.putString("refererId", refererId);
        editor.commit();
    }

    public void clearRefererId() {
        SharedPreferences spReferer = mContext.getSharedPreferences("refererId", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spReferer.edit();
        editor.clear();
        editor.commit();
    }

    public void setNoticeBonus(boolean firstBonus) {
        SharedPreferences spNotice = mContext.getSharedPreferences("notice", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spNotice.edit();
        editor.putBoolean("firstBonus", firstBonus);
        editor.commit();
    }

    public boolean getNoticeBouns() {
        SharedPreferences spNotice = mContext.getSharedPreferences("notice", Activity.MODE_PRIVATE);
        return spNotice.getBoolean("firstBonus", false);
    }

    /**
     * 增加历史搜索记录
     *
     * @param coachName
     */
    public void addSearchHistory(String coachName) {
        SharedPreferences spHistory = mContext.getSharedPreferences("searchHistory", Activity.MODE_PRIVATE);
        LinkedList<String> searchHistoryList = JsonUtils.deserialize(spHistory.getString("searchHistoryStr", ""), LinkedList.class);
        if (searchHistoryList != null) {
            if (searchHistoryList.size() >= 4) {
                searchHistoryList.removeFirst();
            }
        } else {
            searchHistoryList = new LinkedList<>();
        }
        searchHistoryList.addLast(coachName);
        SharedPreferences.Editor editor = spHistory.edit();
        editor.putString("searchHistoryStr", JsonUtils.serialize(searchHistoryList));
        editor.commit();
    }

    public LinkedList getSearchHistory() {
        SharedPreferences spHistory = mContext.getSharedPreferences("searchHistory", Activity.MODE_PRIVATE);
        return JsonUtils.deserialize(spHistory.getString("searchHistoryStr", ""), LinkedList.class);
    }

    public void clearSearchHistory() {
        SharedPreferences spHistory = mContext.getSharedPreferences("searchHistory", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spHistory.edit();
        editor.clear();
        editor.commit();
    }

    public void setEvents(ArrayList<Event> events) {
        SharedPreferences spEvents = mContext.getSharedPreferences("events", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spEvents.edit();
        editor.putString("eventsStr", JsonUtils.serialize(events));
        editor.commit();
    }

    public ArrayList<Event> getEvents() {
        SharedPreferences spEvents = mContext.getSharedPreferences("events", Activity.MODE_PRIVATE);
        Type type = new TypeToken<ArrayList<Event>>() {
        }.getType();
        ArrayList<Event> ret;
        try {
            ret = JsonUtils.deserialize(spEvents.getString("eventsStr", ""), type);
            return ret;
        } catch (Exception e) {
            return null;
        }

    }

    public void setExamPosition(String examType, int position) {
        SharedPreferences spExam = mContext.getSharedPreferences(examType, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spExam.edit();
        editor.putInt("position", position);
        editor.commit();
    }

    public int getExamPosition(String examType) {
        SharedPreferences spExam = mContext.getSharedPreferences(examType, Activity.MODE_PRIVATE);
        return spExam.getInt("position", -1);
    }

    public void clearExamPosition(String examType) {
        SharedPreferences spExam = mContext.getSharedPreferences(examType, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spExam.edit();
        editor.clear();
        editor.commit();
    }

}
