package com.hahaxueche.ui.presenter.login;

import android.text.TextUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.ui.api.HHApiService;
import com.hahaxueche.ui.model.base.BaseModel;
import com.hahaxueche.ui.model.user.User;
import com.hahaxueche.ui.presenter.Presenter;
import com.hahaxueche.ui.view.login.LoginView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;

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
    private static final String SENT_AUTH_TYPE = "login";
    private static final String LOGIN_TYPE_STUDENT = "student";
    public static final int AUTH_LOGIN = 1;
    public static final int PASSWORD_LOGIN = 2;

    public void attachView(LoginView view) {
        this.mLoginView = view;
    }

    public void detachView() {
        this.mLoginView = null;
        if (subscription != null) subscription.unsubscribe();
    }

    public void getAuthCode(String cellPhone) {
        if (TextUtils.isEmpty(cellPhone)) {
            mLoginView.showMessage("手机号不能为空");
            return;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber chNumberProto = phoneUtil.parse(cellPhone, "CN");
            if (!phoneUtil.isValidNumber(chNumberProto)) {
                mLoginView.showMessage("您的手机号码格式有误");
                return;
            }
        } catch (NumberParseException e) {
            mLoginView.showMessage("您的手机号码格式有误");
            return;
        }
        mLoginView.disableButtons();
        mLoginView.showProgressDialog("验证码获取中,请稍后...");
        HHBaseApplication application = HHBaseApplication.get(mLoginView.getContext());
        HHApiService apiService = application.getApiService();
        subscription = apiService.getAuthToken(cellPhone, SENT_AUTH_TYPE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {
                        mLoginView.showViewAfterSendingAuthCode();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseModel baseModel) {
                        mLoginView.enableButtons();
                        mLoginView.dismissProgressDialog();
                    }
                });
    }

    public void changeLoginType(int loginType) {
        if (loginType == AUTH_LOGIN) {
            mLoginView.initPasswordLogin();
        } else {
            mLoginView.initAuthLogin();
        }
    }

    public void login(String cellPhone, String authCode, String password, final int loginType) {
        if (TextUtils.isEmpty(cellPhone)) {
            mLoginView.showMessage("手机号不能为空");
            return;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber chNumberProto = phoneUtil.parse(cellPhone, "CN");
            if (!phoneUtil.isValidNumber(chNumberProto)) {
                mLoginView.showMessage("您的手机号码格式有误");
                return;
            }
        } catch (NumberParseException e) {
            mLoginView.showMessage("您的手机号码格式有误");
            return;
        }
        HashMap<String,Object> map = new HashMap<>();
        map.put("cell_phone",cellPhone);
        if (loginType == AUTH_LOGIN) {
            if (TextUtils.isEmpty(authCode)) {
                mLoginView.showMessage("短信验证码不能为空");
                return;
            }
            map.put("auth_token",authCode);
        } else {
            if (TextUtils.isEmpty(password)) {
                mLoginView.showMessage("登录密码不能为空");
                return;
            }
            map.put("password",password);
        }
        map.put("type",LOGIN_TYPE_STUDENT);
        mLoginView.disableButtons();
        mLoginView.showProgressDialog("登录中,请稍后...");
        HHBaseApplication application = HHBaseApplication.get(mLoginView.getContext());
        HHApiService apiService = application.getApiService();
        subscription = apiService.login(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        mLoginView.enableButtons();
                        mLoginView.dismissProgressDialog();
                        mLoginView.showMessage("登录成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginView.enableButtons();
                        mLoginView.dismissProgressDialog();
                        if (ErrorUtil.isHttp401(e)) {
                            if (loginType == AUTH_LOGIN) {
                                mLoginView.showMessage("验证码错误");
                            }else {
                                mLoginView.showMessage("密码错误");
                            }
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(User user) {

                    }
                });
    }

}
