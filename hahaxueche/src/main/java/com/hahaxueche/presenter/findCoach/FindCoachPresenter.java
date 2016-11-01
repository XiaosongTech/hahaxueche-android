package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.FindCoachView;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import rx.Subscription;

/**
 * Created by wangshirui on 2016/10/19.
 */

public class FindCoachPresenter implements Presenter<FindCoachView> {
    private FindCoachView mFindCoachView;
    private Subscription subscription;
    private int currentPage = 0;//0,驾校教练；1，陪练教练
    private HHBaseApplication application;

    public void attachView(FindCoachView view) {
        this.mFindCoachView = view;
        application = HHBaseApplication.get(mFindCoachView.getContext());
        //默认选中驾校教练
        selectCoach();
    }

    public void detachView() {
        this.mFindCoachView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void selectCoach() {
        mFindCoachView.selectCoach();
        mFindCoachView.unSelectPartner();
        mFindCoachView.showLeftIconMap();
        mFindCoachView.showSearchIcon(true);
        mFindCoachView.showCoachListFragment();
        currentPage = 0;
    }

    public void selectPartner() {
        mFindCoachView.selectPartner();
        mFindCoachView.unSelectCoach();
        mFindCoachView.showLeftIconExplain();
        mFindCoachView.showSearchIcon(false);
        mFindCoachView.showPartnerListFragment();
        currentPage = 1;
    }

    public void clickLeftIcon() {
        if (currentPage == 1) {
            //陪练解释点击
            HashMap<String, String> map = new HashMap();
            User user = application.getSharedPrefUtil().getUser();
            if (user != null && user.isLogin()) {
                map.put("student_id", user.student.id);
                MobclickAgent.onEvent(mFindCoachView.getContext(), "find_coach_page_what_is_personal_coach_tapped", map);
            } else {
                MobclickAgent.onEvent(mFindCoachView.getContext(), "find_coach_page_what_is_personal_coach_tapped");
            }
            mFindCoachView.showPartnerInfoDialog();
        } else {
            //训练场地图点击
            HashMap<String, String> map = new HashMap();
            User user = application.getSharedPrefUtil().getUser();
            if (user != null && user.isLogin()) {
                map.put("student_id", user.student.id);
                MobclickAgent.onEvent(mFindCoachView.getContext(), "find_coach_page_field_icon_tapped", map);
            } else {
                MobclickAgent.onEvent(mFindCoachView.getContext(), "find_coach_page_field_icon_tapped");
            }
            mFindCoachView.navigateToSelectFields();
        }
    }

    public void clickSearchCount() {
        //搜索教练点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mFindCoachView.getContext(), "find_coach_page_search_tapped", map);
        } else {
            MobclickAgent.onEvent(mFindCoachView.getContext(), "find_coach_page_search_tapped");
        }

    }

}
