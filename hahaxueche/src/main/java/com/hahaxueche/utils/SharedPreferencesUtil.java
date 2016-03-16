package com.hahaxueche.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.model.findCoach.Location;
import com.hahaxueche.model.signupLogin.SessionModel;
import com.hahaxueche.model.signupLogin.StudentModel;
import com.hahaxueche.model.util.ConstantsModel;

import java.lang.reflect.Type;

/**
 * Created by gibxin on 2016/3/16.
 */
public class SharedPreferencesUtil {
    private Context mContext;

    public SharedPreferencesUtil(Context context) {
        mContext = context;
    }

    public SessionModel getSession() {
        SharedPreferences spSession = mContext.getSharedPreferences("session", Activity.MODE_PRIVATE);
        Type sessionType = new TypeToken<SessionModel>() {
        }.getType();
        return JsonUtils.deserialize(spSession.getString("sessionStr", ""), sessionType);
    }

    public void setSession(SessionModel session) {
        SharedPreferences spSession = mContext.getSharedPreferences("session", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spSession.edit();
        editor.putString("sessionStr", JsonUtils.serialize(session));
        editor.commit();
    }

    public void clearSession() {
        SharedPreferences spSession = mContext.getSharedPreferences("session", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spSession.edit();
        editor.clear();
        editor.commit();
    }

    public StudentModel getStudent() {
        SharedPreferences spStudent = mContext.getSharedPreferences("student", Activity.MODE_PRIVATE);
        Type studentType = new TypeToken<StudentModel>() {
        }.getType();
        return JsonUtils.deserialize(spStudent.getString("studentStr", ""), studentType);
    }

    public void setStudent(StudentModel student) {
        SharedPreferences spStudent = mContext.getSharedPreferences("student", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spStudent.edit();
        editor.putString("studentStr", JsonUtils.serialize(student));
        editor.commit();
    }

    public void clearStudent() {
        SharedPreferences spStudent = mContext.getSharedPreferences("student", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spStudent.edit();
        editor.clear();
        editor.commit();
    }

    public ConstantsModel getConstants() {
        SharedPreferences spConstants = mContext.getSharedPreferences("constants", Activity.MODE_PRIVATE);
        Type constantsType = new TypeToken<ConstantsModel>() {
        }.getType();
        return JsonUtils.deserialize(spConstants.getString("constantsStr", ""), constantsType);
    }

    public void setConstants(ConstantsModel constants) {
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

    public CoachModel getCurrentCoach() {
        SharedPreferences spCurrentCoach = mContext.getSharedPreferences("currentCoach", Activity.MODE_PRIVATE);
        Type coachType = new TypeToken<CoachModel>() {
        }.getType();
        return JsonUtils.deserialize(spCurrentCoach.getString("currentCoachStr", ""), coachType);
    }

    public void setCurrentCoach(CoachModel currentCoach) {
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

    public Location getLocation() {
        SharedPreferences spLocation = mContext.getSharedPreferences("location", Activity.MODE_PRIVATE);
        Type locationType = new TypeToken<Location>() {
        }.getType();
        return JsonUtils.deserialize(spLocation.getString("locationStr", ""), locationType);
    }

    public void setLocation(Location location){
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
}
