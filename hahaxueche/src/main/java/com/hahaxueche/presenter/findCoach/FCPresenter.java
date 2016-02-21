package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.model.findCoach.CoachListResponse;

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
                             String city_id, String training_field_ids, String distance, String user_location, String sort_by,
                             FCCallbackListener<CoachListResponse> listener);

    /**
     * 根据url获取教练列表
     * @param url
     * @param listener           回调监听器
     */
    public void getCoachList(String url,FCCallbackListener<CoachListResponse> listener);
}
