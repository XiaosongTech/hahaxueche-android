package com.hahaxueche.ui.view.base;

import android.content.Context;

/**
 * Created by wangshirui on 16/9/8.
 */
public interface HHBaseView {
    Context getContext();

    void showProgressDialog();

    void showProgressDialog(String message);

    void dismissProgressDialog();

    void showError(String message);
}
