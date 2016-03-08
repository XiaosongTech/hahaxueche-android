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
    /**
     * prod -> http://api.hahaxueche.net/api/v1/
     * staging -> http://staging-api.hahaxueche.net/api/v1/
     */
    private final static String SERVER_URL = "http://api.hahaxueche.net/api/v1/";
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

    /**
     * post 方法
     *
     * @param param
     * @param typeOfT
     * @param url
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T postHandle(Map<String, String> param, Type typeOfT, String url) throws IOException {
        String jsonParam = JsonUtils.serialize(param);
        Log.i(TAG, "jsonParam->" + jsonParam);
        String response = OkHttpUtils.post(SERVER_URL + url, jsonParam);
        Log.i(TAG, "response:->" + response);
        return JsonUtils.deserialize(response, typeOfT);
    }

    /**
     * put方法
     *
     * @param param
     * @param typeOfT
     * @param url
     * @param accessToken
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T putHandle(Map<String, String> param, Type typeOfT, String url, String accessToken) throws IOException {
        String jsonParam = JsonUtils.serialize(param);
        Log.i(TAG, "jsonParam->" + jsonParam);
        String response = OkHttpUtils.put(SERVER_URL + url, jsonParam, accessToken);
        Log.i(TAG, "response:->" + response);
        return JsonUtils.deserialize(response, typeOfT);
    }

    public <T> T postHandleWithX(Map<String, String> param, Type typeOfT, String url, String accessToken) throws IOException {
        String jsonParam = JsonUtils.serialize(param);
        Log.i(TAG, "jsonParam->" + jsonParam);
        String response = OkHttpUtils.postWithX(SERVER_URL + url, jsonParam, accessToken);
        Log.i(TAG, "response:->" + response);
        return JsonUtils.deserialize(response, typeOfT);
    }

    public String postHandleWithXRaw(Map<String, String> param, String url, String accessToken) throws IOException {
        String jsonParam = JsonUtils.serialize(param);
        Log.i(TAG, "jsonParam->" + jsonParam);
        String response = OkHttpUtils.postWithX(SERVER_URL + url, jsonParam, accessToken);
        Log.i(TAG, "response:->" + response);
        return response;
    }

    /**
     * post文件上传方法
     *
     * @param url
     * @param accessToken
     * @param filePath
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T postHandle(Type typeOfT, String url, String accessToken, String filePath) throws IOException {
        Log.i(TAG, "file upload request:->" + url);
        Log.i(TAG, "filePath :->" + filePath);
        String response = OkHttpUtils.post(SERVER_URL + url, accessToken, filePath);
        Log.i(TAG, "response:->" + response);
        return JsonUtils.deserialize(response, typeOfT);
    }

    /**
     * get方法
     *
     * @param typeOfT
     * @param url
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T getHandle(Type typeOfT, String url) throws IOException {
        Log.i(TAG, "get url ->" + SERVER_URL + url);
        String response = OkHttpUtils.get(SERVER_URL + url);
        Log.i(TAG, "response:->" + response);
        return JsonUtils.deserialize(response, typeOfT);
    }

    public <T> T getHandle(Type typeOfT, String url, String accessToken) throws IOException {
        Log.i(TAG, "get url ->" + SERVER_URL + url);
        String response = OkHttpUtils.get(SERVER_URL + url, accessToken);
        Log.i(TAG, "response:->" + response);
        return JsonUtils.deserialize(response, typeOfT);
    }

    /**
     * get方法（直接根据url获取）
     *
     * @param typeOfT
     * @param url
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T getHandleByUrl(Type typeOfT, String url) throws IOException {
        Log.i(TAG, "get url ->" + url);
        String response = OkHttpUtils.get(url);
        Log.i(TAG, "response:->" + response);
        return JsonUtils.deserialize(response, typeOfT);
    }

    /**
     *
     * @param typeOfT
     * @param url
     * @param accessToken
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T getHandleByUrl(Type typeOfT, String url, String accessToken) throws IOException {
        Log.i(TAG, "get url ->" + url);
        String response = OkHttpUtils.get(url, accessToken);
        Log.i(TAG, "response:->" + response);
        return JsonUtils.deserialize(response, typeOfT);
    }

    /**
     * delete方法
     *
     * @param param
     * @param typeOfT
     * @param url
     * @param accessToken
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T deleteHandle(Map<String, String> param, Type typeOfT, String url, String accessToken) throws IOException {
        String jsonParam = JsonUtils.serialize(param);
        Log.i(TAG, "jsonParam->" + jsonParam);
        String response = OkHttpUtils.delete(SERVER_URL + url, jsonParam, accessToken);
        Log.i(TAG, "response:->" + response);
        return JsonUtils.deserialize(response, typeOfT);
    }
}
