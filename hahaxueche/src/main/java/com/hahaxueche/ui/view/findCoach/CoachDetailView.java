package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 16/10/5.
 */

public interface CoachDetailView extends HHBaseView {
    void showCoachDetail(Coach coach);

    void showMessage(String message);
}
