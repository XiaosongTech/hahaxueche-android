package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.Bank;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.payment.BankCard;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.AddBankView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/11/2.
 */

public class AddBankPresenter implements Presenter<AddBankView> {
    private AddBankView mAddBankView;
    private Subscription subscription;
    private HHBaseApplication application;
    private BankCard mBankCard;
    private Bank mOpenAccountBank;

    public void attachView(AddBankView view) {
        this.mAddBankView = view;
        application = HHBaseApplication.get(mAddBankView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {
            mAddBankView.alertToLogin();
        }
        if (user.student.bank_card != null) {
            setBankCard(user.student.bank_card);
        }
    }

    public void detachView() {
        this.mAddBankView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mBankCard = null;
        mOpenAccountBank = null;
    }

    public void setBankCard(BankCard bankCard) {
        this.mBankCard = bankCard;
        mOpenAccountBank = new Bank();
        mOpenAccountBank.code = mBankCard.open_bank_code;
        mOpenAccountBank.name = mBankCard.bank_name;
        mAddBankView.loadAccount(mBankCard);
        mAddBankView.loadOpenBank(mOpenAccountBank);
    }

    public BankCard getBankCard() {
        return this.mBankCard;
    }

    public void setOpenAccountBank(Bank bank) {
        this.mOpenAccountBank = bank;
        mAddBankView.loadOpenBank(mOpenAccountBank);
    }

    public void addBankCard(String accountName, String accountNumber) {
        if (TextUtils.isEmpty(accountName)) {
            mAddBankView.showMessage("持卡人姓名不能为空");
            return;
        }
        if (TextUtils.isEmpty(accountNumber)) {
            mAddBankView.showMessage("银行卡号不能为空");
            return;
        }
        if (mOpenAccountBank == null || TextUtils.isEmpty(mOpenAccountBank.code)) {
            mAddBankView.showMessage("开户行不能为空");
            return;
        }
        final User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mAddBankView.showProgressDialog("信息上传中，请稍后...");
            final HHApiService apiService = application.getApiService();
            HashMap<String, Object> map = new HashMap<>();
            map.put("cell_phone", user.cell_phone);
            final HashMap<String, Object> mapParam = new HashMap<>();
            mapParam.put("name", accountName);
            mapParam.put("card_number", accountNumber);
            mapParam.put("open_bank_code", mOpenAccountBank.code);
            mapParam.put("transferable_type", "Student");
            mapParam.put("transferable_id", user.student.id);
            subscription = apiService.isValidToken(user.session.access_token, map)
                    .flatMap(new Func1<BaseValid, Observable<BankCard>>() {
                        @Override
                        public Observable<BankCard> call(BaseValid baseValid) {
                            if (baseValid.valid) {
                                return apiService.addBankCard(mapParam, user.session.access_token);
                            } else {
                                return application.getSessionObservable();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(application.defaultSubscribeScheduler())
                    .subscribe(new Subscriber<BankCard>() {
                        @Override
                        public void onCompleted() {
                            mAddBankView.dismissProgressDialog();
                            mAddBankView.back(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mAddBankView.dismissProgressDialog();
                            if (ErrorUtil.isInvalidSession(e)) {
                                mAddBankView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(BankCard bankCard) {

                        }
                    });
        } else {
            mAddBankView.alertToLogin();
        }
    }
}
