package com.hahaxueche.api.auth;

import com.hahaxueche.model.user.User;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.base.BaseApiResponse;

/**
 * Created by gibxin on 2016/1/22.
 */
public interface AuthApi {
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
     * @param refererId
     * @return
     */
    public User createUser(String phoneNum, String identifyCode, String pwd, String user_type, String refererId);

    /**
     * 完善学生资料
     *
     * @param studentId   学生id
     * @param cityId      城市id
     * @param studentName 姓名
     * @param accessToken Access Token
     * @return
     */
    public Student completeStuInfo(String studentId, String cityId, String studentName, String accessToken);

    /**
     * 登录
     *
     * @param cell_phone 手机号
     * @param pwd        auth_token或pwd
     * @param loginType  登录类型(1,验证码登录;2,密码登录)
     * @return
     */
    public User login(String cell_phone, String pwd, int loginType);

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
    public Student uploadAvatar(String access_token, String filePath, String studentId);
}
