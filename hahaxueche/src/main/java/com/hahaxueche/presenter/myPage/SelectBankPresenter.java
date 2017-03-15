package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.base.Bank;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.SelectBankView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/11/2.
 */

public class SelectBankPresenter extends HHBasePresenter implements Presenter<SelectBankView> {
    private SelectBankView mView;
    private HHBaseApplication application;
    private ArrayList<Bank> mBanks;

    public void attachView(SelectBankView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        mBanks = application.getConstants().banks;
        if (mBanks == null || mBanks.size() < 1) return;
        ArrayList<Bank> mPopularBanks = new ArrayList<>();
        for (Bank bank : mBanks) {
            if (bank.is_popular) {
                mPopularBanks.add(bank);
            }
        }
        mView.showPopularBankList(mPopularBanks);
        mView.showBankList(mBanks);
    }

    public void detachView() {
        this.mView = null;
        application = null;
        mBanks = null;
    }

    public void searchBank(String keyword) {
        if (mBanks != null && mBanks.size() > 0) {
            if (TextUtils.isEmpty(keyword)) {
                mView.showBankList(mBanks);
            } else {
                ArrayList<Bank> searchBankList = new ArrayList<>();
                for (Bank bank : mBanks) {
                    if (bank.name.contains(keyword)) {
                        searchBankList.add(bank);
                    }
                }
                mView.showBankList(searchBankList);
            }
        }
    }

    public ArrayList<Bank> getBanks() {
        return mBanks;
    }

}
