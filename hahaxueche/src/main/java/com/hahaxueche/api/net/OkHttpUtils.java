package com.hahaxueche.api.net;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by gibxin on 2016/1/22.
 */
public class OkHttpUtils {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;

    private OkHttpUtils() {
        mOkHttpClient = new OkHttpClient();
    }

    private synchronized static OkHttpUtils getmInstance() {
        if (mInstance == null) {
            mInstance = new OkHttpUtils();
        }
        return mInstance;
    }

    private String getRequest(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = mOkHttpClient.newCall(request).execute();
        return response.body().string();
    }

    private String postRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = mOkHttpClient.newCall(request).execute();
        return response.body().string();
    }

    private String putRequest(String url, String json, String access_token) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).addHeader("X-Access-Token", access_token).put(body).build();
        Response response = mOkHttpClient.newCall(request).execute();
        return response.body().string();
    }

    /**********************对外接口************************/

    /**
     * get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        return getmInstance().getRequest(url);
    }

    /**
     * post请求
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public static String post(String url, String json) throws IOException {
        return getmInstance().postRequest(url, json);
    }

    /**
     * put请求
     *
     * @param url
     * @param json
     * @param accessToken
     * @return
     * @throws IOException
     */
    public static String put(String url, String json, String accessToken) throws IOException {
        return getmInstance().putRequest(url, json, accessToken);
    }
}
