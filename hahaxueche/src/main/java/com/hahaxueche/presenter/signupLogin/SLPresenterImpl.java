package com.hahaxueche.presenter.signupLogin;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hahaxueche.api.auth.AuthApi;
import com.hahaxueche.api.auth.AuthApiImpl;
import com.hahaxueche.api.util.ApiError;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.base.BaseApiResponse;
import com.hahaxueche.presenter.util.ErrorEvent;


/**
 * 注册登录Presenter的实现���
 * Created by gibxin on 2016/1/19.
 */
public class SLPresenterImpl implements SLPresenter {
    private Context context;
    private AuthApi authApi;

    public SLPresenterImpl(Context context) {
        this.context = context;
        this.authApi = new AuthApiImpl();
    }

    @Override
    public void getIdentifyCode(final String phoneNum, final String type, final SLCallbackListener<BaseApiResponse> listener) {
        //参数检查
        if (!isValidPhoneNumber(phoneNum, listener)) {
            return;
        }
        new AsyncTask<Void, Void, BaseApiResponse>() {
            @Override
            protected BaseApiResponse doInBackground(Void... voids) {
                return authApi.sendAuthToken(phoneNum, type);
            }

            @Override
            protected void onPostExecute(BaseApiResponse baseApiResponse) {
                if (listener != null) {
                    if (baseApiResponse != null) {
                        if (baseApiResponse.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            if (baseApiResponse.getCode().equals("40022")) {
                                baseApiResponse.setMessage("该手机号已被注册，请尝试登录");
                                listener.onFailure(baseApiResponse.getCode(), baseApiResponse.getMessage());
                            } else {
                                listener.onFailure(baseApiResponse.getCode(), baseApiResponse.getMessage());
                            }

                        }
                    } else {
                        listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                    }
                }
            }
        }.execute();
    }

    @Override
    public void createUser(final String phoneNum, final String identifyCode, final String pwd, final String type,
                           final SLCallbackListener<User> listener) {
        //参数检查
        if (!isValidIdentifyCode(identifyCode, listener)) {
            return;
        }
        if (!isValidPwd(pwd, listener)) {
            return;
        }
        new AsyncTask<Void, Void, User>() {

            @Override
            protected User doInBackground(Void... params) {
                return authApi.createUser(phoneNum, identifyCode, pwd, type);
            }

            @Override
            protected void onPostExecute(User user) {
                if (listener != null) {
                    if (user != null) {
                        if (user.isSuccess()) {
                            listener.onSuccess(user);
                        } else {
                            if (user.getCode().equals("40011")) {
                                user.setMessage("您的短信验证码有误");
                                listener.onFailure(user.getCode(), user.getMessage());
                            } else {
                                listener.onFailure(user.getCode(), user.getMessage());
                            }
                        }
                    } else {
                        listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                    }
                }
            }
        }.execute();

    }

    @Override
    public void completeStuInfo(final String studentId, final String cityId, final String studentName, final String accessToken,
                                final String photoPath, final SLCallbackListener<Student> listener) {
        if (!isValidUserName(studentName, listener)) {
            return;
        }
        if (!isValidCity(cityId, listener)) {
            return;
        }
        if (TextUtils.isEmpty(photoPath)) {
            listener.onFailure("000", "亲，请上传头像哦~");
            return;
        }
        new AsyncTask<Void, Void, Student>() {

            @Override
            protected Student doInBackground(Void... params) {

                Student compStuResponse = authApi.completeStuInfo(studentId, cityId, studentName, accessToken);
                if (compStuResponse.isSuccess()) {
                    return authApi.uploadAvatar(accessToken, photoPath, studentId);
                } else {
                    return new Student();
                }
            }

            @Override
            protected void onPostExecute(Student compStuResponse) {
                if (listener != null) {
                    if (compStuResponse != null) {
                        if (compStuResponse.isSuccess()) {
                            listener.onSuccess(compStuResponse);
                        } else {
                            listener.onFailure(compStuResponse.getCode(), compStuResponse.getMessage());
                        }
                    } else {
                        listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                    }
                }
            }
        }.execute();
    }

