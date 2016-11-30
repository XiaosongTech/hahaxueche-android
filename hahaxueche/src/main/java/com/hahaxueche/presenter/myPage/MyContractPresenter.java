package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.user.IdCardUrl;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyContractView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;

import java.util.HashMap;

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
        } else {
            HHApiService apiService = application.getApiService();
            subscription = apiService.createAgreement(user.student.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<IdCardUrl>() {
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
                        public void onNext(IdCardUrl idCardUrl) {
                            mMyContractView.setPdf(idCardUrl.agreement_url);
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

    public String getShareText() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return "";
        String shareText = mMyContractView.getContext().getResources().getString(R.string.sign_share_dialog_text);
        City myCity = application.getConstants().getCity(user.student.city_id);
        return String.format(shareText, Utils.getMoney(myCity.referer_bonus), Utils.getMoney(myCity.referee_bonus));
    }
}
