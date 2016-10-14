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
import com.hahaxueche.util.Utils;

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
    private int license;//1 C1; 2 C2
    private int classType;//0 普通版; 1 vip
    private int productType = -1;
    private int paymentMethod = -1;

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
        if (mCoach.coach_group.training_cost != 0) {
            mPurchaseCoachView.showLicenseC1();
        }
        if (mCoach.coach_group.c2_price != 0) {
            mPurchaseCoachView.showLicenseC2();
        }
        selectLicenseC1();
        selectClassNormal();//默认C1 普通班
        mPurchaseCoachView.loadPaymentMethod(getPaymentMethod());
        paymentMethod = 0;
    }

    public void selectLicenseC1() {
        license = 1;
        mPurchaseCoachView.unSelectLicense();
        mPurchaseCoachView.selectLicenseC1();
        if (mCoach.coach_group.vip_price != 0) {
            mPurchaseCoachView.showClassVIP();
        }
        setProductType();
    }

    public void selectLicenseC2() {
        license = 2;
        mPurchaseCoachView.unSelectLicense();
        mPurchaseCoachView.selectLicenseC2();
        if (mCoach.coach_group.c2_vip_price != 0) {
            mPurchaseCoachView.showClassVIP();
        }
        setProductType();
    }

    public void selectClassNormal() {
        classType = 0;
        mPurchaseCoachView.unSelectClass();
        mPurchaseCoachView.selectClassNormal();
        setProductType();
    }

    public void selectClassVip() {
        classType = 1;
        mPurchaseCoachView.unSelectClass();
        mPurchaseCoachView.selectClassVip();
        setProductType();
    }

    private void setProductType() {
        if (license == 1 && classType == 0) {
            productType = 0;
            mPurchaseCoachView.setTotalAmountText("总价： " + Utils.getMoney(mCoach.coach_group.training_cost));
        } else if (license == 1 && classType == 1) {
            productType = 1;
            mPurchaseCoachView.setTotalAmountText("总价： " + Utils.getMoney(mCoach.coach_group.vip_price));
        } else if (license == 2 && classType == 0) {
            productType = 2;
            mPurchaseCoachView.setTotalAmountText("总价： " + Utils.getMoney(mCoach.coach_group.c2_price));
        } else if (license == 2 && classType == 1) {
            productType = 3;
            mPurchaseCoachView.setTotalAmountText("总价： " + Utils.getMoney(mCoach.coach_group.c2_vip_price));
        }
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
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

    public void createCharge() {
        if (productType < 0) {
            mPurchaseCoachView.showMessage("请选择课程类型");
            return;
        }
        if (paymentMethod < 0) {
            mPurchaseCoachView.showMessage("请选择支付方式");
            return;
        }
        HHApiService apiService = application.getApiService();
        final HHApiService apiServiceNoConverter = application.getApiServiceNoConverter();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        final HashMap<String, Object> mapParam = new HashMap<>();
        mapParam.put("coach_id", mCoach.id);
        mapParam.put("method", paymentMethod);
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
                    public void onStart() {
                        super.onStart();
                        mPurchaseCoachView.showProgressDialog("订单生成中，请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mPurchaseCoachView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPurchaseCoachView.dismissProgressDialog();
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
                    public void onStart() {
                        super.onStart();
                        mPurchaseCoachView.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        mPurchaseCoachView.dismissProgressDialog();
                    }

                    @Override
                    public void onNext(Student student) {
                        if (!student.hasPurchasedService()) {
                            getStudentUtilHasCoach();
                        } else {
                            mPurchaseCoachView.dismissProgressDialog();
                            application.getSharedPrefUtil().updateStudent(student);
                            mPurchaseCoachView.paySuccess();
                        }
                    }
                });
    }
}
