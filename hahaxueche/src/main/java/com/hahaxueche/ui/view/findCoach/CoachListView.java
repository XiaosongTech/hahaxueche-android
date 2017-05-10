package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/10/1.
 */

public interface CoachListView extends HHBaseView {
    void setPullLoadEnable(boolean enable);

    /**
     * 刷新教练列表
     *
     * @param coachArrayList
     */
    void refreshCoachList(ArrayList<Coach> coachArrayList);

    /**
     * 加载更多教练
     *
     * @param coachArrayList
     */
    void addMoreCoachList(ArrayList<Coach> coachArrayList);

    void showMessage(String message);

    void showHelp(boolean isShow);
}
