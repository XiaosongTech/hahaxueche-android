package com.hahaxueche.ui.view.homepage;

import com.hahaxueche.model.cluster.FieldItem;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.List;

/**
 * Created by wangshirui on 2017/5/16.
 */

public interface MapSearchView extends HHBaseView {
    void loadFields(List<FieldItem> fields);

    void loadCoaches(List<Coach> coaches);

    void showMessage(String message);

    void showCoachesView();

    void hideCoachesView();
}
