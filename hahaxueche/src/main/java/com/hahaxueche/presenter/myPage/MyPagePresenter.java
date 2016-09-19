package com.hahaxueche.presenter.myPage;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyPageView;

import rx.Subscription;

/**
 * Created by wangshirui on 16/9/19.
 */
public class MyPagePresenter implements Presenter<MyPageView> {
    private MyPageView mMyPageView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Student mStudent;

    public void attachView(MyPageView view) {
        this.mMyPageView = view;
        application = HHBaseApplication.get(mMyPageView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user.isLogin()) {
            mMyPageView.showLoggedInView();
            mStudent = user.student;
            mMyPageView.loadStudentInfo(mStudent);
        } else {
            mMyPageView.showNotLoginView();
        }
    }

    public void detachView() {
        this.mMyPageView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mStudent = null;
    }
}
