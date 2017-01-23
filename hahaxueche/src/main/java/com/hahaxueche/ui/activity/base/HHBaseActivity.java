package com.hahaxueche.ui.activity.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.view.base.HHBaseView;
import com.hahaxueche.util.HHLog;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by wangshirui on 16/9/8.
 */
public class HHBaseActivity extends AppCompatActivity implements HHBaseView {
    private ProgressDialog progressDialog;//进度框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HHLog.v(getClass().getName());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showProgressDialog() {
        showProgressDialog("为学员保驾护航！");
    }

    @Override
    public void showProgressDialog(String message) {
        if (progressDialog != null && progressDialog.isShowing()) {//不干扰正在显示的进度框
            return;
        }
        progressDialog = ProgressDialog.show(getContext(), null, message);
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

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

    // 获取点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                hideSoftInput(view);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
    private boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            return !(ev.getX() > left && ev.getX() < right && ev.getY() > top
                    && ev.getY() < bottom);
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    // 隐藏软键盘
    private void hideSoftInput(View view) {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 打开webview
     *
     * @param url
     */
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

    /**
     * 提示登录
     */
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

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
