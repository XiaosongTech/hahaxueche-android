package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.model.user.Referrer;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/11/2.
 */

public interface ReferrerListView extends HHBaseView {
    void setPullLoadEnable(boolean enable);

    void refreshReferrerList(ArrayList<Referrer> referrerArrayList);

    void addMoreReferrerList(ArrayList<Referrer> referrerArrayList);
}
