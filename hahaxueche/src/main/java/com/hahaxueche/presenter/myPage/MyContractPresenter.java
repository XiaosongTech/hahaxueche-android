package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseSuccess;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.base.ErrorResponse;
import com.hahaxueche.model.user.IdCardUrl;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyContractView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/11/29.
 */

public class MyContractPresenter implements Presenter<MyContractView> {
    private MyContractView mMyContractView;
    private Subscription subscription;
    private HHBaseApplication application;

    @Override
    public void attachView(MyContractView view) {
        this.mMyContractView = view;
        application = HHBaseApplication.get(mMyContractView.getContext());
    }

    @Override
    public void detachView() {
        this.mMyContractView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void getAgreementUrl() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin() ||
                !user.student.hasPurchasedService() || !user.student.isUploadedIdInfo()) return;
        if (!TextUtils.isEmpty(user.student.agreement_url)) {
            mMyContractView.setPdf(user.student.agreement_url);
            mMyContractView.setSignEnable(false);
            myContractViewCount();
        } else {
            signContractViewCount();
            HHApiService apiService = application.getApiService();
            subscription = apiService.createAgreement(user.student.id, user.session.access_token)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<Response<IdCardUrl>>() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            mMyContractView.showProgressDialog();
                        }

                        @Override
                        public void onCompleted() {
                            mMyContractView.dismissProgressDialog();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mMyContractView.dismissProgressDialog();
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Response<IdCardUrl> response) {
                            if (response.isSuccessful()) {
                                mMyContractView.setPdf(response.body().agreement_url);
                            }
                        }
                    });
        }
    }

    public void sign() {
        final HHApiService apiService = application.getApiService();
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<Student>>() {
                    @Override
                    public Observable<Student> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.signAgreement(user.student.id, user.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Student>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mMyContractView.showProgressDialog("数据上传中，请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mMyContractView.dismissProgressDialog();
                        mMyContractView.showShare();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMyContractView.dismissProgressDialog();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mMyContractView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Student student) {
                        application.getSharedPrefUtil().updateStudent(student);
                    }
                });
    }

    public void setAgreementEmail(String email) {
        final HHApiService apiService = application.getApiService();
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        final HashMap<String, Object> mapParam = new HashMap<>();
        mapParam.put("email", email);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<BaseSuccess>>() {
                    @Override
                    public Observable<BaseSuccess> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.sendAgreementEmail(user.student.id, mapParam, user.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<BaseSuccess>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mMyContractView.showProgressDialog("邮件发送中，请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mMyContractView.dismissProgressDialog();
                        mMyContractView.showMessage("协议已发送指定邮箱");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMyContractView.dismissProgressDialog();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mMyContractView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseSuccess baseSuccess) {
                    }
                });
    }

    public void myContractViewCount() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mMyContractView.getContext(), "my_contract_page_viewed", map);
    }

    public void signContractViewCount() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mMyContractView.getContext(), "sign_contract_page_viewed", map);
    }

    public void clickAgreement() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mMyContractView.getContext(), "sign_contract_check_box_checked", map);
    }

    public void clickSettingIcon() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mMyContractView.getContext(), "my_contract_page_top_right_button_tapped", map);
    }

    public void clickSendAgreement() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mMyContractView.getContext(), "my_contract_page_send_by_email_tapped", map);
    }

    public void clickDownloadAgreement() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
        }
        MobclickAgent.onEvent(mMyContractView.getContext(), "my_contract_page_download_tapped", map);
    }

    public String getShareText() {
        return mMyContractView.getContext().getResources().getString(R.string.sign_share_dialog_text);
    }
}
