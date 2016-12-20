package com.hahaxueche.ui.view.community;

import com.hahaxueche.model.community.Article;
import com.hahaxueche.model.community.Comment;
import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 16/9/25.
 */

public interface ArticleView extends HHBaseView {
    void showCommentDialog();

    void setDraft(String draft);

    void setViewCount(String count);

    void setCommentCount(String count);

    void setWebViewUrl(String url);

    void removeCommentViews();

    void addCommentTitle();

    void addComment(Comment comment, boolean isLast);

    void addMoreCommentButton();

    void enableApplaud(boolean enable);

    void showApplaud(boolean isApplaud);

    void setApplaudCount(String count);

    void startApplaudAnimation();

    void showMessage(String message);

    void initShareData(Article article, String shortenUrl);

    void alertToLogin(String alertMessage);
}
