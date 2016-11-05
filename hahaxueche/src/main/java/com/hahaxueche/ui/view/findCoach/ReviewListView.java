package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.user.coach.Review;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/10/8.
 */

public interface ReviewListView extends HHBaseView {
    void setPullLoadEnable(boolean enable);

    /**
     * 刷新评论列表
     *
     * @param reviewArrayList
     */
    void refreshReviewList(ArrayList<Review> reviewArrayList);

    /**
     * 加载更多评论
     *
     * @param reviewArrayList
     */
    void addMoreReviewList(ArrayList<Review> reviewArrayList);

    void setAverageRating(float score);

    void setReviewedCount(String countText);
}
