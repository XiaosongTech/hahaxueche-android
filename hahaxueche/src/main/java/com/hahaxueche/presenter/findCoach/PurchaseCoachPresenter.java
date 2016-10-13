package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.payment.PaymentMethod;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PurchaseCoachView;
import com.hahaxueche.util.HHLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/10/11.
 */

public class PurchaseCoachPresenter implements Presenter<PurchaseCoachView> {
    private PurchaseCoachView mPurchaseCoachView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Coach mCoach;
    private User mUser;

    @Override
    public void attachView(PurchaseCoachView view) {
        this.mPurchaseCoachView = view;
        application = HHBaseApplication.get(mPurchaseCoachView.getContext());
        mUser = application.getSharedPrefUtil().getUser();
    }

    @Override
    public void detachView() {
        this.mPurchaseCoachView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mCoach = null;
        mUser = null;
    }

    public void setCoach(Coach coach) {
        this.mCoach = coach;
        if (mCoach == null) return;
        mPurchaseCoachView.loadCoachInfo(mCoach);
        mPurchaseCoachView.loadPaymentMethod(getPaymentMethod());
    }

    /**
     * 支付方式，目前支持支付宝，银行卡，分期乐
     *
     * @return
     */
    private ArrayList<PaymentMethod> getPaymentMethod() {
        ArrayList<PaymentMethod> paymentMethods = new ArrayList<>();
        PaymentMethod aliPay = new PaymentMethod(0, R.drawable.ic_alipay_icon, "支付宝", "推荐有支付宝账号的用户使用");
        PaymentMethod cardPay = new PaymentMethod(4, R.drawable.ic_cardpay_icon, "银行卡支付", "安全极速支付,无需开通网银");
        PaymentMethod fqlPay = new PaymentMethod(1, R.drawable.logo_fenqile, "分期乐", "推荐分期使用");
        paymentMethods.add(aliPay);
        paymentMethods.add(cardPay);
        paymentMethods.add(fqlPay);
        return paymentMethods;
    }

    public void createCharge(int method, int productType) {
        HHApiService apiService = application.getApiService();
        final HHApiService apiServiceNoConverter = application.getApiServiceNoConverter();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        final HashMap<String, Object> mapParam = new HashMap<>();
        mapParam.put("coach_id", mCoach.id);
        mapParam.put("method", method);
        mapParam.put("product_type", productType);
        subscription = apiService.isValidToken(mUser.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiServiceNoConverter.createCharge(mapParam, mUser.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ResponseBody>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
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
                        mPurchaseCoachView.callPingpp(result);
                    }
                });
    }

    public void getStudentUtilHasCoach() {
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        subscription = apiService.isValidToken(mUser.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<Student>>() {
                    @Override
                    public Observable<Student> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.getStudent(mUser.student.id, mUser.session.access_token).delay(1, TimeUnit.SECONDS);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Student>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Student student) {
                        if (!student.hasPurchasedService()) {
                            getStudentUtilHasCoach();
                        } else {
                            mPurchaseCoachView.showMessage("付款成功");
                        }
                    }
                });
    }
}
