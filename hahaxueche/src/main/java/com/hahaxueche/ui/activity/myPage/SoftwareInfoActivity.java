package com.hahaxueche.ui.activity.myPage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    ImageView mIvBack;
    TextView mTvTitle;
    private SoftwareInfoPresenter mPresenter;
    private static final int PERMISSIONS_REQUEST_SDCARD = 600;

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

    @Override
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.rly_version_check)
    public void versionCheck() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_SDCARD);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            mPresenter.doVersionCheck();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_SDCARD) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                mPresenter.doVersionCheck();
            } else {
                showMessage("请允许读写sdcard权限，不然无法下载最新的安装包");
            }
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }
}