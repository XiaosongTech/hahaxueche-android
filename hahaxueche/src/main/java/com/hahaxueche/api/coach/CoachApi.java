package com.hahaxueche.api.coach;

import com.hahaxueche.model.response.CoachListResponse;
import com.hahaxueche.model.coach.Coach;
import com.hahaxueche.model.response.FollowResponse;
import com.hahaxueche.model.response.GetReviewsResponse;
import com.hahaxueche.model.response.StuPurchaseResponse;
import com.hahaxueche.model.response.TrailResponse;
import com.hahaxueche.model.student.PurchasedService;
import com.hahaxueche.model.base.BaseApiResponse;
import com.hahaxueche.model.base.BaseBoolean;

import java.util.ArrayList;

/**
 * 寻找教练api
 * Created by gibxin on 2016/2/21.
 */
public interface CoachApi {
    public static final String COACHES = "coaches";

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
     */
    public CoachListResponse getCoachList(String page, String per_page, String golden_coach_only, String license_type, String price,
                                          String city_id, ArrayList<String> training_field_ids, String distance, ArrayList<String> user_location, String sort_by);

    /**
     * 获取教练列表，直接根据反馈的links_next或者links_previous获取
     *
     * @param url link
     * @return
     */
    public CoachListResponse getCoachList(String url);

    /**
     * 获取一个教练信息
     *
     * @param coach_id 教练id
     * @return
     */
    public Coach getCoach(String coach_id);

    /**
     * 用户A关注用户B
     *
     * @param followee_user_id 被关注的人的user id
     * @param content          关注内容
     * @param access_token     关注人的access_token
     * @return
     */
    public FollowResponse follow(String followee_user_id, String content, String access_token);

    /**
     * 用户A取消关注用户B
     *
     * @param followee_user_id 被关注的人的user id
     * @param access_token     关注人的access_token
     * @return
     */
    public BaseApiResponse cancelFollow(String followee_user_id, String access_token);

    /**
     * 用户A是否关注了用户B
     *
     * @param followee_user_id 被关注的人的user id
     * @param access_token     关注人的access_token
     * @return
     */
    public BaseBoolean isFollow(String followee_user_id, String access_token);

    /**
     * 创建预约教练试学
     *
     * @param coach_id
     * @param name
     * @param phone_number
     * @param first_time_option
     * @param second_time_option
     * @return
     */
    public TrailResponse createTrail(String coach_id, String name, String phone_number, String first_time_option, String second_time_option);

    /**
     * 提取一个教练的评价
     *
     * @param coach_user_id
     * @param page
     * @param per_page
     * @return
     */
    public GetReviewsResponse getReviewList(String coach_user_id, String page, String per_page);

    /**
     * @param url
     * @return
     */
    public GetReviewsResponse getReviewList(String url);

    /**
     * 学生创建教练和学员的Purchased Service
     *
     * @param coach_id
     * @param access_token
     * @param current_payment_stage
     * @param service_stage
     * @param total_amount
     * @return
     */
    public StuPurchaseResponse createPurchaseStu(String coach_id, String access_token, String current_payment_stage,
                                                 String service_stage, String total_amount);

    /**
     * @param coach_id
     * @param access_token
     * @return
     */
    public String createCharge(String coach_id, String access_token);

    /**
     * @param payment_stage
     * @param access_token
     * @return
     */
    public PurchasedService purchasedService(String payment_stage, String access_token);

    /**
     * @param lat
     * @param lng
     * @return
     */
    public Coach oneKeyFindCoach(String lat, String lng);

    /**
     * @param keyword
     * @return
     */
    public ArrayList<Coach> searchCoach(String keyword);
}
