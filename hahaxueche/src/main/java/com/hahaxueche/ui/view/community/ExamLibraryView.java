package com.hahaxueche.ui.view.community;

import android.text.SpannableString;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2016/12/1.
 */

public interface ExamLibraryView extends HHBaseView {
    void showNotLogin();

    void showNotPurchase();

    void showScores();

    void setInsuranceCount(SpannableString ss);
}
