package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.List;

/**
 * Created by wangshirui on 2017/5/3.
 */

public interface DrivingSchoolListView extends HHBaseView {
    void setPullLoadEnable(boolean enable);

    void refreshDrivingSchoolList(List<DrivingSchool> drivingSchoolList);

    void addMoreDrivingSchoolList(List<DrivingSchool> drivingSchoolList);

    void showMessage(String message);

    void showHelp(boolean isShow);
}
