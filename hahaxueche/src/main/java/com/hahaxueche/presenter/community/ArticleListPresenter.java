package com.hahaxueche.presenter.community;

import android.os.Bundle;
import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.ArticleCategory;
import com.hahaxueche.model.responseList.ArticleResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.community.ArticleListView;
import com.hahaxueche.util.HHLog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 2016/11/3.
 */

public class ArticleListPresenter implements Presenter<ArticleListView> {
    private static final int PAGE = 1;
    private static final int PER_PAGE = 10;
    private String nextLink;
    private ArticleListView mArticleListView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Bundle mQueryBundle;

    public void attachView(ArticleListView view) {
        this.mArticleListView = view;
        application = HHBaseApplication.get(mArticleListView.getContext());
    }

    public void detachView() {
        this.mArticleListView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void fetchNews() {
        String studentId = null;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            studentId = user.student.id;
        }
        String isPopular = null;
        String category = null;
        if (mQueryBundle != null) {
            isPopular = String.valueOf(mQueryBundle.getInt("isPopular", 0));
            if (isPopular.equals("0")) {//只有非热门新闻时，才传类别参数
                category = String.valueOf(mQueryBundle.getInt("category", 0));
            }
        }
        HHApiService apiService = application.getApiService();
        subscription = apiService.getArticles(PAGE, PER_PAGE, isPopular, category, studentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ArticleResponseList>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mArticleListView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        mArticleListView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        mArticleListView.dismissProgressDialog();
                        mArticleListView.showRedBag(false);
                    }

                    @Override
                    public void onNext(ArticleResponseList articleResponseList) {
                        if (articleResponseList.data != null) {
                            mArticleListView.refreshNewsList(articleResponseList.data);
                            nextLink = articleResponseList.links.next;
                            mArticleListView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                            mArticleListView.showRedBag(true);
                        } else {
                            mArticleListView.showRedBag(false);
                        }

                    }
                });
    }

    public void loadMoreNews() {
        if (TextUtils.isEmpty(nextLink)) return;
        HHApiService apiService = application.getApiService();
        subscription = apiService.getArticles(nextLink)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ArticleResponseList>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mArticleListView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        mArticleListView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        mArticleListView.dismissProgressDialog();
                    }

                    @Override
                    public void onNext(ArticleResponseList articleResponseList) {
                        if (articleResponseList.data != null) {
                            mArticleListView.addMoreNewsList(articleResponseList.data);
                            nextLink = articleResponseList.links.next;
                            mArticleListView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }

                    }
                });
    }

    public void setQuery(Bundle bundle) {
        this.mQueryBundle = bundle;
    }

    public String getCategoryLabel() {
        String ret = "";
        ArrayList<ArticleCategory> articleCategories = application.getConstants().article_categories;
        if (articleCategories != null && articleCategories.size() > 0 && mQueryBundle != null) {
            for (ArticleCategory articleCategory : articleCategories) {
                if (articleCategory.type == mQueryBundle.getInt("category")) {
                    ret = articleCategory.name;
                    break;
                }
            }
        }
        return ret;
    }

    public void clickRedBag() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mArticleListView.getContext(), "club_page_flying_envelop_tapped", map);
        } else {
            MobclickAgent.onEvent(mArticleListView.getContext(), "club_page_flying_envelop_tapped");
        }
    }
}
