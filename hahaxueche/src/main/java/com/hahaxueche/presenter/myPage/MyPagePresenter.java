package com.hahaxueche.presenter.myPage;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyPageView;
import com.hahaxueche.util.HHLog;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

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

    public void fetchStudent() {
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mMyPageView.startRefresh();
            HHApiService apiService = application.getApiService();
            subscription = apiService.getStudent(user.student.id, user.session.access_token)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<Student>() {
                        @Override
                        public void onCompleted() {
                            mMyPageView.stopRefresh();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mMyPageView.stopRefresh();
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Student student) {
                            application.getSharedPrefUtil().updateStudent(student);
                            mMyPageView.loadStudentInfo(student);
                        }
                    });
        }

    }

    public void logOut() {
        HHApiService apiService = application.getApiService();
        String sessionId = application.getSharedPrefUtil().getUser().session.id;
        String accessToken = application.getSharedPrefUtil().getUser().session.access_token;
        apiService.logOut(sessionId, accessToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {
                        application.getSharedPrefUtil().setUser(null);//清空用户
                        mMyPageView.finishToStartLogin();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseModel baseModel) {

                    }
                });
    }
}
