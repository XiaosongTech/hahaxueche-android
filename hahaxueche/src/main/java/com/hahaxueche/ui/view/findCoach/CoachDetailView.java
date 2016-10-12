package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.responseList.ReviewResponseList;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.coach.ProductType;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/10/5.
 */

public interface CoachDetailView extends HHBaseView {
    void showCoachDetail(Coach coach);

    void showMessage(String message);

    void showNoReview(String coachName);

    void showReviews(ReviewResponseList responseList);

    void enableFollow(boolean enable);

    void showFollow(boolean isFollow);

    void enableApplaud(boolean enable);

    void showApplaud(boolean isApplaud);

    void setApplaudCount(int count);

    void startApplaudAnimation();

    void addPrices(ArrayList<ProductType> productTypes);

    void navigateToPurchaseCoach(Coach coach);
}
