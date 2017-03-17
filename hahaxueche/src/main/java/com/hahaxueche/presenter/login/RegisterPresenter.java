package com.hahaxueche.presenter.login;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.login.RegisterView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;

import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/9/10.
 */
public class RegisterPresenter extends HHBasePresenter implements Presenter<RegisterView> {
    private RegisterView mView;
    private Subscription subscription;

    public void attachView(RegisterView view) {
        this.mView = view;
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
    }

    public void getAuthCode(String cellPhone, final boolean isResetPwd) {
        String phoneNumberError = validatePhoneNumber(cellPhone);
        if (!TextUtils.isEmpty(phoneNumberError)) {
            mView.showMessage(phoneNumberError);
            return;
        }
        mView.disableButtons();
        mView.showProgressDialog("验证码获取中,请稍后...");
        HHBaseApplication application = HHBaseApplication.get(mView.getContext());
        HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", cellPhone);
        if (isResetPwd) {
            map.put("type", Common.SEND_AUTH_TYPE_RESET);
        } else {
            map.put("type", Common.SEND_AUTH_TYPE_REGISTER);
        }
        subscription = apiService.getAuthToken(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {
                        mView.enableButtons();
                        mView.dismissProgressDialog();
                        mView.showViewAfterSendingAuthCode();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isResetPwd) {
                            if (ErrorUtil.isHttp404(e)) {
                                mView.showMessage("该手机号未注册");
                            }
                        } else {
                            if (ErrorUtil.isHttp422(e)) {
                                mView.showMessage("该手机号已经注册");
                            }
                        }
                        mView.enableButtons();
                        mView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseModel baseModel) {
                    }
                });
    }

    public void resetPassword(final String cellPhone, String authCode, final String password) {
        String phoneNumberError = validatePhoneNumber(cellPhone);
        if (!TextUtils.isEmpty(phoneNumberError)) {
            mView.showMessage(phoneNumberError);
            return;
        }
        if (TextUtils.isEmpty(authCode)) {
            mView.showMessage("短信验证码不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mView.showMessage("密码不能为空");
            return;
        }
        if (password.length() < 6) {
            mView.showMessage("密码长度不能少于6位");
            return;
        }
        if (password.length() > 20) {
            mView.showMessage("密码长度不能多于20位");
            return;
        }
        mView.disableButtons();
        mView.showProgressDialog();
        HHBaseApplication application = HHBaseApplication.get(mView.getContext());
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
                            mView.showMessage("验证码错误");
                        }
                        mView.enableButtons();
                        mView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseModel baseModel) {

                    }
                });
    }

    public void register(final String cellPhone, String authCode, final String password) {
        String phoneNumberError = validatePhoneNumber(cellPhone);
        if (!TextUtils.isEmpty(phoneNumberError)) {
            mView.showMessage(phoneNumberError);
            return;
        }
        if (TextUtils.isEmpty(authCode)) {
            mView.showMessage("短信验证码不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mView.showMessage("密码不能为空");
            return;
        }
        if (password.length() < 6) {
            mView.showMessage("密码长度不能少于6位");
            return;
        }
        if (password.length() > 20) {
            mView.showMessage("密码长度不能多于20位");
            return;
        }
        mView.disableButtons();
        mView.showProgressDialog();
        final HHBaseApplication application = HHBaseApplication.get(mView.getContext());
        HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", cellPhone);
        map.put("auth_token", authCode);
        map.put("password", password);
        map.put("password_confirmation", password);
        map.put("user_type", Common.USER_TYPE_STUDENT);
        map.put("source", 0);//从app注册
        subscription = apiService.createSession(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        mView.enableButtons();
                        mView.dismissProgressDialog();
                        mView.navigateToCompleteInfo();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isHttp401(e)) {
                            mView.showMessage("验证码错误");
                        }
                        mView.enableButtons();
                        mView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(User user) {
                        application.getSharedPrefUtil().setUser(user);
                    }
                });
    }

    /**
     * 密码重置后,直接登录
     *
     * @param cellPhone
     * @param password
     */
    public void autoLogin(String cellPhone, String password) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", cellPhone);
        map.put("password", password);
        map.put("type", Common.USER_TYPE_STUDENT);
        final HHBaseApplication application = HHBaseApplication.get(mView.getContext());
        HHApiService apiService = application.getApiService();
        subscription = apiService.login(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.enableButtons();
                        mView.dismissProgressDialog();
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(User user) {
                        application.getSharedPrefUtil().setUser(user);
                        if (!user.isCompleted()) {
                            mView.enableButtons();
                            mView.dismissProgressDialog();
                            mView.navigateToCompleteInfo();
                        } else {
                            mView.enableButtons();
                            mView.dismissProgressDialog();
                            mView.navigateToHomepage();
                        }
                    }
                });
    }
}
