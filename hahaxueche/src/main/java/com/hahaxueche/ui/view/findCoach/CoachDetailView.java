package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.responseList.ReviewResponseList;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.view.base.HHBaseView;

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

    void navigateToPurchaseCoach(Coach coach);

    void initShareData(Coach coach);

    void addC1Label(int pos);

    void addC2Label(int pos);

    void addPrice(int pos, boolean isVIP, int price);

    /**
     * 打开webview
     *
     * @param url
     */
    void openWebView(String url);

    void alertToLogin(String alertMessage);

    void setCoachBadge(boolean isGolden);

    void setPayBadge(boolean isCashPledge);
}
