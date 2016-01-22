package com.hahaxueche.api.net;

import android.util.Log;

import com.hahaxueche.utils.JsonUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Http引擎处理类
 * Created by gibxin on 2016/1/22.
 */
public class HttpEngine {
    private final static String TAG = "HttpEngine";
    private final static String SERVER_URL = "http://staging-api.hahaxueche.net:8000/api/v1/";
    private final static int TIME_OUT = 20000;
    private static HttpEngine instance = null;

    private HttpEngine() {

    }

    public static HttpEngine getInstance() {
        if (instance == null) {
            instance = new HttpEngine();
        }
        return instance;
    }

    public <T> T postHandle(Map<String, String> param, Type typeOfT, String url) throws IOException {
        String jsonParam = JsonUtils.serialize(param);
        Log.i(TAG, "jsonParam->" + jsonParam);
        String response = OkHttpUtils.post(SERVER_URL + url, jsonParam);
        Log.i(TAG, "response:->" + response);
        return JsonUtils.deserialize(response, typeOfT);
    }

}
