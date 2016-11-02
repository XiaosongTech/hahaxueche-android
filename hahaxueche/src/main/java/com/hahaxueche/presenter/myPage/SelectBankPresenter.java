package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.base.Bank;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.SelectBankView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/11/2.
 */

public class SelectBankPresenter implements Presenter<SelectBankView> {
    private SelectBankView mSelectBankView;
    private HHBaseApplication application;
    private ArrayList<Bank> mBanks;
    private ArrayList<Bank> mPopularBanks;

    public void attachView(SelectBankView view) {
        this.mSelectBankView = view;
        application = HHBaseApplication.get(mSelectBankView.getContext());
        mBanks = application.getConstants().banks;
        if (mBanks == null || mBanks.size() < 1) return;
        mPopularBanks = new ArrayList<>();
        for (Bank bank : mBanks) {
            if (bank.is_popular) {
                mPopularBanks.add(bank);
            }
        }
        mSelectBankView.showPopularBankList(mPopularBanks);
        mSelectBankView.showBankList(mBanks);
    }

    public void detachView() {
        this.mSelectBankView = null;
        application = null;
        mBanks = null;
    }

    public void searchBank(String keyword) {
        if (mBanks != null && mBanks.size() > 0) {
            if (TextUtils.isEmpty(keyword)) {
                mSelectBankView.showBankList(mBanks);
            } else {
                ArrayList<Bank> searchBankList = new ArrayList<>();
                for (Bank bank : mBanks) {
                    if (bank.name.contains(keyword)) {
                        searchBankList.add(bank);
                    }
                }
                mSelectBankView.showBankList(searchBankList);
            }
        }
    }

    public ArrayList<Bank> getBanks() {
        return mBanks;
    }

}
