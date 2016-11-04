package com.hahaxueche.presenter.community;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.base.ArticleCategory;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.community.CommunityView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Subscription;

/**
 * Created by wangshirui on 16/9/22.
 */

public class CommunityPresenter implements Presenter<CommunityView> {
    private CommunityView mCommunityView;
    private Subscription subscription;
    private HHBaseApplication application;

    public void attachView(CommunityView view) {
        this.mCommunityView = view;
        application = HHBaseApplication.get(mCommunityView.getContext());
    }

    public void detachView() {
        this.mCommunityView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public ArrayList<ArticleCategory> getArticleCaterories() {
        return application.getConstants().article_categories;
    }

    public void clickGroupBuyCount() {
        //团购点击
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            HashMap<String, String> map = new HashMap();
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mCommunityView.getContext(), "club_page_group_purchase_tapped", map);
        } else {
            MobclickAgent.onEvent(mCommunityView.getContext(), "club_page_group_purchase_tapped");
        }

    }

    public void clickTestLibCount() {
        //在线题库点击
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            HashMap<String, String> map = new HashMap();
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mCommunityView.getContext(), "club_page_online_test_tapped", map);
        } else {
            MobclickAgent.onEvent(mCommunityView.getContext(), "club_page_online_test_tapped");
        }

    }
}
