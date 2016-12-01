package com.hahaxueche.presenter.community;

import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.community.ExamLibraryView;
import com.hahaxueche.util.Utils;

import rx.Subscription;

/**
 * Created by wangshirui on 2016/12/1.
 */

public class ExamLibraryPresenter implements Presenter<ExamLibraryView> {
    private ExamLibraryView mExamLibraryView;
    private Subscription subscription;
    private HHBaseApplication application;

    public void attachView(ExamLibraryView view) {
        this.mExamLibraryView = view;
        application = HHBaseApplication.get(mExamLibraryView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {
            mExamLibraryView.showNotLogin();
        } else if (!user.student.hasPurchasedService()) {
            mExamLibraryView.showNotPurchase();
        } else {
            mExamLibraryView.showScores();
        }
        String text = Utils.getCount(11999) + "人已获得保过卡";
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mExamLibraryView.getContext(), R.color.app_theme_color)), 0, text.indexOf("人"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mExamLibraryView.setInsuranceCount(ss);
    }

    public void detachView() {
        this.mExamLibraryView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public int getBonus() {
        int cityId = 0;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.student != null) {
            cityId = user.student.city_id;
        }
        return application.getConstants().getCity(cityId).referer_bonus;
    }
}
