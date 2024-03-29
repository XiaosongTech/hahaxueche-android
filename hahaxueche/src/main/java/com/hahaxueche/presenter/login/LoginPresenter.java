package com.hahaxueche.presenter.login;

import android.text.TextUtils;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.login.LoginView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;

import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/9/9.
 */
public class LoginPresenter extends HHBasePresenter implements Presenter<LoginView> {
    private LoginView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String mLoginType;

    public void attachView(LoginView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        //默认验证码登陆
        mLoginType = Common.LOGIN_TYPE_AUTH;
        mView.initAuthLogin();
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        this.application = null;
    }

    public void getAuthCode(String cellPhone) {
        String phoneNumberError = validatePhoneNumber(cellPhone);
        if (!TextUtils.isEmpty(phoneNumberError)) {
            mView.showMessage(phoneNumberError);
            return;
        }
        HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", cellPhone);
        map.put("type", Common.SEND_AUTH_TYPE_LOGIN);
        if (BuildConfig.DEBUG) {
            //后门参数，验证码是手机号后六位
            map.put("back_door", 1);
        }
        subscription = apiService.getAuthToken(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mView.disableButtons();
                        mView.showProgressDialog("验证码获取中,请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mView.enableButtons();
                        mView.dismissProgressDialog();
                        mView.showViewAfterSendingAuthCode();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isHttp404(e)) {
                            mView.showMessage("手机号未注册");
                        }
                        mView.enableButtons();
                        mView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(BaseModel baseModel) {
                    }
                });
    }

    /**
     * 切换登陆方式
     */
    public void changeLoginType() {
        if (Common.LOGIN_TYPE_AUTH.equals(mLoginType)) {
            mLoginType = Common.LOGIN_TYPE_PASSWORD;
            mView.initPasswordLogin();
        } else {
            mLoginType = Common.LOGIN_TYPE_AUTH;
            mView.initAuthLogin();
        }
    }

    public void login(String cellPhone, String authCode, String password) {
        String phoneNumberError = validatePhoneNumber(cellPhone);
        if (!TextUtils.isEmpty(phoneNumberError)) {
            mView.showMessage(phoneNumberError);
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", cellPhone);
        if (Common.LOGIN_TYPE_AUTH.equals(mLoginType)) {
            if (TextUtils.isEmpty(authCode)) {
                mView.showMessage("短信验证码不能为空");
                return;
            }
            map.put("auth_token", authCode);
        } else {
            if (TextUtils.isEmpty(password)) {
                mView.showMessage("登录密码不能为空");
                return;
            }
            map.put("password", password);
        }
        map.put("type", Common.USER_TYPE_STUDENT);
        final HHBaseApplication application = HHBaseApplication.get(mView.getContext());
        HHApiService apiService = application.getApiService();
        subscription = apiService.login(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mView.disableButtons();
                        mView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.enableButtons();
                        mView.dismissProgressDialog();
                        if (ErrorUtil.isHttp401(e)) {
                            if (Common.LOGIN_TYPE_AUTH.equals(mLoginType)) {
                                mView.showMessage("验证码错误");
                            } else {
                                mView.showMessage("密码错误");
                            }
                        } else if (ErrorUtil.isHttp404(e)) {
                            mView.showMessage("手机号未注册");
                        }
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(User user) {
                        application.getSharedPrefUtil().setUser(user);
                        mView.enableButtons();
                        mView.dismissProgressDialog();
                        if (!user.isCompleted()) {
                            mView.navigateToCompleteInfo();
                        } else {
                            mView.navigateToHomepage();
                        }
                    }
                });
    }

}
