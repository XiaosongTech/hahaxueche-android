package com.hahaxueche.ui.activity.myPage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.hahaxueche.R;
import com.hahaxueche.presenter.myPage.MyContractPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.dialog.BaseAlertDialog;
import com.hahaxueche.ui.dialog.myPage.EnterEmailDialog;
import com.hahaxueche.ui.dialog.myPage.MyContractDialog;
import com.hahaxueche.ui.view.myPage.MyContractView;
import com.hahaxueche.util.DownloadContractManager;
import com.hahaxueche.util.HHLog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/29.
 */

public class MyContractActivity extends HHBaseActivity implements MyContractView {
    private MyContractPresenter mPresenter;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    @BindView(R.id.pdf_contract)
    PDFView mPdfContract;
    @BindView(R.id.fly_sign)
    FrameLayout mFlySign;
    @BindView(R.id.cb_sign)
    CheckBox mCbSign;
    private ImageView mIvSettings;
    private static final int PERMISSIONS_REQUEST_SDCARD = 600;
    private String pdfUrl;
    private Uri pdfUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MyContractPresenter();
        setContentView(R.layout.activity_my_contract);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        Intent intent = getIntent();
        if (intent != null && !TextUtils.isEmpty(intent.getStringExtra("pdfUrl"))) {
            setPdf(intent.getStringExtra("pdfUrl"));
            mPresenter.signContractViewCount();
        } else {
            mPresenter.getAgreementUrl();
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_my_contract);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("我的协议");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyContractActivity.this.finish();
            }
        });
        mIvSettings = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_settings);
        mIvSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.clickSettingIcon();
                MyContractDialog dialog = new MyContractDialog(MyContractActivity.this);
                dialog.show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setPdf(String url) {
        pdfUrl = url;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_SDCARD);
        } else {
            downloadPdf();
        }
    }

    private void downloadPdf() {
        if (!TextUtils.isEmpty(pdfUrl)) {
            DownloadContractManager manager = new DownloadContractManager(getContext(), pdfUrl,
                    new DownloadContractManager.onDownloadListener() {
                        @Override
                        public void finish(Uri uri) {
                            mPdfContract.fromUri(uri)
                                    .load();
                            pdfUri = uri;
                        }
                    });
            manager.downloadPdf();
            mCbSign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mPresenter.clickAgreement();
                        mPresenter.sign();
                    }
                }
            });
        }
    }

    @Override
    public void setSignEnable(boolean enable) {
        if (enable) {
            mFlySign.setVisibility(View.VISIBLE);
            mIvSettings.setVisibility(View.GONE);
        } else {
            mFlySign.setVisibility(View.GONE);
            mIvSettings.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showShare() {
        String shareText = mPresenter.getShareText();
        BaseAlertDialog dialog = new BaseAlertDialog(getContext(), "推荐好友", shareText, "分享得现金",
                new BaseAlertDialog.onButtonClickListener() {
                    @Override
                    public void sure() {
                        setResult(RESULT_OK, null);
                        finish();
                    }
                });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_SDCARD) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                downloadPdf();
            } else {
                showMessage("请允许读写sdcard权限，不然我们无法完pdf的加载");
            }
        }
    }

    public void openPdf() {
        mPresenter.clickDownloadAgreement();
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            //跳转
            startActivity(intent);
        } catch (Exception e) {
            showMessage("请确认是否有查看PDF格式的应用！");
            HHLog.e(e.getMessage());
        }
    }

    public void sendEmail() {
        mPresenter.clickSendAgreement();
        EnterEmailDialog dialog = new EnterEmailDialog(getContext(), new EnterEmailDialog.OnButtonClickListener() {
            @Override
            public void send(String email) {
                mPresenter.setAgreementEmail(email);
            }
        });
        dialog.show();
    }
}
