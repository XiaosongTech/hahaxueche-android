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
import com.hahaxueche.ui.view.login.RegisterView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;

import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/9/10.
 */
public class RegisterPresenter implements Presenter<RegisterView> {
    private RegisterView mRegisterView;
    private Subscription subscription;
    private static final String SEND_AUTH_TYPE_RESET = "reset";
    private static final String SEND_AUTH_TYPE_REGISTER = "register";
    private static final String TYPE_STUDENT = "student";

    public void attachView(RegisterView view) {
        this.mRegisterView = view;
    }

    public void detachView() {
        this.mRegisterView = null;
        if (subscription != null) subscription.unsubscribe();
    }

    public void getAuthCode(String cellPhone, boolean isResetPwd) {
        if (TextUtils.isEmpty(cellPhone)) {
            mRegisterView.showMessage("手机号不能为空");
            return;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber chNumberProto = phoneUtil.parse(cellPhone, "CN");
            if (!phoneUtil.isValidNumber(chNumberProto)) {
                mRegisterView.showMessage("您的手机号码格式有误");
                return;
            }
        } catch (NumberParseException e) {
            mRegisterView.showMessage("您的手机号码格式有误");
            return;
        }
        mRegisterView.disableButtons();
        mRegisterView.showProgressDialog("验证码获取中,请稍后...");
        HHBaseApplication application = HHBaseApplication.get(mRegisterView.getContext());
        HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", cellPhone);
        if (isResetPwd) {
            map.put("type", SEND_AUTH_TYPE_RESET);
        } else {
            map.put("type", SEND_AUTH_TYPE_REGISTER);
        }
        subscription = apiService.getAuthToken(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {
                        mRegisterView.enableButtons();
                        mRegisterView.dismissProgressDialog();
                        mRegisterView.showViewAfterSendingAuthCode();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isHttp422(e)) {
                            mRegisterView.showMessage("该手机号已经注册");
                        }
                        mRegisterView.enableButtons();
                        mRegisterView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseModel baseModel) {
                    }
                });
    }

    public void resetPassword(final String cellPhone, String authCode, final String password) {
        if (TextUtils.isEmpty(cellPhone)) {
            mRegisterView.showMessage("手机号不能为空");
            return;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber chNumberProto = phoneUtil.parse(cellPhone, "CN");
            if (!phoneUtil.isValidNumber(chNumberProto)) {
                mRegisterView.showMessage("您的手机号码格式有误");
                return;
            }
        } catch (NumberParseException e) {
            mRegisterView.showMessage("您的手机号码格式有误");
            return;
        }
        if (TextUtils.isEmpty(authCode)) {
            mRegisterView.showMessage("短信验证码不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mRegisterView.showMessage("密码不能为空");
            return;
        }
        if (password.length() < 6) {
            mRegisterView.showMessage("密码长度不能少于6位");
            return;
        }
        if (password.length() > 20) {
            mRegisterView.showMessage("密码长度不能多于20位");
            return;
        }
        mRegisterView.disableButtons();
        mRegisterView.showProgressDialog("密码重置中,请稍后...");
        HHBaseApplication application = HHBaseApplication.get(mRegisterView.getContext());
        HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", cellPhone);
        map.put("auth_token", authCode);
        map.put("password", password);
        map.put("password_confirmation", password);
        subscription = apiService.resetPassword(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {
                        autoLogin(cellPhone, password);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isHttp401(e)) {
                            mRegisterView.showMessage("验证码错误");
                        }
                        mRegisterView.enableButtons();
                        mRegisterView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseModel baseModel) {

                    }
                });
    }

    public void register(final String cellPhone, String authCode, final String password) {
        if (TextUtils.isEmpty(cellPhone)) {
            mRegisterView.showMessage("手机号不能为空");
            return;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber chNumberProto = phoneUtil.parse(cellPhone, "CN");
            if (!phoneUtil.isValidNumber(chNumberProto)) {
                mRegisterView.showMessage("您的手机号码格式有误");
                return;
            }
        } catch (NumberParseException e) {
            mRegisterView.showMessage("您的手机号码格式有误");
            return;
        }
        if (TextUtils.isEmpty(authCode)) {
            mRegisterView.showMessage("短信验证码不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mRegisterView.showMessage("密码不能为空");
            return;
        }
        if (password.length() < 6) {
            mRegisterView.showMessage("密码长度不能少于6位");
            return;
        }
        if (password.length() > 20) {
            mRegisterView.showMessage("密码长度不能多于20位");
            return;
        }
        mRegisterView.disableButtons();
        mRegisterView.showProgressDialog("注册中,请稍后...");
        final HHBaseApplication application = HHBaseApplication.get(mRegisterView.getContext());
        HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", cellPhone);
        map.put("auth_token", authCode);
        map.put("password", password);
        map.put("password_confirmation", password);
        map.put("user_type", TYPE_STUDENT);
        subscription = apiService.createSession(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        mRegisterView.enableButtons();
                        mRegisterView.dismissProgressDialog();
                        mRegisterView.navigateToCompleteInfo();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isHttp401(e)) {
                            mRegisterView.showMessage("验证码错误");
                        }
                        mRegisterView.enableButtons();
                        mRegisterView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(User user) {
                        application.getSharedPrefUtil().setUser(user);
                    }
                });
    }

    public void autoLogin(String cellPhone, String password) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", cellPhone);
        map.put("password", password);
        map.put("type", TYPE_STUDENT);
        final HHBaseApplication application = HHBaseApplication.get(mRegisterView.getContext());
        HHApiService apiService = application.getApiService();
        subscription = apiService.login(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        mRegisterView.enableButtons();
                        mRegisterView.dismissProgressDialog();
                        mRegisterView.showMessage("登录成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRegisterView.enableButtons();
                        mRegisterView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(User user) {
                        application.getSharedPrefUtil().setUser(user);
                    }
                });
    }
}
