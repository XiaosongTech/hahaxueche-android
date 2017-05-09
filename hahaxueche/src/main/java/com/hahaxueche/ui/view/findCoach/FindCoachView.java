package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2016/10/19.
 */

public interface FindCoachView extends HHBaseView {
    void selectCoach();

    void unSelectCoach();

    void selectDrivingSchool();

    void unSelectDrivingSchool();

    void showCoachListFragment();

    void showDrivingSchoolListFragment();

    void navigateToSelectFields();
}
