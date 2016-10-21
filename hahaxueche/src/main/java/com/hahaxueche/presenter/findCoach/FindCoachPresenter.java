package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.FindCoachView;

import rx.Subscription;

/**
 * Created by wangshirui on 2016/10/19.
 */

public class FindCoachPresenter implements Presenter<FindCoachView> {
    private FindCoachView mFindCoachView;
    private Subscription subscription;
    private int currentPage = 0;//0,驾校教练；1，陪练教练

    public void attachView(FindCoachView view) {
        this.mFindCoachView = view;
        //默认选中驾校教练
        selectCoach();
    }

    public void detachView() {
        this.mFindCoachView = null;
        if (subscription != null) subscription.unsubscribe();
    }

    public void selectCoach() {
        mFindCoachView.selectCoach();
        mFindCoachView.unSelectPartner();
        mFindCoachView.showLeftIconMap();
        mFindCoachView.showSearchIcon(true);
        mFindCoachView.showCoachListFragment();
        currentPage = 0;
    }

    public void selectPartner() {
        mFindCoachView.selectPartner();
        mFindCoachView.unSelectCoach();
        mFindCoachView.showLeftIconExplain();
        mFindCoachView.showSearchIcon(false);
        mFindCoachView.showPartnerListFragment();
        currentPage = 1;
    }

    public void clickLeftIcon() {
        if (currentPage == 1) {
            mFindCoachView.showPartnerInfoDialog();
        } else {
            mFindCoachView.navigateToSelectFields();
        }
    }


}
