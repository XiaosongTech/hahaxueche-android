package com.hahaxueche.api.signupLogin;

import com.hahaxueche.api.ApiResponse;
import com.hahaxueche.model.util.BaseApiResponse;

/**
 * Created by gibxin on 2016/1/22.
 */
public interface SLApi {
    public static final String SEND_AUTH_TOKEN = "send_auth_token";

    /**
     *
     * @param phoneNum 手机号
     * @param type     类型（register,login）
     * @return
     */
    public BaseApiResponse sendAuthToken(String phoneNum,String type);
}
