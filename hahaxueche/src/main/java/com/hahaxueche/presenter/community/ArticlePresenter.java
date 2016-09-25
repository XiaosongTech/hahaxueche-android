package com.hahaxueche.presenter.community;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.community.ArticleView;
import com.hahaxueche.util.HHLog;

import rx.Subscription;

/**
 * Created by wangshirui on 16/9/25.
 */

public class ArticlePresenter implements Presenter<ArticleView> {
    private ArticleView mArticleView;
    private Subscription subscription;
    private HHBaseApplication application;

    public void attachView(ArticleView view) {
        this.mArticleView = view;
        application = HHBaseApplication.get(mArticleView.getContext());
    }

    public void detachView() {
        this.mArticleView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void sendComment(String comment) {
        HHLog.v("send comment -> " + comment);
    }

    public void saveDraft(String draft) {
        mArticleView.setDraft("[草稿]" + draft);
    }

    public void clearDraft(){
        mArticleView.setDraft("发表伟大言论...");
    }
}
