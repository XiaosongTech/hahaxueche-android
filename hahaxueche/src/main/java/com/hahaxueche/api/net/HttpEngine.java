package com.hahaxueche.api.net;

import android.util.Log;

import com.hahaxueche.utils.JsonUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.Response;

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
    private static boolean isDebug = true;
    private boolean isShowLog = true;
    public final static String BASE_SERVER_IP = isDebug ? "http://staging-api.hahaxueche.net" : "http://api.hahaxueche.net";
    private final static String SERVER_URL = BASE_SERVER_IP + "/api/v1/";
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
     * get
     *
     * @param url
     * @param accessToken
     * @return
     * @throws IOException
     */
    public Response getHandle(String url, String accessToken) throws IOException {
        if (isShowLog) {
            Log.i(TAG, "get url ->" + SERVER_URL + url);
            Log.i(TAG, "accessToken->" + accessToken);
        }
        Response response = OkHttpUtils.get(SERVER_URL + url, accessToken);
        return response;
    }

    /**
     * @param url
     * @param accessToken
     * @return
     * @throws IOException
     */
    public Response getHandleByUrl(String url, String accessToken) throws IOException {
        if (isShowLog) {
            Log.i(TAG, "get url ->" + url);
            Log.i(TAG, "accessToken->" + accessToken);
        }
        Response response = OkHttpUtils.get(url, accessToken);
        return response;
    }

    /**
     * post
     *
     * @param param
     * @param url
     * @param accessToken
     * @return
     * @throws IOException
     */
    public Response postHandle(Map<String, Object> param, String url, String accessToken) throws IOException {
        String jsonParam = JsonUtils.serialize(param);
        if (isShowLog) {
            Log.i(TAG, "post url ->" + SERVER_URL + url);
            Log.i(TAG, "jsonParam->" + jsonParam);
            Log.i(TAG, "accessToken->" + accessToken);
        }
        Response response = OkHttpUtils.post(SERVER_URL + url, jsonParam, accessToken);
        return response;
    }

    /**
     * delete方法
     *
     * @param param
     * @param url
     * @param accessToken
     * @return
     * @throws IOException
     */
    public Response deleteHandle(Map<String, Object> param, String url, String accessToken) throws IOException {
        String jsonParam = JsonUtils.serialize(param);
        if (isShowLog) {
            Log.i(TAG, "delete url ->" + SERVER_URL + url);
            Log.i(TAG, "jsonParam->" + jsonParam);
            Log.i(TAG, "accessToken->" + accessToken);
        }
        Response response = OkHttpUtils.delete(SERVER_URL + url, jsonParam, accessToken);
        return response;
    }

    /**
     * put方法
     *
     * @param param
     * @param url
     * @param accessToken
     * @return
     * @throws IOException
     */
    public Response putHandle(Map<String, Object> param, String url, String accessToken) throws IOException {
        String jsonParam = JsonUtils.serialize(param);
        if (isShowLog) {
            Log.i(TAG, "put url ->" + SERVER_URL + url);
            Log.i(TAG, "jsonParam->" + jsonParam);
            Log.i(TAG, "accessToken->" + accessToken);
        }
        Response response = OkHttpUtils.put(SERVER_URL + url, jsonParam, accessToken);
        return response;
    }

    /**
     * post文件上传方法
     *
     * @param url
     * @param accessToken
     * @param filePath
     * @return
     * @throws IOException
     */
    public Response postFileHandle(String url, String accessToken, String filePath) throws IOException {
        if (isShowLog) {
            Log.i(TAG, "file upload request:->" + url);
            Log.i(TAG, "filePath :->" + filePath);
            Log.i(TAG, "accessToken->" + accessToken);
        }
        Response response = OkHttpUtils.postFile(SERVER_URL + url, accessToken, filePath);
        return response;
    }
}
