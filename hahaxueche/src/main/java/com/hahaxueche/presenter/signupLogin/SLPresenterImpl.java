package com.hahaxueche.presenter.signupLogin;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.hahaxueche.api.signupLogin.SLApi;
import com.hahaxueche.api.signupLogin.SLApiImpl;
import com.hahaxueche.api.util.ApiError;
import com.hahaxueche.model.signupLogin.CompStuResponse;
import com.hahaxueche.model.signupLogin.CreateUserResponse;
import com.hahaxueche.model.util.BaseApiResponse;
import com.hahaxueche.presenter.util.ErrorEvent;


/**
 * 注册登录Presenter的实现���
 * Created by gibxin on 2016/1/19.
 */
public class SLPresenterImpl implements SLPresenter {
    private Context context;
    private SLApi api;

    public SLPresenterImpl(Context context) {
        this.context = context;
        this.api = new SLApiImpl();
    }

    @Override
    public void getIdentifyCode(final String phoneNum, final String type, final SLCallbackListener<Void> listener) {
        //参数检查
        if (!isValidPhoneNumber(phoneNum, listener)) {
            return;
        }
        new AsyncTask<Void, Void, BaseApiResponse>() {
            @Override
            protected BaseApiResponse doInBackground(Void... voids) {
                return api.sendAuthToken(phoneNum, type);
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
                           final SLCallbackListener<CreateUserResponse> listener) {
        //参数检查
        if (!isValidIdentifyCode(identifyCode, listener)) {
            return;
        }
        if (!isValidPwd(pwd, listener)) {
            return;
        }
        new AsyncTask<Void, Void, CreateUserResponse>() {

            @Override
            protected CreateUserResponse doInBackground(Void... params) {
                return api.createUser(phoneNum, identifyCode, pwd, type);
            }

            @Override
            protected void onPostExecute(CreateUserResponse createUserResponse) {
                if (listener != null) {
                    if (createUserResponse != null) {
                        if (createUserResponse.isSuccess()) {
                            listener.onSuccess(createUserResponse);
                        } else {
                            if (createUserResponse.getCode().equals("40011")) {
                                createUserResponse.setMessage("您的短信验证码有误");
                                listener.onFailure(createUserResponse.getCode(), createUserResponse.getMessage());
                            } else {
                                listener.onFailure(createUserResponse.getCode(), createUserResponse.getMessage());
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
                                final SLCallbackListener<CompStuResponse> listener) {
        if (!isValidUserName(studentName, listener)) {
            return;
        }
        if (!isValidCity(cityId, listener)) {
            return;
        }
        new AsyncTask<Void, Void, CompStuResponse>() {

            @Override
            protected CompStuResponse doInBackground(Void... params) {
                return api.completeStuInfo(studentId, cityId, studentName, accessToken);
            }

            @Override
            protected void onPostExecute(CompStuResponse compStuResponse) {
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
    public void login(final String cell_phone,final String pwd, final int loginType, final SLCallbackListener<CreateUserResponse> listener) {
        if (TextUtils.isEmpty(cell_phone)) {
            if (listener != null) {
                listener.onFailure(ErrorEvent.PARAM_NULL, "您的手机号码不能为空");
            }
            return;
        }
        // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1][358]\\d{9}";
//        if (!phoneNumber.matches(telRegex)) {
//            if (listener != null) {
//                listener.onFailure(ErrorEvent.PARAM_ILLEGAL, "您的手机号码格式有误");
//            }
//            return;
//        }
        new AsyncTask<Void, Void, CreateUserResponse>() {

            @Override
            protected CreateUserResponse doInBackground(Void... params) {
                return api.login(cell_phone, pwd, loginType);
            }

            @Override
            protected void onPostExecute(CreateUserResponse createUserResponse) {
                if (listener != null) {
                    if (createUserResponse != null) {
                        if (createUserResponse.isSuccess()) {
                            listener.onSuccess(createUserResponse);
                        } else {
                            if (createUserResponse.getCode().equals("40011")) {
                                createUserResponse.setMessage("您的短信验证码有误");
                                listener.onFailure(createUserResponse.getCode(), createUserResponse.getMessage());
                            } else {
                                listener.onFailure(createUserResponse.getCode(), createUserResponse.getMessage());
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
    public void resetPassword(final String cell_phone, final String password, final String auth_token,final SLCallbackListener<BaseApiResponse> listener) {
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
                return api.resetPassword(cell_phone,password,auth_token);
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
    private boolean isValidPhoneNumber(String phoneNumber, SLCallbackListener<Void> listener) {
        if (TextUtils.isEmpty(phoneNumber)) {
            if (listener != null) {
                listener.onFailure(ErrorEvent.PARAM_NULL, "您的手机号码不能为空");
            }
            return false;
        }
        // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1][358]\\d{9}";
//        if (!phoneNumber.matches(telRegex)) {
//            if (listener != null) {
//                listener.onFailure(ErrorEvent.PARAM_ILLEGAL, "您的手机号码格式有误");
//            }
//            return false;
//        }
        return true;
    }

    /**
     * 校验验证码格式有效性
     *
     * @param identifyCode
     * @param listener
     * @return
     */
    private boolean isValidIdentifyCode(String identifyCode, SLCallbackListener<CreateUserResponse> listener) {
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
    private boolean isValidPwd(String pwd, SLCallbackListener<CreateUserResponse> listener) {
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
    private boolean isValidUserName(String userName, SLCallbackListener<CompStuResponse> listener) {
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
    private boolean isValidCity(String cityId, SLCallbackListener<CompStuResponse> listener) {
        if (TextUtils.isEmpty(cityId)) {
            if (listener != null) {
                listener.onFailure(ErrorEvent.PARAM_NULL, "您的所在地不能为空");
            }
            return false;
        }
        return true;
    }

}
