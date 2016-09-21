package com.hahaxueche.ui.activity.myPage;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.presenter.myPage.SoftwareInfoPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.myPage.SoftwareInfoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 16/9/21.
 */

public class SoftwareInfoActivity extends HHBaseActivity implements SoftwareInfoView {
    @BindView(R.id.tv_current_version)
    TextView mTvCurrentVersion;
    ImageView mIvBack;
    TextView mTvTitle;
    private SoftwareInfoPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new SoftwareInfoPresenter();
        setContentView(R.layout.activity_software_info);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("软件信息");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoftwareInfoActivity.this.finish();
            }
        });
    }

    @Override
    public void setVersionCode(String versionCode) {
        mTvCurrentVersion.setText(versionCode);
    }

    @OnClick(R.id.rly_version_check)
    public void versionCheck() {
        mPresenter.doVersionCheck();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }
}