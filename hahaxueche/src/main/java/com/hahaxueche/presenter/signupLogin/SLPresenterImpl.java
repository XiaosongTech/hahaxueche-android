package com.hahaxueche.presenter.signupLogin;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.hahaxueche.api.signupLogin.SLApi;
import com.hahaxueche.api.signupLogin.SLApiImpl;
import com.hahaxueche.api.util.ApiError;
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
    public void getIdentifyCode(final String phoneNum, final SLCallbackListener<Void> listener) {
        //参数检查
        if (TextUtils.isEmpty(phoneNum)) {
            if (listener != null) {
                listener.onFailure(ErrorEvent.PARAM_NULL, "您的手机号码不能为空");
            }
            return;
        }
        if (!isValidPhoneNumber(phoneNum)) {
            if (listener != null) {
                listener.onFailure(ErrorEvent.PARAM_ILLEGAL, "请输入有效手机号");
            }
            return;
        }
        new AsyncTask<Void, Void, BaseApiResponse>() {
            @Override
            protected BaseApiResponse doInBackground(Void... voids) {
                return api.sendAuthToken(phoneNum,"register");
            }

            @Override
            protected void onPostExecute(BaseApiResponse baseApiResponse) {
                if (listener != null) {
                    if(baseApiResponse!=null){
                        if(baseApiResponse.isSuccess()){
                            listener.onSuccess(null);
                        }else{
                            listener.onFailure(baseApiResponse.getCode(), baseApiResponse.getMessage());
                        }
                    }else{
                        listener.onFailure(ApiError.TIME_OUT_EVENT,ApiError.TIME_OUT_EVENT_MSG);
                    }
                }
            }
        }.execute();
    }

    /**
     * 校验手机号有效性
     * @param phoneNumber
     * @return
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1][358]\\d{9}";
        if (!phoneNumber.matches(telRegex)) {
            return false;
        }
        return true;
    }
}
