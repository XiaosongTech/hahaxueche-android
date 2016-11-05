package com.hahaxueche.presenter.base;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.base.HHBaseView;
import com.hahaxueche.util.HHLog;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by wangshirui on 2016/11/1.
 */

public class MainPresenter implements Presenter<HHBaseView> {
    private HHBaseView mBaseView;
    private HHBaseApplication application;

    public void attachView(HHBaseView view) {
        this.mBaseView = view;
        application = HHBaseApplication.get(mBaseView.getContext());
    }

    public void detachView() {
        this.mBaseView = null;
        this.application = null;
    }

    public void viewHomepageCount(){
        //首页展现
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mBaseView.getContext(), "home_page_viewed", map);
        } else {
            MobclickAgent.onEvent(mBaseView.getContext(), "home_page_viewed");
        }
    }

    public void viewFindCoachCount(){
        //寻找教练展现
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mBaseView.getContext(), "find_coach_page_viewed", map);
        } else {
            MobclickAgent.onEvent(mBaseView.getContext(), "find_coach_page_viewed");
        }
    }

    public void viewCommunityCount(){
        //俱乐部展现
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mBaseView.getContext(), "club_page_viewed", map);
        } else {
            MobclickAgent.onEvent(mBaseView.getContext(), "club_page_viewed");
        }
    }

    public void viewMyPageCount(){
        //我的页面展现
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mBaseView.getContext(), "my_page_viewed", map);
        } else {
            MobclickAgent.onEvent(mBaseView.getContext(), "my_page_viewed");
        }
    }
}
