package com.hahaxueche.ui.fragment;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 16/9/17.
 */
public class HHBaseFragment extends Fragment implements HHBaseView {
    private ProgressDialog progressDialog;//进度框

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressDialog() {
        showProgressDialog("数据加载中，请稍后……");
    }

    @Override
    public void showProgressDialog(String message) {
        dismissProgressDialog();
        progressDialog = ProgressDialog.show(getContext(), null, message);
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


}
