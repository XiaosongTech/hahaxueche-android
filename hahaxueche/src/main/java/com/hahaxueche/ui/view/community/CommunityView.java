package com.hahaxueche.ui.view.community;

import com.hahaxueche.model.community.News;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/9/22.
 */

public interface CommunityView extends HHBaseView {
    void setPullLoadEnable(boolean enable);

    /**
     * 刷新新闻列表
     *
     * @param newsArrayList
     */
    void refreshNewsList(ArrayList<News> newsArrayList);

    /**
     * 加载更多新闻
     *
     * @param newsArrayList
     */
    void addMoreNewsList(ArrayList<News> newsArrayList);
}
