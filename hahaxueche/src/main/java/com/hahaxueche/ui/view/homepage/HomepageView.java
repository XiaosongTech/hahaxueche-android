package com.hahaxueche.ui.view.homepage;

import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangshirui on 16/9/17.
 */
public interface HomepageView extends HHBaseView {
    /**
     * 显示信息
     *
     * @param message
     */
    void showMessage(String message);

    /**
     * 显示城市选择对话框
     */
    void showCityChoseDialog();

    void navigateToReferFriends();

    void navigateToExamLibrary();

    void navigateToStudentRefer();

    void navigateToMyInsurance();

    void loadHotDrivingSchools(List<DrivingSchool> drivingSchoolList);

    void loadNearCoaches(ArrayList<Coach> data);

    void setCityName(String cityName);

    void readyToLoadViews();

    void onCityChange();
}
