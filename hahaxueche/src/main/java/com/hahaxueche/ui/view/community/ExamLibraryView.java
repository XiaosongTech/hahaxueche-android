package com.hahaxueche.ui.view.community;

import android.text.SpannableString;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2016/12/1.
 */

public interface ExamLibraryView extends HHBaseView {
    void showNotLogin();

    void showNotPurchase();

    void showScores(int passCount);

    void setInsuranceCount(SpannableString ss);

    void showMessage(String message);

    void initShareData(String description, String shareUrl);
}
