package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangshirui on 16/10/4.
 */

public interface SearchCoachView extends HHBaseView {
    void setRightSearch();

    void setRightCancel();

    void loadSearchHistory(LinkedList searchHistoryList);

    void loadCoachList(ArrayList<Coach> coachList);

    void showMessage(String message);

    void disableButton();

    void enableButton();

    void setSearch(String text);

    void loadDrivingSchoolList(List<DrivingSchool> drivingSchools);
}
