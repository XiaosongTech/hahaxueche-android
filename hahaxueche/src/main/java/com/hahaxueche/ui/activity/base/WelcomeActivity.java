package com.hahaxueche.ui.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.hahaxueche.R;
import com.hahaxueche.presenter.base.WelcomePresenter;
import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.view.base.WelcomeView;
import com.hahaxueche.ui.view.login.CompleteUserInfoView;
import com.hahaxueche.util.HHLog;
import com.singulariti.deepshare.DeepShare;
import com.singulariti.deepshare.listeners.DSInappDataListener;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/9/8.
 */
public class WelcomeActivity extends HHBaseActivity implements WelcomeView, DSInappDataListener {
    private WelcomePresenter mPresenter;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    public void onStart() {
        super.onStart();
        // 请直接复制下一行代码，粘贴到相应位置
        DeepShare.init(this, "c4e677e0fa60ceb4", this);
        mPresenter.startApplication();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new WelcomePresenter();
        mPresenter.attachView(this);
        setTheme(R.style.AppThemeNoTitle);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        MobclickAgent.setDebugMode(true);
    }

    @Override
    public void navigationToStartLogin() {
        startActivity(new Intent(getContext(), StartLoginActivity.class));
        WelcomeActivity.this.finish();
    }

    @Override
    public void navigateToCompleteInfo() {
        startActivity(new Intent(getContext(), CompleteUserInfoView.class));
        WelcomeActivity.this.finish();
    }

    @Override
    public void navigateToHomepage(Bundle bundle) {
        ActivityCollector.finishAll();
        Intent intent = new Intent(getContext(), MainActivity.class);
        if (bundle != null) {
            intent.putExtra("shareObject", bundle);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        DeepShare.onStop();//停止DeepShare
    }

    @Override
    /** 代理方法onInappDataReturned处理获取的启动参数
     * @param params 所获取到的启动参数
     */
    public void onInappDataReturned(JSONObject params) {
        try {
            if (params == null) return;
            HHLog.v("inapp_data -> " + params.toString());
            String type = params.getString("type");
            Bundle shareObject = new Bundle();
            shareObject.putString("type", type);
            if (type.equals("coach_detail")) {
                shareObject.putString("objectId", params.getString("coach_id"));
            } else if (type.equals("training_partner_detail")) {
                shareObject.putString("objectId", params.getString("training_partner_id"));
            }
            mPresenter.setShareObject(shareObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(String s) {
        HHLog.e("deepshare error -> " + s);
    }
}
