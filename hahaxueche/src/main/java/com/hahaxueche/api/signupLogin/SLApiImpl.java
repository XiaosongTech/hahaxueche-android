package com.hahaxueche.api.signupLogin;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.model.response.CreateUserResponse;
import com.hahaxueche.model.student.StudentModel;
import com.hahaxueche.model.base.BaseApiResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gibxin on 2016/1/22.
 */
public class SLApiImpl implements SLApi {
    private HttpEngine httpEngine;
    private String TAG = "SLApiImpl";

    public SLApiImpl() {
        httpEngine = HttpEngine.getInstance();
    }

    /**
     * 发送验证码
     *
     * @param phoneNum  手机号
     * @param send_type 类型
     * @return
     */
    @Override
    public BaseApiResponse sendAuthToken(String phoneNum, String send_type) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("cell_phone", phoneNum);
        paramMap.put("type", send_type);
        Type type = new TypeToken<BaseApiResponse>() {
        }.getType();
        try {
            return httpEngine.postHandle(paramMap, type, SLApi.SEND_AUTH_TOKEN);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 创建注册用户
     *
     * @param phoneNum     手机号
     * @param identifyCode 短信验证码
     * @param pwd          密码
     * @param user_type    类型(coach,student)
     * @return
     */
    @Override
    public CreateUserResponse createUser(String phoneNum, String identifyCode, String pwd, String user_type) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("cell_phone", phoneNum);
        paramMap.put("auth_token", identifyCode);
        paramMap.put("password", pwd);
        paramMap.put("user_type", user_type);
        Type type = new TypeToken<CreateUserResponse>() {
        }.getType();
        try {
            return httpEngine.postHandle(paramMap, type, SLApi.CREATE_USER);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    /**
     * 完善学生资料
     *
     * @param studentId   学生id
     * @param cityId      城市id
     * @param studentName 姓名
     * @param accessToken Access Token
     * @return
     */
    @Override
    public StudentModel completeStuInfo(String studentId, String cityId, String studentName, String accessToken) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("name", studentName);
        paramMap.put("city_id", cityId);
        Type type = new TypeToken<StudentModel>() {
        }.getType();
        try {
            return httpEngine.putHandle(paramMap, type, SLApi.STUDENTS + "/" + studentId, accessToken);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public CreateUserResponse login(String cell_phone, String pwd, int loginType) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("cell_phone", cell_phone);
        if (loginType == 1) {
            paramMap.put("auth_token", pwd);
        } else if (loginType == 2) {
            paramMap.put("password", pwd);
        }
        Type type = new TypeToken<CreateUserResponse>() {
        }.getType();
        try {
            return httpEngine.postHandle(paramMap, type, SLApi.SESSIONS);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public BaseApiResponse resetPassword(String cell_phone, String password, String auth_token) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("auth_token", auth_token);
        paramMap.put("password", password);
        paramMap.put("password_confirmation", password);
        paramMap.put("cell_phone", cell_phone);
        Type type = new TypeToken<BaseApiResponse>() {
        }.getType();
        try {
            return httpEngine.postHandle(paramMap, type, SLApi.RESET_PASSWORD);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public StudentModel uploadAvatar(String access_token, String filePath, String studentId) {
        Type type = new TypeToken<StudentModel>() {
        }.getType();
        try {
            return httpEngine.postHandle(type, SLApi.STUDENTS + "/" + studentId + "/"+SLApi.AVATAR, access_token, filePath);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }
}
