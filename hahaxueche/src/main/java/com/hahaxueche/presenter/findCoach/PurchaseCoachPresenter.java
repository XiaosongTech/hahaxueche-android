package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.payment.PaymentMethod;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PurchaseCoachView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
import com.umeng.analytics.MobclickAgent;

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
    private int classType;//0 超值班; 1 vip
    private int productType = -1;
    private int paymentMethod = -1;
    private Voucher mSelectVoucher;
    private ArrayList<Voucher> mUnCumulativeVoucherList;
    private ArrayList<Voucher> mCumulativeVoucherList;
    //默认选择赔付宝
    private boolean mIsSelectInsurance = true;

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
        mSelectVoucher = null;
        mUnCumulativeVoucherList = null;
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
        selectClassNormal();//默认C1 超值班
        fetchCumulativeVouchers();//代金券
        fetchUnCumulativeVouchers();
        mPurchaseCoachView.loadPaymentMethod(getPaymentMethod());
        paymentMethod = 0;//默认支付方式：支付宝
        pageStartCount();
    }

    public void selectLicenseC1() {
        license = 1;
        mPurchaseCoachView.unSelectLicense();
        mPurchaseCoachView.selectLicenseC1();
        if (mCoach.coach_group.vip_price != 0) {
            mPurchaseCoachView.showClassVIP();
        } else {
            selectClassNormal();
            mPurchaseCoachView.hideClassVIP();
        }
        calculateAmount();
    }

    public void selectLicenseC2() {
        license = 2;
        mPurchaseCoachView.unSelectLicense();
        mPurchaseCoachView.selectLicenseC2();
        if (mCoach.coach_group.c2_vip_price != 0) {
            mPurchaseCoachView.showClassVIP();
        } else {
            selectClassNormal();
            mPurchaseCoachView.hideClassVIP();
        }
        calculateAmount();
    }

    public void selectClassNormal() {
        classType = 0;
        mPurchaseCoachView.unSelectClass();
        mPurchaseCoachView.selectClassNormal();
        calculateAmount();
    }

    public void selectClassVip() {
        classType = 1;
        mPurchaseCoachView.unSelectClass();
        mPurchaseCoachView.selectClassVip();
        calculateAmount();
    }

    private void calculateAmount() {
        int voucherAmount = mSelectVoucher != null ? mSelectVoucher.amount : 0;
        int totalAmount = 0;
        //可叠加代金券的优惠金额
        if (mCumulativeVoucherList != null && mCumulativeVoucherList.size() > 0) {
            for (Voucher voucher : mCumulativeVoucherList) {
                voucherAmount += voucher.amount;
            }
        }
        if (license == 1 && classType == 0) {
            productType = 0;
            totalAmount = mCoach.coach_group.training_cost;
        } else if (license == 1 && classType == 1) {
            productType = 1;
            totalAmount = mCoach.coach_group.vip_price;
        } else if (license == 2 && classType == 0) {
            productType = 2;
            totalAmount = mCoach.coach_group.c2_price;
        } else if (license == 2 && classType == 1) {
            productType = 3;
            totalAmount = mCoach.coach_group.c2_vip_price;
        }
        //赔付宝金额计算
        if (mIsSelectInsurance) {
            totalAmount += 12000;
        }
        if (voucherAmount > 0) {
            mPurchaseCoachView.setTotalAmountWithVoucher("总价:" + Utils.getMoney(totalAmount) + " 立减:" + Utils.getMoney(voucherAmount),
                    Utils.getMoney(totalAmount - voucherAmount));
        } else {
            mPurchaseCoachView.setTotalAmountText("总价: " + Utils.getMoney(totalAmount));
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
        PaymentMethod aliPay = new PaymentMethod(0, R.drawable.ic_alipay_icon, "支付宝", "推荐拥有支付宝账号的用户使用");
        PaymentMethod wxlPay = new PaymentMethod(5, R.drawable.ic_wx_icon, "微信支付", "推荐拥有微信账号的用户使用");
        PaymentMethod cardPay = new PaymentMethod(4, R.drawable.ic_cardpay_icon, "银行卡", "一网通支付，支持所有主流借记卡/信用卡");
        PaymentMethod fqlPay = new PaymentMethod(1, R.drawable.logo_fenqile, "分期乐", "推荐分期使用");
        paymentMethods.add(aliPay);
        paymentMethods.add(wxlPay);
        paymentMethods.add(cardPay);
        paymentMethods.add(fqlPay);
        return paymentMethods;
    }

    public void createCharge() {
        clickPayCount();
        if (mUser.student.hasPurchasedService()) {
            mPurchaseCoachView.showMessage("该学员已经购买过教练");
            return;
        }
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
        mapParam.put("need_insurance", mIsSelectInsurance);
        if (mSelectVoucher != null) {
            mapParam.put("voucher_id", mSelectVoucher.id);
        }
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

    private void fetchCumulativeVouchers() {
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        subscription = apiService.isValidToken(mUser.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<ArrayList<Voucher>>>() {
                    @Override
                    public Observable<ArrayList<Voucher>> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.getAvailableVouchers(mUser.student.id, mCoach.id, "1", mUser.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ArrayList<Voucher>>() {

                    @Override
                    public void onCompleted() {
                        loadCumulativeVoucher();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isInvalidSession(e)) {
                            mPurchaseCoachView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<Voucher> vouchers) {
                        categoryVouchers(vouchers);
                    }
                });
    }

    private void fetchUnCumulativeVouchers() {
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        subscription = apiService.isValidToken(mUser.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<ArrayList<Voucher>>>() {
                    @Override
                    public Observable<ArrayList<Voucher>> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.getAvailableVouchers(mUser.student.id, mCoach.id, "0", mUser.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ArrayList<Voucher>>() {

                    @Override
                    public void onCompleted() {
                        loadUnCumulativeVoucher();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (ErrorUtil.isInvalidSession(e)) {
                            mPurchaseCoachView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<Voucher> vouchers) {
                        categoryVouchers(vouchers);
                    }
                });
    }

    /**
     * 优惠券分类
     *
     * @param vouchers
     */
    private void categoryVouchers(ArrayList<Voucher> vouchers) {
        if (vouchers == null || vouchers.size() < 1) return;
        for (Voucher voucher : vouchers) {
            if (voucher.cumulative == 1) {
                if (mCumulativeVoucherList == null) {
                    mCumulativeVoucherList = new ArrayList<>();
                }
                mCumulativeVoucherList.add(voucher);
            } else {
                if (mUnCumulativeVoucherList == null) {
                    mUnCumulativeVoucherList = new ArrayList<>();
                }
                mUnCumulativeVoucherList.add(voucher);
            }
        }
    }

    private void loadUnCumulativeVoucher() {
        if (mUnCumulativeVoucherList == null || mUnCumulativeVoucherList.size() < 1) {
            mPurchaseCoachView.showUnCumulativeVoucher(false);
            return;
        }
        mPurchaseCoachView.showUnCumulativeVoucher(true);
        setSelectVoucher(mUnCumulativeVoucherList.get(0));
        if (mUnCumulativeVoucherList.size() == 1) {
            //有一张代金券，直接使用不选择
            mPurchaseCoachView.setVoucherSelectable(false);
        } else {
            mPurchaseCoachView.setVoucherSelectable(true);
        }

    }

    public void setSelectVoucher(Voucher voucher) {
        this.mSelectVoucher = voucher;
        mPurchaseCoachView.setVoucher(mSelectVoucher);
        calculateAmount();
    }

    public void pageStartCount() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        map.put("student_id", user.student.id);
        map.put("student_id", mCoach.id);
        MobclickAgent.onEvent(mPurchaseCoachView.getContext(), "pay_coach_status_page_viewed", map);
    }

    public void clickPayCount() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        map.put("student_id", user.student.id);
        map.put("student_id", mCoach.id);
        MobclickAgent.onEvent(mPurchaseCoachView.getContext(), "purchase_confirm_page_purchase_button_tapped", map);
    }

    public ArrayList<Voucher> getUnCumulativeVoucherList() {
        return mUnCumulativeVoucherList;
    }

    public void setUnCumulativeVoucherList(ArrayList<Voucher> voucherList) {
        mUnCumulativeVoucherList = voucherList;
        if (mUnCumulativeVoucherList == null || mUnCumulativeVoucherList.size() < 1) return;
        for (Voucher voucher : mUnCumulativeVoucherList) {
            if (voucher.isSelect) {
                setSelectVoucher(voucher);
                break;
            }
        }
    }

    /**
     * 加载可叠加代金券
     */
    private void loadCumulativeVoucher() {
        if (mCumulativeVoucherList == null || mCumulativeVoucherList.size() < 1) {
            mPurchaseCoachView.showCumulativeVoucher(false);
            return;
        }
        mPurchaseCoachView.showCumulativeVoucher(true);
        for (Voucher voucher : mCumulativeVoucherList) {
            mPurchaseCoachView.addCumulativeVoucher(voucher.title, "-" + Utils.getMoney(voucher.amount));
        }
        calculateAmount();
    }

    /**
     * 选择赔付保
     *
     * @param isSelect
     */
    public void selectInsurance(boolean isSelect) {
        mIsSelectInsurance = isSelect;
        if (mIsSelectInsurance) {
            mPurchaseCoachView.selectInsurance();
        } else {
            mPurchaseCoachView.unSelectInsurance();
        }
        calculateAmount();
    }
}
