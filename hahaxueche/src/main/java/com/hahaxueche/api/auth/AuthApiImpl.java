package com.hahaxueche.api.auth;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.base.BaseApiResponse;
import com.hahaxueche.utils.JsonUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

/**
 * Created by gibxin on 2016/1/22.
 */
public class AuthApiImpl implements AuthApi {
    private HttpEngine httpEngine;
    private String TAG = "AuthApiImpl";

    public AuthApiImpl() {
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
            Response response = httpEngine.postHandle(paramMap, "send_auth_token", "");
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                BaseApiResponse retModel = JsonUtils.deserialize(body, type);
                retModel.setIsSuccess(false);
                return retModel;
            }
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
    public User createUser(String phoneNum, String identifyCode, String pwd, String user_type) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("cell_phone", phoneNum);
        paramMap.put("auth_token", identifyCode);
        paramMap.put("password", pwd);
        paramMap.put("user_type", user_type);
        Type type = new TypeToken<User>() {
        }.getType();
        Type typeBase = new TypeToken<BaseApiResponse>() {
        }.getType();
        try {
            Response response = httpEngine.postHandle(paramMap, "users", "");
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                User retModel = new User();
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, typeBase);
                retModel.setCode(baseApiResponse.getCode());
                retModel.setMessage(baseApiResponse.getMessage());
                retModel.setIsSuccess(false);
                return retModel;
            }
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
    public Student completeStuInfo(String studentId, String cityId, String studentName, String accessToken) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("name", studentName);
        paramMap.put("city_id", cityId);
        Type type = new TypeToken<Student>() {
        }.getType();
        Type typeBase = new TypeToken<BaseApiResponse>() {
        }.getType();
        try {
            Response response = httpEngine.putHandle(paramMap, "students/" + studentId, accessToken);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                Student retModel = new Student();
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, typeBase);
                retModel.setCode(baseApiResponse.getCode());
                retModel.setMessage(baseApiResponse.getMessage());
                retModel.setIsSuccess(false);
                return retModel;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public User login(String cell_phone, String pwd, int loginType) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("cell_phone", cell_phone);
        if (loginType == 1) {
            paramMap.put("auth_token", pwd);
        } else if (loginType == 2) {
            paramMap.put("password", pwd);
        }
        Type type = new TypeToken<User>() {
        }.getType();
        Type typeBase = new TypeToken<BaseApiResponse>() {
        }.getType();
        try {
            Response response = httpEngine.postHandle(paramMap, "sessions", "");
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, typeBase);
                User retModel = new User();
                retModel.setCode(baseApiResponse.getCode());
                retModel.setMessage(baseApiResponse.getMessage());
                retModel.setIsSuccess(false);
                return retModel;
            }
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
            Response response = httpEngine.postHandle(paramMap, "users/reset_password", "");
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                BaseApiResponse retModel = JsonUtils.deserialize(body, type);
                retModel.setIsSuccess(false);
                return retModel;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public Student uploadAvatar(String access_token, String filePath, String studentId) {
        Type type = new TypeToken<Student>() {
        }.getType();
        Type typeBase = new TypeToken<BaseApiResponse>() {
        }.getType();
        try {
            Response response = httpEngine.postFileHandle("students/" + studentId + "/avatar", access_token, filePath);
            String body = response.body().string();
            Log.v("gibxin", "body -> " + body);
            if (response.isSuccessful()) {
                return JsonUtils.deserialize(body, type);
            } else {
                BaseApiResponse baseApiResponse = JsonUtils.deserialize(body, typeBase);
                Student retModel = new Student();
                retModel.setCode(baseApiResponse.getCode());
                retModel.setMessage(baseApiResponse.getMessage());
                retModel.setIsSuccess(false);
                return retModel;
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }
}
