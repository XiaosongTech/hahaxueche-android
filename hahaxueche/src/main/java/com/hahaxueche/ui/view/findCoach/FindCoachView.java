package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2016/10/19.
 */

public interface FindCoachView extends HHBaseView {
    void selectCoach();

    void unSelectCoach();

    void selectPartner();

    void unSelectPartner();

    void showLeftIconMap();

    void showLeftIconExplain();

    void showCoachListFragment();

    void showPartnerListFragment();
}
