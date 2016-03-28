package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.model.findCoach.CoachListResponse;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.model.findCoach.FollowResponse;
import com.hahaxueche.model.findCoach.GetReviewsResponse;
import com.hahaxueche.model.findCoach.StuPurchaseResponse;
import com.hahaxueche.model.findCoach.TrailResponse;
import com.hahaxueche.model.mySetting.PurchasedService;
import com.hahaxueche.model.util.BaseApiResponse;
import com.hahaxueche.model.util.BaseBoolean;

import java.util.ArrayList;

/**
 * 寻找教练presenter
 * Created by gibxin on 2016/2/21.
 */
public interface FCPresenter {
    /**
     * 获取教练列表
     *
     * @param page               目前第几页
     * @param per_page           每个页面显示几个 Coach
     * @param golden_coach_only  是否只显示金牌教练
     * @param license_type       C1 还是 C2 后者都行
     * @param price              按照教练组的薪水搜索
     * @param city_id            城市的 ID
     * @param training_field_ids 数组。选特定训练场的教练组和教练
     * @param distance           距离搜索的距离
     * @param user_location      数组。[x, y] 当前位置
     * @param sort_by            排序依据
     * @param listener           回调监听器
     */
    public void getCoachList(String page, String per_page, String golden_coach_only, String license_type, String price,
                             String city_id, ArrayList<String> training_field_ids, String distance,  ArrayList<String> user_location, String sort_by,
                             FCCallbackListener<CoachListResponse> listener);

    /**
     * 根据url获取教练列表
     *
     * @param url      url
     * @param listener 回调监听器
     */
    public void getCoachList(String url, FCCallbackListener<CoachListResponse> listener);

    /**
     * 获取一个教练信息
     *
     * @param coach_id 教练id
     * @param listener 回调监听器
     */
    public void getCoach(String coach_id, FCCallbackListener<CoachModel> listener);

    /**
     * 用户A关注用户B
     *
     * @param followee_user_id 被关注的人的user id
     * @param content          关注内容
     * @param access_token     关注人的access_token
     * @param listener         回调监听器
     */
    public void follow(String followee_user_id, String content, String access_token, FCCallbackListener<FollowResponse> listener);

    /**
     * 用户A取消关注用户B
     *
     * @param followee_user_id 被关注的人的user id
     * @param access_token     关注人的access_token
     * @param listener         回调监听器
     */
    public void cancelFollow(String followee_user_id, String access_token, FCCallbackListener<BaseApiResponse> listener);

    /**
     * 用户A是否关注了用户B
     *
     * @param followee_user_id 被关注的人的user id
     * @param access_token     关注人的access_token
     * @param listener         回调监听器
     */
    public void isFollow(String followee_user_id, String access_token, FCCallbackListener<BaseBoolean> listener);

    /**
     * 创建预约教练试学
     *
     * @param coach_id
     * @param name
     * @param phone_number
     * @param first_time_option
     * @param second_time_option
     * @param listener
     */
    public void createTrail(String coach_id, String name, String phone_number, String first_time_option,
                            String second_time_option, FCCallbackListener<TrailResponse> listener);

    /**
     * 提取一个教练的评价
     *
     * @param coach_user_id
     * @param page
     * @param per_page
     * @param listener
     */
    public void getReviewList(String coach_user_id, String page, String per_page,
                              FCCallbackListener<GetReviewsResponse> listener);

    /**
     * @param url
     * @param listener
     */
    public void getReviewList(String url, FCCallbackListener<GetReviewsResponse> listener);

    public void createPurchaseStu(String coach_id, String access_token, String current_payment_stage, String service_stage,
                                  String total_amount, FCCallbackListener<StuPurchaseResponse> listener);

    /**
     * @param coach_id
     * @param access_token
     * @param listener
     */
    public void createCharge(String coach_id, String access_token, FCCallbackListener<String> listener);

    /**
     * @param payment_stage
     * @param access_token
     * @param listener
     */
    public void purchasedService(String payment_stage, String access_token, FCCallbackListener<PurchasedService> listener);

    /**
     * @param lat
     * @param lng
     * @param listener
     */
    public void oneKeyFindCoach(String lat, String lng, FCCallbackListener<CoachModel> listener);

}
