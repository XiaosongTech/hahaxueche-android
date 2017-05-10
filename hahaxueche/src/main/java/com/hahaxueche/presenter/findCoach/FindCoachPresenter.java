package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.FindCoachView;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import rx.Subscription;

/**
 * Created by wangshirui on 2016/10/19.
 */

public class FindCoachPresenter extends HHBasePresenter implements Presenter<FindCoachView> {
    private FindCoachView mView;
    private Subscription subscription;
    private int currentPage = 0;//0,选驾校；1，挑教练
    private HHBaseApplication application;

    public void attachView(FindCoachView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        //默认选驾校
        selectDrivingSchool();
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void selectCoach() {
        mView.selectCoach();
        mView.unSelectDrivingSchool();
        mView.showCoachListFragment();
        currentPage = 0;
    }

    public void selectDrivingSchool() {
        mView.selectDrivingSchool();
        mView.unSelectCoach();
        mView.showDrivingSchoolListFragment();
        currentPage = 1;
    }

    public void clickLeftIcon() {
        //训练场地图点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "find_coach_page_field_icon_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "find_coach_page_field_icon_tapped");
        }
        mView.navigateToSelectFields();
    }

    public void clickSearchCount() {
        //搜索教练点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "find_coach_page_search_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "find_coach_page_search_tapped");
        }
    }

}
