package com.hahaxueche.presenter.signupLogin;

import com.hahaxueche.model.signupLogin.CompStuResponse;
import com.hahaxueche.model.signupLogin.CreateUserResponse;

/**
 * 注册登录Presenter
 * Created by gibxin on 2016/1/19.
 */
public interface SLPresenter {
    /**
     * 发送验证码
     *
     * @param phoneNum 手机号
     * @param type     类型(register,login)
     * @param listener 回调监听器
     */
    public void getIdentifyCode(String phoneNum, String type, SLCallbackListener<Void> listener);

    /**
     * @param phoneNum           手机号
     * @param identifyCode       验证码
     * @param pwd                密码
     * @param type               类型
     * @param slCallbackListener 回调监听器
     */
    public void createUser(String phoneNum, String identifyCode, String pwd, String type, SLCallbackListener<CreateUserResponse> slCallbackListener);

    /**
     * 完善学生资料
     *
     * @param studentId   学生Id
     * @param cityId      城市Id
     * @param studentName 姓名
     * @param accessToken Access Token
     */
    public void completeStuInfo(String studentId, String cityId, String studentName, String accessToken, SLCallbackListener<CompStuResponse> slCallbackListener);

    /**
     * 登录
     *
     * @param cell_phone         手机号
     * @param pwd                auth_token或者pwd
     * @param loginType          登录类型(1,验证码登录;2,密码登录)
     * @param slCallbackListener 回调监听器
     */
    public void login(String cell_phone, String pwd, int loginType, SLCallbackListener<CreateUserResponse> slCallbackListener);

}
