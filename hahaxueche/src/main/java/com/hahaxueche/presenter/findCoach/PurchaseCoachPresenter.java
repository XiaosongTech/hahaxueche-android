package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.ClassType;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PurchaseCoachView;
import com.hahaxueche.util.Common;
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

public class PurchaseCoachPresenter extends HHBasePresenter implements Presenter<PurchaseCoachView> {
    private PurchaseCoachView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Coach mCoach;
    private User mUser;
    public ClassType mClassType;
    private int paymentMethod = -1;
    private Voucher mSelectVoucher;
    private ArrayList<Voucher> mUnCumulativeVoucherList;
    private ArrayList<Voucher> mCumulativeVoucherList;

    @Override
    public void attachView(PurchaseCoachView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        mUser = application.getSharedPrefUtil().getUser();
    }

    @Override
    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mCoach = null;
        mUser = null;
        mSelectVoucher = null;
        mUnCumulativeVoucherList = null;
    }

    public void setPurchaseExtras(Coach coach, ClassType classType) {
        this.mCoach = coach;
        this.mClassType = classType;
        if (mCoach == null || mClassType == null) return;
        mView.loadCoachInfo(mCoach);
        mView.setClassTypeName((classType.licenseType == Common.LICENSE_TYPE_C1 ? "C1" : "C2") + classType.name);
        mView.setClassTypePrice(Utils.getMoney(classType.price));
        calculateAmount();
        fetchCumulativeVouchers();//代金券
        fetchUnCumulativeVouchers();
        mView.loadPaymentMethod(getPaymentMethod());
        paymentMethod = 0;//默认支付方式：支付宝
        pageStartCount();
    }

    private void calculateAmount() {
        int voucherAmount = mSelectVoucher != null ? mSelectVoucher.amount : 0;
        int totalAmount = mClassType.price;
        if (voucherAmount > 0) {
            mView.setTotalAmountWithVoucher("总价:" + Utils.getMoney(totalAmount) + " 立减:" + Utils.getMoney(voucherAmount),
                    Utils.getMoney(totalAmount - voucherAmount));
        } else {
            mView.setTotalAmountText("总价: " + Utils.getMoney(totalAmount));
        }
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void createCharge() {
        clickPayCount();
        if (mUser.student.isPurchasedService()) {
            mView.showMessage("该学员已经购买过教练");
            return;
        }
        if (paymentMethod < 0) {
            mView.showMessage("请选择支付方式");
            return;
        }
        HHApiService apiService = application.getApiService();
        final HHApiService apiServiceNoConverter = application.getApiServiceNoConverter();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        final HashMap<String, Object> mapParam = new HashMap<>();
        mapParam.put("coach_id", mCoach.id);
        mapParam.put("method", paymentMethod);
        mapParam.put("product_type", mClassType.type);
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
                        if (!student.isPurchasedService()) {
                            getStudentUtilHasCoach();
                        } else {
                            mView.dismissProgressDialog();
                            application.getSharedPrefUtil().updateStudent(student);
                            mView.paySuccess();
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
                            mView.forceOffline();
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
                            mView.forceOffline();
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
            mView.showUnCumulativeVoucher(false);
            return;
        }
        mView.showUnCumulativeVoucher(true);
        setSelectVoucher(mUnCumulativeVoucherList.get(0));
        if (mUnCumulativeVoucherList.size() == 1) {
            //有一张代金券，直接使用不选择
            mView.setVoucherSelectable(false);
        } else {
            mView.setVoucherSelectable(true);
        }

    }

    public void setSelectVoucher(Voucher voucher) {
        this.mSelectVoucher = voucher;
        mView.setVoucher(mSelectVoucher);
        calculateAmount();
    }

    public void pageStartCount() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        map.put("student_id", user.student.id);
        map.put("student_id", mCoach.id);
        MobclickAgent.onEvent(mView.getContext(), "pay_coach_status_page_viewed", map);
    }

    public void clickPayCount() {
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        map.put("student_id", user.student.id);
        map.put("student_id", mCoach.id);
        MobclickAgent.onEvent(mView.getContext(), "purchase_confirm_page_purchase_button_tapped", map);
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
            mView.showCumulativeVoucher(false);
            return;
        }
        mView.showCumulativeVoucher(true);
        for (Voucher voucher : mCumulativeVoucherList) {
            mView.addCumulativeVoucher(voucher.title, "-" + Utils.getMoney(voucher.amount));
        }
        calculateAmount();
    }
}
