package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PurchasePrepaidView;
import com.hahaxueche.util.HHLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2017/3/13.
 */

public class PurchasePrepaidPresenter extends HHBasePresenter implements Presenter<PurchasePrepaidView> {
    private PurchasePrepaidView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private int mInsuranceType;
    private int mPaymentMethod = -1;

    @Override
    public void attachView(PurchasePrepaidView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        mView.loadPaymentMethod(getPaymentMethod());
        mPaymentMethod = 0;//默认支付方式：支付宝
    }

    @Override
    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.mPaymentMethod = paymentMethod;
    }

    public void createCharge() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {
            mView.alertToLogin();
            return;
        }
        if (user.student.hasPurchasedService()) {
            mView.showMessage("该学员已经购买过教练");
            return;
        }
        if (mPaymentMethod < 0) {
            mView.showMessage("请选择支付方式");
            return;
        }
        HHApiService apiService = application.getApiService();
        final HHApiService apiServiceNoConverter = application.getApiServiceNoConverter();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        final HashMap<String, Object> mapParam = new HashMap<>();
        mapParam.put("method", mPaymentMethod);
        mapParam.put("phone", user.cell_phone);
        mapParam.put("event_type", 3);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiServiceNoConverter.getPrepayCharge(mapParam, user.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mView.showProgressDialog("订单生成中，请稍后...");
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
                    public void onNext(ResponseBody responseBody) {
                        //Try to get response body
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        reader = new BufferedReader(new InputStreamReader(responseBody.byteStream()));
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String result = sb.toString();
                        mView.callPingpp(result);
                    }
                });
    }

    public void getStudentUtilPrepaid() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {
            mView.alertToLogin();
            return;
        }
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<Student>>() {
                    @Override
                    public Observable<Student> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.getStudent(user.student.id, user.session.access_token).delay(1, TimeUnit.SECONDS);
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

                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        mView.dismissProgressDialog();
                    }

                    @Override
                    public void onNext(Student student) {
                        if (student.prepaid_amount <= 0) {
                            getStudentUtilPrepaid();
                        } else {
                            mView.dismissProgressDialog();
                            application.getSharedPrefUtil().updateStudent(student);
                            mView.paySuccess();
                        }
                    }
                });
    }
}
