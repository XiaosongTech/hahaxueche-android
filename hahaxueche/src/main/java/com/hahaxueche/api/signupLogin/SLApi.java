package com.hahaxueche.api.signupLogin;

import com.hahaxueche.model.signupLogin.AvaterResponse;
import com.hahaxueche.model.signupLogin.CompStuResponse;
import com.hahaxueche.model.signupLogin.CreateUserResponse;
import com.hahaxueche.model.util.BaseApiResponse;

/**
 * Created by gibxin on 2016/1/22.
 */
public interface SLApi {
    public static final String SEND_AUTH_TOKEN = "send_auth_token";
    public static final String CREATE_USER = "users";
    public static final String CONSTANTS = "constants";
    public static final String STUDENTS = "students";
    public static final String SESSIONS = "sessions";
    public static final String RESET_PASSWORD = "users/reset_password";
    public static final String AVATAR = "avatar";

    /**
     * 发送验证码
     *
     * @param phoneNum  手机号
     * @param send_type 类型（register,login）
     * @return
     */
    public BaseApiResponse sendAuthToken(String phoneNum, String send_type);

    /**
     * 创建注册用户
     *
     * @param phoneNum     手机号
     * @param identifyCode 短信验证码
     * @param pwd          密码
     * @param user_type    类型(coach,student)
     * @return
     */
    public CreateUserResponse createUser(String phoneNum, String identifyCode, String pwd, String user_type);

    /**
     * 完善学生资料
     *
     * @param studentId   学生id
     * @param cityId      城市id
     * @param studentName 姓名
     * @param accessToken Access Token
     * @return
     */
    public CompStuResponse completeStuInfo(String studentId, String cityId, String studentName, String accessToken);

    /**
     * 登录
     *
     * @param cell_phone 手机号
     * @param pwd        auth_token或pwd
     * @param loginType  登录类型(1,验证码登录;2,密码登录)
     * @return
     */
    public CreateUserResponse login(String cell_phone, String pwd, int loginType);

    /**
     * 重置密码
     *
     * @param cell_phone
     * @param password
     * @param auth_token
     * @return
     */
    public BaseApiResponse resetPassword(String cell_phone, String password, String auth_token);

    /**
     * 头像上传
     *
     * @param access_token
     * @param filePath
     * @param studentId
     * @return
     */
    public CompStuResponse uploadAvatar(String access_token, String filePath, String studentId);
}
