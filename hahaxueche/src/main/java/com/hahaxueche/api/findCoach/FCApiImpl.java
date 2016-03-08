package com.hahaxueche.api.findCoach;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.model.findCoach.CoachListResponse;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.model.findCoach.FollowResponse;
import com.hahaxueche.model.findCoach.GetReviewsResponse;
import com.hahaxueche.model.findCoach.StuPurchaseResponse;
import com.hahaxueche.model.findCoach.TrailResponse;
import com.hahaxueche.model.mySetting.PurchasedService;
import com.hahaxueche.model.util.BaseApiResponse;
import com.hahaxueche.model.util.BaseBoolean;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gibxin on 2016/2/21.
 */
public class FCApiImpl implements FCApi {
    private HttpEngine httpEngine;
    private String TAG = "FCApiImpl";

    public FCApiImpl() {
        httpEngine = HttpEngine.getInstance();
    }

    @Override
    public CoachListResponse getCoachList(String page, String per_page, String golden_coach_only, String license_type, String price,
                                          String city_id, ArrayList<String> training_field_ids, String distance, String user_location, String sort_by) {
        Type type = new TypeToken<CoachListResponse>() {
        }.getType();
        String url = FCApi.COACHES;
        if (!TextUtils.isEmpty(page)) {
            url += "?page=" + page;
        }
        if (!TextUtils.isEmpty(per_page)) {
            url += url.indexOf("?") > 0 ? ("&per_page=" + per_page) : ("?per_page=" + per_page);
        }
        if (!TextUtils.isEmpty(golden_coach_only)) {
            url += url.indexOf("?") > 0 ? ("&golden_coach_only=" + golden_coach_only) : ("?golden_coach_only=" + golden_coach_only);
        }
        if (!TextUtils.isEmpty(license_type)) {
            url += url.indexOf("?") > 0 ? ("&license_type=" + license_type) : ("?license_type=" + license_type);
        }
        if (!TextUtils.isEmpty(price)) {
            url += url.indexOf("?") > 0 ? ("&price=" + price) : ("?price=" + price);
        }
        if (!TextUtils.isEmpty(city_id)) {
            url += url.indexOf("?") > 0 ? ("&city_id=" + city_id) : ("?city_id=" + city_id);
        }
        if(training_field_ids!=null && training_field_ids.size()>0){
            for(String field_id:training_field_ids){
                url += url.indexOf("?") > 0 ? ("&training_field_ids[]=" + field_id) : ("?training_field_ids[]=" + field_id);
            }
        }
        if (!TextUtils.isEmpty(distance)) {
            url += url.indexOf("?") > 0 ? ("&distance=" + distance) : ("?distance=" + distance);
        }
        if (!TextUtils.isEmpty(user_location)) {
            url += url.indexOf("?") > 0 ? ("&user_location=" + user_location) : ("?user_location=" + user_location);
        }
        if (!TextUtils.isEmpty(sort_by)) {
            url += url.indexOf("?") > 0 ? ("&sort_by=" + sort_by) : ("?sort_by=" + sort_by);
        }
        try {
            return httpEngine.getHandle(type, url);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public CoachListResponse getCoachList(String url) {
        Type type = new TypeToken<CoachListResponse>() {
        }.getType();
        try {
            return httpEngine.getHandleByUrl(type, url);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public CoachModel getCoach(String coach_id) {
        Type type = new TypeToken<CoachModel>() {
        }.getType();
        try {
            return httpEngine.getHandle(type, FCApi.COACHES + "/" + coach_id);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public FollowResponse follow(String followee_user_id, String content, String access_token) {
        Type type = new TypeToken<FollowResponse>() {
        }.getType();
        Map<String, String> paramMap = new HashMap<String, String>();
        try {
            return httpEngine.postHandleWithX(paramMap, type, FCApi.USERS + "/" + FCApi.FOLLOWS + "/" + followee_user_id, access_token);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public BaseApiResponse cancelFollow(String followee_user_id, String access_token) {
        Type type = new TypeToken<BaseApiResponse>() {
        }.getType();
        Map<String, String> paramMap = new HashMap<String, String>();
        try {
            return httpEngine.deleteHandle(paramMap, type, FCApi.USERS + "/" + FCApi.FOLLOWS + "/" + followee_user_id, access_token);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public BaseBoolean isFollow(String followee_user_id, String access_token) {
        Type type = new TypeToken<BaseBoolean>() {
        }.getType();
        try {
            return httpEngine.getHandle(type, FCApi.USERS + "/" + FCApi.FOLLOWS + "/" + followee_user_id, access_token);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public TrailResponse createTrail(String coach_id, String name, String phone_number, String first_time_option, String second_time_option) {
        Type type = new TypeToken<TrailResponse>() {
        }.getType();
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("name", name);
        paramMap.put("phone_number", phone_number);
        paramMap.put("first_time_option", first_time_option);
        paramMap.put("second_time_option", second_time_option);
        try {
            return httpEngine.postHandle(paramMap, type, "students/" + FCApi.TRAIL + "/" + coach_id);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public GetReviewsResponse getReviewList(String coach_user_id, String page, String per_page) {
        Type type = new TypeToken<GetReviewsResponse>() {
        }.getType();
        String url = "users/reviews/" + coach_user_id;
        if (!TextUtils.isEmpty(page)) {
            url += "?page=" + page;
        }
        if (!TextUtils.isEmpty(per_page)) {
            url += url.indexOf("?") > 0 ? ("&per_page=" + per_page) : ("?per_page=" + per_page);
        }
        try {
            return httpEngine.getHandle(type, url);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public GetReviewsResponse getReviewList(String url) {
        Type type = new TypeToken<GetReviewsResponse>() {
        }.getType();
        try {
            return httpEngine.getHandleByUrl(type, url);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public StuPurchaseResponse createPurchaseStu(String coach_id, String access_token, String current_payment_stage, String service_stage, String total_amount) {
        Type type = new TypeToken<StuPurchaseResponse>() {
        }.getType();
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("current_payment_stage",current_payment_stage);
        paramMap.put("service_stage",service_stage);
        paramMap.put("total_amount",total_amount);
        try {
            return httpEngine.postHandleWithX(paramMap, type, "students/purchased_service/" + coach_id, access_token);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public String createCharge(String coach_id, String access_token) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("coach_id",coach_id);
        try {
            return httpEngine.postHandleWithXRaw(paramMap,"charges",access_token);
        }catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public PurchasedService purchasedService(String payment_stage, String access_token) {
        Type type = new TypeToken<PurchasedService>() {
        }.getType();
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("payment_stage",payment_stage);
        try {
            return httpEngine.putHandle(paramMap,type,"students/purchased_service",access_token);
        } catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }

    @Override
    public CoachModel oneKeyFindCoach(String lat, String lng) {
        Type type = new TypeToken<CoachModel>() {
        }.getType();
        String url = "students/best_match_coach?user_location[]="+lat+"&user_location[]="+lng;
        try {
            return httpEngine.getHandle(type,url);
        }catch (IOException e) {
            Log.e(TAG, "Exception e ->" + e.getMessage());
            return null;
        }
    }


}
