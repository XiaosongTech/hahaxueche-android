package com.hahaxueche.presenter.myPage;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyVoucherView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/11/11.
 */

public class MyVoucherPresenter implements Presenter<MyVoucherView> {
    private MyVoucherView mMyVoucherView;
    private Subscription subscription;
    private HHBaseApplication application;
    private User mUser;

    public void attachView(MyVoucherView view) {
        this.mMyVoucherView = view;
        application = HHBaseApplication.get(mMyVoucherView.getContext());
        mUser = application.getSharedPrefUtil().getUser();
        getVouchers();
        mMyVoucherView.changeCustomerService();
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
                        mMyVoucherView.startRefresh();
                    }

                    @Override
                    public void onCompleted() {
                        mMyVoucherView.stopRefresh();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMyVoucherView.stopRefresh();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mMyVoucherView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Student student) {
                        application.getSharedPrefUtil().updateStudent(student);
                        mMyVoucherView.clearVouchers();
                        if (student.vouchers != null && student.vouchers.size() > 0) {
                            mMyVoucherView.loadVouchers(student.vouchers);
                        } else {
                            mMyVoucherView.showNoVoucher();
                        }
                    }
                });
    }

}
