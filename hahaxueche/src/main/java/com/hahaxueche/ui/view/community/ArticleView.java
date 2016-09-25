package com.hahaxueche.ui.view.community;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 16/9/25.
 */

public interface ArticleView extends HHBaseView {
    void showCommentDialog();

    void setDraft(String draft);
}
