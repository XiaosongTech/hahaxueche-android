package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 16/9/19.
 */
public interface MyPageView extends HHBaseView {
    void showNotLogin();

    void showLogin();

    void loadStudentInfo(Student student);

    void finishToStartLogin();

    void showMessage(String message);

    void toMyCoach(String coachId);

    void navigateToPaymentStage();

    void navigateToNoCourse();

    void navigateToMyCourse();

    void navigateToMyVoucher();

    void editUsername(String name);

    /**
     * 设置代金券未读显示
     */
    void setVoucherBadge(boolean hasBadge);

    /**
     * 设置协议未读显示
     *
     * @param hasBadge
     */
    void setContractBadge(boolean hasBadge);

    void alertToFindCoach();

    void navigateToUploadIdCard();

    void navigateToSignContract();

    void navigateToMyContract();

    void navigateToNotLoginVoucher();

    void navigateToPassEnsurance();

    void setPassEnsuranceBadge(boolean hasBadge);

    void setReferText(String text);

    void navigateToReferFriends();

    void navigateToStudentRefer();

    void navigateToMyInsurance();
}
