package com.hahaxueche.presenter.base;

import android.os.Bundle;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.base.WelcomeView;
import com.hahaxueche.util.HHLog;

import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/9/18.
 */
public class WelcomePresenter implements Presenter<WelcomeView> {
    private WelcomeView mWelcomeView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Bundle mShareObject = null;

    public void attachView(WelcomeView view) {
        this.mWelcomeView = view;
        application = HHBaseApplication.get(mWelcomeView.getContext());
    }

    public void detachView() {
        this.mWelcomeView = null;
        if (subscription != null) subscription.unsubscribe();
        this.application = null;
    }

    /**
     * 启动app
     */
    public void startApplication() {
        //获取常量信息
        HHApiService apiService = application.getApiService();
        subscription = apiService.getConstants()
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Constants>() {
                    @Override
                    public void onCompleted() {
                        doAutoLogin();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        mWelcomeView.showError("服务器连接异常,请稍后再试~");
                    }

                    @Override
                    public void onNext(Constants constants) {
                        application.setConstants(constants);
                    }
                });

    }

    public void doAutoLogin() {
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            HHApiService apiService = application.getApiService();
            subscription = apiService.getStudent(user.student.id, user.session.access_token)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<Student>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mShareObject != null) {
                                HHBaseApplication application = HHBaseApplication.get(mWelcomeView.getContext());
                                application.getSharedPrefUtil().createFakeUser();
                                mWelcomeView.navigateToHomepage(mShareObject);
                            } else {
                                mWelcomeView.navigationToStartLogin();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Student student) {
                            application.getSharedPrefUtil().updateStudent(student);
                            if (!student.isCompleted()) {
                                mWelcomeView.navigateToCompleteInfo();
                            } else {
                                mWelcomeView.navigateToHomepage(mShareObject);
                            }
                        }
                    });
        } else {
            if (mShareObject != null) {
                HHBaseApplication application = HHBaseApplication.get(mWelcomeView.getContext());
                application.getSharedPrefUtil().createFakeUser();
                mWelcomeView.navigateToHomepage(mShareObject);
            } else {
                mWelcomeView.navigationToStartLogin();
            }
        }
    }

    public void setShareObject(Bundle shareObject) {
        this.mShareObject = shareObject;
    }
}
