package com.hahaxueche.presenter.login;

import android.text.TextUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.login.LoginView;
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
public class LoginPresenter implements Presenter<LoginView> {
    private LoginView mLoginView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String mLoginType;
    private static final String LOGIN_TYPE_AUTH = "auth";
    private static final String LOGIN_TYPE_PASSWORD = "password";

    public void attachView(LoginView view) {
        this.mLoginView = view;
        application = HHBaseApplication.get(mLoginView.getContext());
        //默认验证码登陆
        mLoginType = LOGIN_TYPE_AUTH;
        mLoginView.initAuthLogin();
    }

    public void detachView() {
        this.mLoginView = null;
        if (subscription != null) subscription.unsubscribe();
        this.application = null;
    }

    public void getAuthCode(String cellPhone) {
        if (TextUtils.isEmpty(cellPhone)) {
            mLoginView.showMessage("手机号不能为空");
            return;
        }
        if (!Utils.isValidPhoneNumber(cellPhone)) {
            mLoginView.showMessage("您的手机号码格式有误");
            return;
        }
        HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", cellPhone);
        map.put("type", "login");
        if (BuildConfig.DEBUG) {
            map.put("back_door", 1);
        }
        subscription = apiService.getAuthToken(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mLoginView.disableButtons();
                        mLoginView.showProgressDialog("验证码获取中,请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mLoginView.enableButtons();
                        mLoginView.dismissProgressDialog();
                        mLoginView.showViewAfterSendingAuthCode();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isHttp404(e)) {
                            mLoginView.showMessage("手机号未注册");
                        }
                        mLoginView.enableButtons();
                        mLoginView.dismissProgressDialog();
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
        if (LOGIN_TYPE_AUTH.equals(mLoginType)) {
            mLoginType = LOGIN_TYPE_PASSWORD;
            mLoginView.initPasswordLogin();
        } else {
            mLoginType = LOGIN_TYPE_AUTH;
            mLoginView.initAuthLogin();
        }
    }

    public void login(String cellPhone, String authCode, String password) {
        if (TextUtils.isEmpty(cellPhone)) {
            mLoginView.showMessage("手机号不能为空");
            return;
        }
        if (!Utils.isValidPhoneNumber(cellPhone)) {
            mLoginView.showMessage("您的手机号码格式有误");
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", cellPhone);
        if (LOGIN_TYPE_AUTH.equals(mLoginType)) {
            if (TextUtils.isEmpty(authCode)) {
                mLoginView.showMessage("短信验证码不能为空");
                return;
            }
            map.put("auth_token", authCode);
        } else {
            if (TextUtils.isEmpty(password)) {
                mLoginView.showMessage("登录密码不能为空");
                return;
            }
            map.put("password", password);
        }
        map.put("type", "student");
        final HHBaseApplication application = HHBaseApplication.get(mLoginView.getContext());
        HHApiService apiService = application.getApiService();
        subscription = apiService.login(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mLoginView.disableButtons();
                        mLoginView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginView.enableButtons();
                        mLoginView.dismissProgressDialog();
                        if (ErrorUtil.isHttp401(e)) {
                            if (LOGIN_TYPE_AUTH.equals(mLoginType)) {
                                mLoginView.showMessage("验证码错误");
                            } else {
                                mLoginView.showMessage("密码错误");
                            }
                        } else if (ErrorUtil.isHttp404(e)) {
                            mLoginView.showMessage("手机号未注册");
                        }
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(User user) {
                        application.getSharedPrefUtil().setUser(user);
                        mLoginView.enableButtons();
                        mLoginView.dismissProgressDialog();
                        if (!user.isCompleted()) {
                            mLoginView.navigateToCompleteInfo();
                        } else {
                            mLoginView.navigateToHomepage();
                        }
                    }
                });
    }

}
