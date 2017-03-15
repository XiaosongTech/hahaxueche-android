package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyVoucherView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/11/11.
 */

public class MyVoucherPresenter extends HHBasePresenter implements Presenter<MyVoucherView> {
    private MyVoucherView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private User mUser;

    public void attachView(MyVoucherView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        mUser = application.getSharedPrefUtil().getUser();
        getVouchers();
        mView.changeCustomerService();
    }

    public void detachView() {
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mUser = null;
    }

    public void getVouchers() {
        if (mUser == null || !mUser.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        subscription = apiService.isValidToken(mUser.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<Student>>() {
                    @Override
                    public Observable<Student> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.getStudent(mUser.student.id, mUser.session.access_token);
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
                        mView.startRefresh();
                    }

                    @Override
                    public void onCompleted() {
                        mView.stopRefresh();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.stopRefresh();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Student student) {
                        application.getSharedPrefUtil().updateStudent(student);
                        mView.clearVouchers();
                        if (student.vouchers != null && student.vouchers.size() > 0) {
                            mView.loadVouchers(student.vouchers);
                        } else {
                            mView.showNoVoucher();
                        }
                    }
                });
    }

    public void addVoucher(String code) {
        if (TextUtils.isEmpty(code)) {
            mView.showMessage("优惠码不能为空！");
            return;
        }
        if (mUser == null || !mUser.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", mUser.cell_phone);
        final HashMap<String, Object> mapParam = new HashMap<>();
        mapParam.put("phone", mUser.cell_phone);
        mapParam.put("code", code);
        subscription = apiService.isValidToken(mUser.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<Voucher>>() {
                    @Override
                    public Observable<Voucher> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.addVoucher(mapParam, mUser.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Voucher>() {

                    @Override
                    public void onCompleted() {
                        mView.showMessage("代金券激活成功！");
                        getVouchers();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.stopRefresh();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mView.forceOffline();
                        } else if (ErrorUtil.isHttp404(e)) {
                            mView.showMessage("输入的优惠码不存在！");
                        } else if (ErrorUtil.isHttp422(e)) {
                            mView.showMessage("该代金券已存在！");
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Voucher voucher) {
                    }
                });
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }

}
