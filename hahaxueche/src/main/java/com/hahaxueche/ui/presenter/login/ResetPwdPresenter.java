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
import com.hahaxueche.ui.view.login.ResetPwdView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;

import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/9/10.
 */
public class ResetPwdPresenter implements Presenter<ResetPwdView> {
    private ResetPwdView mResetPwdView;
    private Subscription subscription;
    private static final String SENT_AUTH_TYPE = "reset";
    private static final String LOGIN_TYPE_STUDENT = "student";

    public void attachView(ResetPwdView view) {
        this.mResetPwdView = view;
    }

    public void detachView() {
        this.mResetPwdView = null;
        if (subscription != null) subscription.unsubscribe();
    }

    public void getAuthCode(String cellPhone) {
        if (TextUtils.isEmpty(cellPhone)) {
            mResetPwdView.showMessage("手机号不能为空");
            return;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber chNumberProto = phoneUtil.parse(cellPhone, "CN");
            if (!phoneUtil.isValidNumber(chNumberProto)) {
                mResetPwdView.showMessage("您的手机号码格式有误");
                return;
            }
        } catch (NumberParseException e) {
            mResetPwdView.showMessage("您的手机号码格式有误");
            return;
        }
        mResetPwdView.disableButtons();
        mResetPwdView.showProgressDialog("验证码获取中,请稍后...");
        HHBaseApplication application = HHBaseApplication.get(mResetPwdView.getContext());
        HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", cellPhone);
        map.put("type", SENT_AUTH_TYPE);
        subscription = apiService.getAuthToken(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {
                        mResetPwdView.enableButtons();
                        mResetPwdView.dismissProgressDialog();
                        mResetPwdView.showViewAfterSendingAuthCode();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mResetPwdView.enableButtons();
                        mResetPwdView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseModel baseModel) {
                    }
                });
    }

    public void resetPassword(final String cellPhone, String authCode, final String password) {
        if (TextUtils.isEmpty(cellPhone)) {
            mResetPwdView.showMessage("手机号不能为空");
            return;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber chNumberProto = phoneUtil.parse(cellPhone, "CN");
            if (!phoneUtil.isValidNumber(chNumberProto)) {
                mResetPwdView.showMessage("您的手机号码格式有误");
                return;
            }
        } catch (NumberParseException e) {
            mResetPwdView.showMessage("您的手机号码格式有误");
            return;
        }
        if (TextUtils.isEmpty(authCode)) {
            mResetPwdView.showMessage("短信验证码不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mResetPwdView.showMessage("密码不能为空");
            return;
        }
        if (password.length() < 6) {
            mResetPwdView.showMessage("密码长度不能少于6位");
            return;
        }
        if (password.length() > 20) {
            mResetPwdView.showMessage("密码长度不能多于20位");
            return;
        }
        mResetPwdView.disableButtons();
        mResetPwdView.showProgressDialog("密码重置中,请稍后...");
        HHBaseApplication application = HHBaseApplication.get(mResetPwdView.getContext());
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
                            mResetPwdView.showMessage("验证码错误");
                        }
                        mResetPwdView.enableButtons();
                        mResetPwdView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseModel baseModel) {

                    }
                });
    }

    public void autoLogin(String cellPhone, String password) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", cellPhone);
        map.put("password", password);
        map.put("type", LOGIN_TYPE_STUDENT);
        final HHBaseApplication application = HHBaseApplication.get(mResetPwdView.getContext());
        HHApiService apiService = application.getApiService();
        subscription = apiService.login(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        mResetPwdView.enableButtons();
                        mResetPwdView.dismissProgressDialog();
                        mResetPwdView.showMessage("登录成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mResetPwdView.enableButtons();
                        mResetPwdView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(User user) {
                        application.getSharedPrefUtil().setUser(user);
                    }
                });
    }
}
