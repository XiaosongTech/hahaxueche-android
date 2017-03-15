package com.hahaxueche.presenter.community;

import android.os.Bundle;
import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.ArticleCategory;
import com.hahaxueche.model.responseList.ArticleResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.community.ArticleListView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.WebViewUrl;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 2016/11/3.
 */

public class ArticleListPresenter extends HHBasePresenter implements Presenter<ArticleListView> {
    private String nextLink;
    private ArticleListView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Bundle mQueryBundle;

    public void attachView(ArticleListView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
    }

    public void detachView() {
        this.mView = null;
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
        subscription = apiService.getArticles(Common.START_PAGE, Common.PER_PAGE, isPopular, category, studentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ArticleResponseList>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        mView.dismissProgressDialog();
                        mView.showRedBag(false);
                    }

                    @Override
                    public void onNext(ArticleResponseList articleResponseList) {
                        if (articleResponseList.data != null) {
                            mView.refreshNewsList(articleResponseList.data);
                            nextLink = articleResponseList.links.next;
                            mView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                            mView.showRedBag(true);
                        } else {
                            mView.showRedBag(false);
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
                        mView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onNext(ArticleResponseList articleResponseList) {
                        if (articleResponseList.data != null) {
                            mView.addMoreNewsList(articleResponseList.data);
                            nextLink = articleResponseList.links.next;
                            mView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
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
            MobclickAgent.onEvent(mView.getContext(), "club_page_flying_envelop_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "club_page_flying_envelop_tapped");
        }
        mView.openWebView(WebViewUrl.WEB_URL_DALIBAO);
    }
}
