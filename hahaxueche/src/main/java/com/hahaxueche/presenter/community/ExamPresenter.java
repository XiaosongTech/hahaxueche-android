package com.hahaxueche.presenter.community;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class ExamPresenter implements Presenter<HHBaseView> {
    private HHBaseView mBaseView;
    private HHBaseApplication application;

    public void attachView(HHBaseView view) {
        this.mBaseView = view;
        application = HHBaseApplication.get(mBaseView.getContext());
    }

    public void detachView() {
        this.mBaseView = null;
        application = null;
    }

    public int getBonus() {
        int cityId = 0;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.student != null) {
            cityId = user.student.city_id;
        }
        return application.getConstants().getCity(cityId).referer_bonus;
    }
}
