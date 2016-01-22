package com.hahaxueche.presenter.signupLogin;

/**
 * 注册登录Presenter
 * Created by gibxin on 2016/1/19.
 */
public interface SLPresenter {
    /**
     * 发送验证码
     *
     * @param phoneNum 手机号
     * @param listener 回调监听器
     */
    public void getIdentifyCode(String phoneNum, SLCallbackListener<Void> listener);

}
