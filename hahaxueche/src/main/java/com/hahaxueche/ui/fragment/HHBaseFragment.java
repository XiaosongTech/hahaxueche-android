package com.hahaxueche.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.base.BaseWebViewActivity;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.view.base.HHBaseView;
import com.hahaxueche.util.HHLog;

/**
 * Created by wangshirui on 16/9/17.
 */
public class HHBaseFragment extends Fragment implements HHBaseView {

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void forceOffline() {
        //token过期,强制下线
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("提示");
        builder.setMessage("会话已过期,请重新登录");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCollector.finishAll();
                Intent intent = new Intent(getContext(), StartLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    @Override
    public void alertToLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("提示");
        builder.setMessage("请先登录或者注册");
        builder.setPositiveButton("现在就去", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCollector.finishAll();
                Intent intent = new Intent(getContext(), StartLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("再看看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void openWebView(String url) {
        openWebView(url, url);
    }

    @Override
    public void openWebView(String originUrl, String shareUrl) {
        Intent intent = new Intent(getContext(), BaseWebViewActivity.class);
        Bundle bundle = new Bundle();
        HHLog.v("webview url -> " + originUrl);
        bundle.putString("url", originUrl);
        bundle.putString("shareUrl", shareUrl);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void showProgressDialog() {
        HHBaseActivity activity = (HHBaseActivity) getActivity();
        if (activity != null) {
            activity.showProgressDialog();
        }
    }

    @Override
    public void showProgressDialog(String message) {
        HHBaseActivity activity = (HHBaseActivity) getActivity();
        if (activity != null) {
            activity.showProgressDialog(message);
        }
    }

    @Override
    public void dismissProgressDialog() {
        HHBaseActivity activity = (HHBaseActivity) getActivity();
        if (activity != null) {
            activity.dismissProgressDialog();
        }
    }

}
