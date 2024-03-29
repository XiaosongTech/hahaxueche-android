package com.hahaxueche.presenter.base;

import android.os.Bundle;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.base.SplashView;
import com.hahaxueche.util.HHLog;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/9/18.
 */
public class SplashPresenter extends HHBasePresenter implements Presenter<SplashView> {
    private SplashView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Bundle mShareObject = null;

    public void attachView(SplashView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
    }

    public void detachView() {
        this.mView = null;
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Constants>() {
                    @Override
                    public void onCompleted() {
                        doLogin();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                        mView.showError("服务器连接异常,请稍后再试~");
                    }

                    @Override
                    public void onNext(Constants constants) {
                        application.setConstants(constants);
                    }
                });

    }

    /**
     * 登陆
     */
    private void doLogin() {
        if (application.getConstants() == null)
            return;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            //已记录用户信息，自动登陆
            doAutoLogin(user);
        } else {
            //游客登陆，进入主页
            application.getSharedPrefUtil().createFakeUser();
            mView.navigateToHomepage(mShareObject);
        }
    }

    /**
     * 自动登陆
     */
    private void doAutoLogin(User user) {
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
                            HHBaseApplication application = HHBaseApplication.get(mView.getContext());
                            application.getSharedPrefUtil().createFakeUser();
                            mView.navigateToHomepage(mShareObject);
                        } else {
                            mView.navigateToStartLogin();
                        }
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Student student) {
                        application.getSharedPrefUtil().updateStudent(student);
                        if (!student.isCompleted()) {
                            //信息不完善，需要先补充信息
                            mView.navigateToCompleteInfo();
                        } else {
                            if (mShareObject == null) {
                                mShareObject = new Bundle();
                            }
                            mShareObject.putBoolean("isLogin", true);
                            mView.navigateToHomepage(mShareObject);
                        }
                    }
                });
    }

    public void setShareObject(Bundle shareObject) {
        this.mShareObject = shareObject;
    }
}
