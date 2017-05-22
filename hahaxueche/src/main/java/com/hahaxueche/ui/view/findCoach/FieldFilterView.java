package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangshirui on 2016/10/17.
 */

public interface FieldFilterView extends HHBaseView {
    void initMap(List<Field> fields);

    void loadCoaches(ArrayList<Coach> coaches, int[] drivingSchoolIds);

    void showMessage(String message);

    void showCoachesView();

    void hideCoachesView();
}
