package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2016/10/26.
 */

public interface MyCoachDetailView extends HHBaseView {
    void showCoachDetail(Coach coach);

    void showMessage(String message);

    void enableFollow(boolean enable);

    void showFollow(boolean isFollow);

    void enableApplaud(boolean enable);

    void showApplaud(boolean isApplaud);

    void setApplaudCount(int count);

    void startApplaudAnimation();
}
