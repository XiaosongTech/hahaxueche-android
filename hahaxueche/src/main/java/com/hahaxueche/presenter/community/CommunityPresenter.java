package com.hahaxueche.presenter.community;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.ArticleCategory;
import com.hahaxueche.model.community.Article;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.community.CommunityView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.WebViewUrl;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/9/22.
 */

public class CommunityPresenter extends HHBasePresenter implements Presenter<CommunityView> {
    private CommunityView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Article mHeadlineArticle;

    public void attachView(CommunityView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        getHeadline();
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mHeadlineArticle = null;
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
            MobclickAgent.onEvent(mView.getContext(), "club_page_group_purchase_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "club_page_group_purchase_tapped");
        }
        mView.openWebView(WebViewUrl.WEB_URL_GROUP_BUY);
    }

    public void clickTestLibCount() {
        //在线题库点击
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            HashMap<String, String> map = new HashMap();
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mView.getContext(), "club_page_online_test_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "club_page_online_test_tapped");
        }

    }

    public void getHeadline() {
        String studentId = null;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            studentId = user.student.id;
        }
        HHApiService apiService = application.getApiService();
        subscription = apiService.getHeadline(studentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Article>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Article article) {
                        mHeadlineArticle = article;
                        mView.setHeadline(mHeadlineArticle);
                    }
                });
    }

    public void setHeadlineArticle(Article headline) {
        mHeadlineArticle = headline;
    }

    public Article getHeadlineArticle() {
        return mHeadlineArticle;
    }
}
