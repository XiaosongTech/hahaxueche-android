package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.Bank;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.payment.BankCard;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
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

public class AddBankPresenter extends HHBasePresenter implements Presenter<AddBankView> {
    private AddBankView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private BankCard mBankCard;
    private Bank mOpenAccountBank;

    public void attachView(AddBankView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {
            mView.alertToLogin();
        }
        if (user.student.bank_card != null) {
            setBankCard(user.student.bank_card);
        }
    }

    public void detachView() {
        this.mView = null;
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
        mView.loadAccount(mBankCard);
        mView.loadOpenBank(mOpenAccountBank);
    }

    public BankCard getBankCard() {
        return this.mBankCard;
    }

    public void setOpenAccountBank(Bank bank) {
        this.mOpenAccountBank = bank;
        mView.loadOpenBank(mOpenAccountBank);
    }

    public void addBankCard(String accountName, String accountNumber) {
        if (TextUtils.isEmpty(accountName)) {
            mView.showMessage("持卡人姓名不能为空");
            return;
        }
        if (TextUtils.isEmpty(accountNumber)) {
            mView.showMessage("银行卡号不能为空");
            return;
        }
        if (mOpenAccountBank == null || TextUtils.isEmpty(mOpenAccountBank.code)) {
            mView.showMessage("开户行不能为空");
            return;
        }
        final User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mView.showProgressDialog();
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
                            mView.dismissProgressDialog();
                            mView.back(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.dismissProgressDialog();
                            if (ErrorUtil.isInvalidSession(e)) {
                                mView.forceOffline();
                            }
                            HHLog.e(e.getMessage());
                        }

                        @Override
                        public void onNext(BankCard bankCard) {

                        }
                    });
        } else {
            mView.alertToLogin();
        }
    }
}
