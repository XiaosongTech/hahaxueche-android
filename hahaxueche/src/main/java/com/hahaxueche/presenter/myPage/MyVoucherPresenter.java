package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.model.user.User;
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

    public void addVoucher(String code) {
        if (TextUtils.isEmpty(code)) {
            mMyVoucherView.showMessage("优惠码不能为空！");
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
                        mMyVoucherView.showMessage("代金券激活成功！");
                        getVouchers();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMyVoucherView.stopRefresh();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mMyVoucherView.forceOffline();
                        } else if (ErrorUtil.isHttp404(e)) {
                            mMyVoucherView.showMessage("输入的优惠码不存在！");
                        } else if (ErrorUtil.isHttp422(e)) {
                            mMyVoucherView.showMessage("该代金券已存在！");
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
        String title = "聊天窗口的标题";
        // 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入三个参数分别为来源页面的url，来源页面标题，来源页面额外信息（可自由定义）
        // 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
        ConsultSource source = new ConsultSource("", "android", "");
        //登录用户添加用户信息
        if (user != null && user.isLogin()) {
            YSFUserInfo userInfo = new YSFUserInfo();
            userInfo.userId = user.id;
            userInfo.data = "[{\"key\":\"real_name\", \"value\":\"" + user.student.name + "\"},{\"key\":\"mobile_phone\", \"value\":\"" + user.student.cell_phone + "\"}]";
            Unicorn.setUserInfo(userInfo);
        }
        // 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable(), 如果返回为false，该接口不会有任何动作
        Unicorn.openServiceActivity(mMyVoucherView.getContext(), // 上下文
                title, // 聊天窗口的标题
                source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
        );
    }

}
