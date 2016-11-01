package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.model.user.Student;
import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 16/9/19.
 */
public interface MyPageView extends HHBaseView {
    void showNotLoginView();

    void showLoggedInView();

    void loadStudentInfo(Student student);

    void finishToStartLogin();

    void startRefresh();

    void stopRefresh();

    void showMessage(String message);

    void toMyCoach(String coachId);

    void navigateToPaymentStage();
}
