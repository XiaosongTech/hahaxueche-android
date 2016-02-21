package com.hahaxueche.api.findCoach;

import com.hahaxueche.model.findCoach.CoachListResponse;

/**
 * 寻找教练api
 * Created by gibxin on 2016/2/21.
 */
public interface FCApi {
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
                                          String city_id, String training_field_ids, String distance, String user_location, String sort_by);

    /**
     * 获取教练列表，直接根据反馈的links_next或者links_previous获取
     * @param url    link
     * @return
     */
    public CoachListResponse getCoachList(String url);
}
