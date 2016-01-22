package com.hahaxueche.api.signupLogin;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.api.ApiResponse;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.api.util.ApiError;
import com.hahaxueche.model.util.BaseApiResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gibxin on 2016/1/22.
 */
public class SLApiImpl implements SLApi {
    private HttpEngine httpEngine;

    public SLApiImpl() {
        httpEngine = HttpEngine.getInstance();
    }

    /**
     * 发送验证码
     *
     * @param phoneNum 手机号
     * @param userType 类型
     * @return
     */
    @Override
    public BaseApiResponse sendAuthToken(String phoneNum, String userType) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("cell_phone", phoneNum);
        paramMap.put("type", userType);
        Type type = new TypeToken<BaseApiResponse>() {
        }.getType();
        try {
            return httpEngine.postHandle(paramMap, type, SLApi.SEND_AUTH_TOKEN);
        } catch (IOException e) {
            return null;
        }
    }
}
