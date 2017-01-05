package com.hahaxueche.presenter.community;

import android.text.TextUtils;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.base.ShortenUrl;
import com.hahaxueche.model.community.Article;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.community.ArticleView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
import com.umeng.analytics.MobclickAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 16/9/25.
 */

public class ArticlePresenter implements Presenter<ArticleView> {
    private ArticleView mArticleView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Article mArticle;
    private boolean isApplaud;

    public void attachView(ArticleView view) {
        this.mArticleView = view;
        application = HHBaseApplication.get(mArticleView.getContext());
    }

    public void detachView() {
        this.mArticleView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void sendComment(final String comment) {
        final HHApiService apiService = application.getApiService();
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null && !user.isLogin()) return;
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<Article>>() {
                    @Override
                    public Observable<Article> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.commentArticle(mArticle.id, user.student.id, comment, user.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Article>() {
                    @Override
                    public void onCompleted() {
                        loadCount();
                        loadComments();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isInvalidSession(e)) {
                            mArticleView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Article article) {
                        mArticle = article;
                    }
                });
    }

    public void saveDraft(String draft) {
        mArticleView.setDraft("[草稿]" + draft);
    }

    public void clearDraft() {
        mArticleView.setDraft("发表伟大言论...");
    }

    public void setArticle(Article article) {
        this.mArticle = article;
        String articleUrl = BuildConfig.MOBILE_URL + "/articles/" + mArticle.id + "?view=raw";
        HHLog.v(articleUrl);
        mArticleView.setWebViewUrl(articleUrl);
        shortenUrl(mArticle);
        pageStartCount();
        loadCount();
    }

    public void shortenUrl(final Article article) {
        String url = BuildConfig.MOBILE_URL + "/articles/" + article.id;
        String longUrl = null;
        HHApiService apiService = application.getApiService();
        try {
            longUrl = " https://api.t.sina.com.cn/short_url/shorten.json?source=4186780524&url_long=" +
                    URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(longUrl)) return;
        subscription = apiService.shortenUrl(longUrl)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ArrayList<ShortenUrl>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<ShortenUrl> shortenUrls) {
                        if (shortenUrls != null && shortenUrls.size() > 0) {
                            mArticleView.initShareData(article, shortenUrls.get(0).url_short);
                        }
                    }
                });
    }

    public void setArticle(final String articleId) {
        HHApiService apiService = application.getApiService();
        subscription = apiService.getArticle(articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Article>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Article article) {
                        setArticle(article);
                    }
                });
    }

    public Article getArticle() {
        return mArticle;
    }

    public void loadCount() {
        int commentCount = 0;
        if (mArticle.comments != null) {
            commentCount = mArticle.comments.size();
        }
        mArticleView.setCommentCount(Utils.getCount(commentCount));
        mArticleView.setViewCount(Utils.getCount(mArticle.view_count));
        loadApplaud();
    }

    public void loadComments() {
        mArticleView.removeCommentViews();
        if (mArticle.comments != null && mArticle.comments.size() > 0) {
            mArticleView.addCommentTitle();
            boolean loadMoreComments = mArticle.comments.size() > 3;
            int showCommentCount = loadMoreComments ? 3 : mArticle.comments.size();
            for (int i = 0; i < showCommentCount; i++) {
                mArticleView.addComment(mArticle.comments.get(i), i == showCommentCount - 1);
            }
            if (loadMoreComments) {//显示更多评论按钮
                mArticleView.addMoreCommentButton();
            }
        }
    }

    public void clickComment() {
        //评论点击
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        map.put("article_id", mArticle.id);
        MobclickAgent.onEvent(mArticleView.getContext(), "article_detail_page_comment_tapped", map);
        if (user != null && user.isLogin()) {
            mArticleView.showCommentDialog();
        } else {
            mArticleView.alertToLogin("注册登录后,才可以评价文章哦～\n注册获得更多学车咨询!～");
        }
    }

    private void loadApplaud() {
        isApplaud = (mArticle.liked == 1);
        HHLog.v("isApplaud -> " + isApplaud);
        mArticleView.showApplaud(isApplaud);
        mArticleView.setApplaudCount(Utils.getCount(mArticle.like_count));
    }

    public void applaud() {
        //like unlike 点击
        final User user = application.getSharedPrefUtil().getUser();
        HashMap<String, String> countMap = new HashMap();
        if (user != null && user.isLogin()) {
            countMap.put("student_id", user.student.id);
        }
        countMap.put("article_id", mArticle.id);
        countMap.put("like", isApplaud ? "0" : "1");
        MobclickAgent.onEvent(mArticleView.getContext(), "article_detail_page_like_unlike_tapped", countMap);
        if (user == null || !user.isLogin()) {
            mArticleView.alertToLogin("注册登录后,才可以点赞文章哦～\n注册获得更多学车咨询!～");
            return;
        }
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        mArticleView.enableApplaud(false);
        if (isApplaud) {
            subscription = apiService.isValidToken(user.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<Article>>() {
                        @Override
                        public Observable<Article> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                return apiService.likeArticle(user.student.id, mArticle.id, 0, user.session.access_token);
                            } else {
                                return application.getSessionObservable();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<Article>() {
                        @Override
                        public void onCompleted() {
                            loadApplaud();
                            mArticleView.enableApplaud(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mArticleView.enableApplaud(true);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mArticleView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Article article) {
                            mArticle = article;
                        }
                    });
        } else {
            subscription = apiService.isValidToken(user.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<Article>>() {
                        @Override
                        public Observable<Article> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                return apiService.likeArticle(user.student.id, mArticle.id, 1, user.session.access_token);
                            } else {
                                return application.getSessionObservable();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<Article>() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            mArticleView.startApplaudAnimation();
                        }

                        @Override
                        public void onCompleted() {
                            loadApplaud();
                            mArticleView.enableApplaud(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mArticleView.enableApplaud(true);
                            if (ErrorUtil.isInvalidSession(e)) {
                                mArticleView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Article article) {
                            mArticle = article;
                        }
                    });
        }
    }

    public void clickShareCount() {
        //分享点击
        User user = application.getSharedPrefUtil().getUser();
        HashMap<String, String> map = new HashMap();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        map.put("article_id", mArticle.id);
        MobclickAgent.onEvent(mArticleView.getContext(), "article_detail_page_share_article_tapped", map);
    }

    public void clickShareSuccessCount(String shareChannel) {
        //分享成功
        User user = application.getSharedPrefUtil().getUser();
        HashMap<String, String> map = new HashMap();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        map.put("article_id", mArticle.id);
        map.put("share_channel", shareChannel);
        MobclickAgent.onEvent(mArticleView.getContext(), "article_detail_page_share_article_succeed", map);
    }

    public void pageStartCount() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        map.put("article_id", mArticle.id);
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mArticleView.getContext(), "article_detail_page_viewed", map);
    }

    public void clickCommentCount() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        map.put("article_id", mArticle.id);
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mArticleView.getContext(), "article_detail_page_view_comment_tapped", map);
    }
}
