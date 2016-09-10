package com.hahaxueche.ui.view;

import android.content.Context;

/**
 * Created by wangshirui on 16/9/8.
 */
public interface HHBaseView {
    Context getContext();

    void showProgressDialog();

    void showProgressDialog(String message);

    void dismissProgressDialog();
}
