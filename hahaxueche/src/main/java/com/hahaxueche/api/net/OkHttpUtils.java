package com.hahaxueche.api.net;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by gibxin on 2016/1/22.
 */
public class OkHttpUtils {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MULTIPART_FORM_DATA = MediaType.parse("multipart/form-data; boundary=__X_PAW_BOUNDARY__");
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

    private Response getRequest(String url, String accessToken) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        Request request;
        if (!TextUtils.isEmpty(accessToken)) {
            request = builder.addHeader("X-Access-Token", accessToken).build();
        } else {
            request = builder.build();
        }
        return mOkHttpClient.newCall(request).execute();
    }

    private Response postRequest(String url, String jsonParam, String accessToken) throws IOException {
        RequestBody body = RequestBody.create(JSON, jsonParam);
        Request.Builder builder = new Request.Builder().url(url);
        Request request;
        if (!TextUtils.isEmpty(accessToken)) {
            request = builder.addHeader("X-Access-Token", accessToken).post(body).build();
        } else {
            request = builder.post(body).build();
        }
        return mOkHttpClient.newCall(request).execute();
    }

    private Response putRequest(String url, String jsonParam, String accessToken) throws IOException {
        RequestBody body = RequestBody.create(JSON, jsonParam);
        Request.Builder builder = new Request.Builder().url(url);
        Request request;
        if (!TextUtils.isEmpty(accessToken)) {
            request = builder.addHeader("X-Access-Token", accessToken).put(body).build();
        } else {
            request = builder.put(body).build();
        }
        return mOkHttpClient.newCall(request).execute();
    }

    private Response deleteRequest(String url, String jsonParam, String accessToken) throws IOException {
        RequestBody body = RequestBody.create(JSON, jsonParam);
        Request.Builder builder = new Request.Builder().url(url);
        Request request;
        if (!TextUtils.isEmpty(accessToken)) {
            request = builder.addHeader("X-Access-Token", accessToken).delete(body).build();
        } else {
            request = builder.put(body).build();
        }
        return mOkHttpClient.newCall(request).execute();
    }

    private Response postRequestFile(String url, String access_token, String filePath) throws IOException {
        File file = new File(filePath);
        String fileName = filePath.split("/")[filePath.split("/").length - 1];
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, RequestBody.create(MULTIPART_FORM_DATA, file))
                .build();

        Request request = new Request.Builder().url(url).addHeader("X-Access-Token", access_token).post(requestBody).build();
        return mOkHttpClient.newCall(request).execute();
    }


    /**********************对外接口************************/

    /**
     * get请求
     *
     * @param url
     * @param accessToken
     * @return
     * @throws IOException
     */
    public static Response get(String url, String accessToken) throws IOException {
        Response response = getmInstance().getRequest(url, accessToken);
        return response.newBuilder().build();
    }

    /**
     * post请求
     *
     * @param url
     * @param jsonParam
     * @param accessToken
     * @return
     */
    public static Response post(String url, String jsonParam, String accessToken) throws IOException {
        return getmInstance().postRequest(url, jsonParam, accessToken);
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
    public static Response put(String url, String json, String accessToken) throws IOException {
        return getmInstance().putRequest(url, json, accessToken);
    }

    /**
     * delete
     *
     * @param url
     * @param jsonParam
     * @param accessToken
     * @return
     * @throws IOException
     */
    public static Response delete(String url, String jsonParam, String accessToken) throws IOException {
        return getmInstance().deleteRequest(url, jsonParam, accessToken);
    }

    /**
     * post文件上传
     *
     * @param url
     * @param access_token
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Response postFile(String url, String access_token, String filePath) throws IOException {
        return getmInstance().postRequestFile(url, access_token, filePath);
    }


}
