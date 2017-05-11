package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseSuccess;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.user.IdCardUrl;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyContractView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;

import java.util.HashMap;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/11/29.
 */

public class MyContractPresenter extends HHBasePresenter implements Presenter<MyContractView> {
    private MyContractView mView;
    private Subscription subscription;
    private HHBaseApplication application;

    @Override
    public void attachView(MyContractView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
    }

    @Override
    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void getAgreementUrl() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin() ||
                !user.student.isPurchasedService() || !user.student.isUploadedIdInfo()) return;
        if (!TextUtils.isEmpty(user.student.agreement_url)) {
            mView.setPdf(user.student.agreement_url, user.student.id);
            mView.setSignEnable(false);
            addDataTrack("my_contract_page_viewed", mView.getContext());
        } else {
            addDataTrack("sign_contract_page_viewed", mView.getContext());
            HHApiService apiService = application.getApiService();
            subscription = apiService.createAgreement(user.student.id, user.session.access_token)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<Response<IdCardUrl>>() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            mView.showProgressDialog();
                        }

                        @Override
                        public void onCompleted() {
                            mView.dismissProgressDialog();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.dismissProgressDialog();
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(Response<IdCardUrl> response) {
                            if (response.isSuccessful()) {
                                mView.setPdf(response.body().agreement_url, user.student.id);
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
                        mView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        mView.dismissProgressDialog();
                        mView.showShare();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.dismissProgressDialog();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mView.forceOffline();
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
                        mView.showProgressDialog("邮件发送中，请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mView.dismissProgressDialog();
                        mView.showMessage("协议已发送指定邮箱");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.dismissProgressDialog();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseSuccess baseSuccess) {
                    }
                });
    }

    public String getShareText() {
        return mView.getContext().getResources().getString(R.string.sign_share_dialog_text);
    }
}
