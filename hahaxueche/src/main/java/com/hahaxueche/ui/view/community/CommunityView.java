package com.hahaxueche.ui.view.community;

import com.hahaxueche.model.community.Article;
import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 16/9/22.
 */

public interface CommunityView extends HHBaseView {

    void setHeadline(Article article);
}
