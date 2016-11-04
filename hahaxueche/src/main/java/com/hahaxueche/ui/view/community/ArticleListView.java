package com.hahaxueche.ui.view.community;

import com.hahaxueche.model.community.Article;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/11/3.
 */

public interface ArticleListView extends HHBaseView {
    void setPullLoadEnable(boolean enable);

    /**
     * 刷新新闻列表
     *
     * @param articleArrayList
     */
    void refreshNewsList(ArrayList<Article> articleArrayList);

    /**
     * 加载更多新闻
     *
     * @param articleArrayList
     */
    void addMoreNewsList(ArrayList<Article> articleArrayList);
}