    @Override
    public void login(final String cell_phone, final String pwd, final int loginType, final SLCallbackListener<User> listener) {
        if (TextUtils.isEmpty(cell_phone)) {
            if (listener != null) {
                listener.onFailure(ErrorEvent.PARAM_NULL, "您的手机号码不能为空");
            }
            return;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        boolean isValidPhoneNumber = false;
        try {
            Phonenumber.PhoneNumber chNumberProto = phoneUtil.parse(cell_phone, "CN");
            isValidPhoneNumber = phoneUtil.isValidNumber(chNumberProto);
        } catch (NumberParseException e) {
            listener.onFailure(ErrorEvent.PARAM_ILLEGAL, "您的手机号码格式有误");
        }
        if (!isValidPhoneNumber) {
            listener.onFailure(ErrorEvent.PARAM_ILLEGAL, "您的手机号码格式有误");
        }
        if (TextUtils.isEmpty(pwd)) {
            if (listener != null) {
                listener.onFailure(ErrorEvent.PARAM_NULL, loginType == 1 ? "您的验证码不能为空" : "您的密码不能为空");
            }
            return;
        }
        new AsyncTask<Void, Void, User>() {

            @Override
            protected User doInBackground(Void... params) {
                return authApi.login(cell_phone, pwd, loginType);
            }

            @Override
            protected void onPostExecute(User user) {
                if (listener != null) {
                    if (user != null) {
                        if (user.isSuccess()) {
                            listener.onSuccess(user);
                        } else {
                            if (user.getCode().equals("40011")) {
                                listener.onFailure(user.getCode(), loginType == 1 ? "您的短信验证码有误" : "您的密码有误");
                            } else if (user.getCode().equals("40001")) {
                                listener.onFailure(user.getCode(), loginType == 1 ? "您的短信验证码有误" : "您的密码有误");
                            } else {
                                listener.onFailure(user.getCode(), user.getMessage());
                            }
                        }
                    } else {
                        listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                    }
                }
            }
        }.execute();

    }

    @Override
    public void resetPassword(final String cell_phone, final String password, final String auth_token, final SLCallbackListener<BaseApiResponse> listener) {
        if (TextUtils.isEmpty(password)) {
            if (listener != null) {
                listener.onFailure(ErrorEvent.PARAM_NULL, "您的密码为空");
            }
            return;
        }
        if (password.length() < 6 || password.length() > 20) {
            if (listener != null) {
                listener.onFailure(ErrorEvent.PARAM_ILLEGAL, "您的密码格式有误");
            }
            return;
        }
        new AsyncTask<Void, Void, BaseApiResponse>() {

            @Override
            protected BaseApiResponse doInBackground(Void... params) {
                return authApi.resetPassword(cell_phone, password, auth_token);
            }

            @Override
            protected void onPostExecute(BaseApiResponse baseApiResponse) {
                if (listener != null) {
                    if (baseApiResponse != null) {
                        if (baseApiResponse.isSuccess()) {
                            listener.onSuccess(baseApiResponse);
                        } else {
                            if (baseApiResponse.getCode().equals("40001")) {
                                baseApiResponse.setMessage("您的短信验证码有误");
                                listener.onFailure(baseApiResponse.getCode(), baseApiResponse.getMessage());
                            } else {
                                listener.onFailure(baseApiResponse.getCode(), baseApiResponse.getMessage());
                            }
                        }
                    } else {
                        listener.onFailure(ApiError.TIME_OUT_EVENT, ApiError.TIME_OUT_EVENT_MSG);
                    }
                }
            }
        }.execute();

    }


    /**
     * 校验手机号有效性
     *
     * @param phoneNumber
     * @param listener
     * @return
     */
    private boolean isValidPhoneNumber(String phoneNumber, SLCallbackListener<BaseApiResponse> listener) {
        if (TextUtils.isEmpty(phoneNumber)) {
            if (listener != null) {
                listener.onFailure(ErrorEvent.PARAM_NULL, "您的手机号码不能为空");
            }
            return false;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber chNumberProto = phoneUtil.parse(phoneNumber, "CN");
            if(!phoneUtil.isValidNumber(chNumberProto)){
                listener.onFailure(ErrorEvent.PARAM_ILLEGAL, "您的手机号码格式有误");
                return false;
            }
        } catch (NumberParseException e) {
            listener.onFailure(ErrorEvent.PARAM_ILLEGAL, "您的手机号码格式有误");
            return false;
        }
        return true;
    }

    /**
     * 校验验证码格式有效性
     *
     * @param identifyCode
     * @param listener
     * @return
     */
    private boolean isValidIdentifyCode(String identifyCode, SLCallbackListener<User> listener) {
        if (TextUtils.isEmpty(identifyCode)) {
            if (listener != null) {
                listener.onFailure(ErrorEvent.PARAM_NULL, "您的短信验证码不能为空");
            }
            return false;
        }
        return true;
    }

    /**
     * 校验密码有效性
     *
     * @param pwd
     * @return
     */
    private boolean isValidPwd(String pwd, SLCallbackListener<User> listener) {
        if (TextUtils.isEmpty(pwd)) {
            if (listener != null) {
                listener.onFailure(ErrorEvent.PARAM_NULL, "您的密码为空");
            }
            return false;
        }
        if (pwd.length() < 6 || pwd.length() > 20) {
            if (listener != null) {
                listener.onFailure(ErrorEvent.PARAM_ILLEGAL, "您的密码格式有误");
            }
            return false;
        }
        return true;
    }

    /**
     * 姓名有效性校验
     *
     * @param userName
     * @param listener
     * @return
     */
    private boolean isValidUserName(String userName, SLCallbackListener<Student> listener) {
        if (TextUtils.isEmpty(userName)) {
            if (listener != null) {
                listener.onFailure(ErrorEvent.PARAM_NULL, "您的姓名不能为空");
            }
            return false;
        }
        return true;
    }

    /**
     * 所在地校验
     *
     * @param cityId
     * @param listener
     * @return
     */
    private boolean isValidCity(String cityId, SLCallbackListener<Student> listener) {
        if (TextUtils.isEmpty(cityId)) {
            if (listener != null) {
                listener.onFailure(ErrorEvent.PARAM_NULL, "您的所在地不能为空");
            }
            return false;
        }
        return true;
    }

}
