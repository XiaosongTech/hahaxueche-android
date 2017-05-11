package com.hahaxueche.ui.activity.myPage;

import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.presenter.myPage.MyContractPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.dialog.BaseAlertDialog;
import com.hahaxueche.ui.dialog.ShareAppDialog;
import com.hahaxueche.ui.dialog.myPage.EnterEmailDialog;
import com.hahaxueche.ui.dialog.myPage.MyContractDialog;
import com.hahaxueche.ui.view.myPage.MyContractView;
import com.hahaxueche.util.HHLog;
import com.lidong.pdf.PDFView;
import com.lidong.pdf.listener.OnDrawListener;
import com.lidong.pdf.listener.OnLoadCompleteListener;
import com.lidong.pdf.listener.OnPageChangeListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/29.
 */

public class MyContractActivity extends HHBaseActivity implements MyContractView, OnPageChangeListener,
        OnLoadCompleteListener, OnDrawListener {
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
    private String pdfUrl;
    private Uri pdfUri;
    //是否已显示过分享弹窗
    private boolean isShownShare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MyContractPresenter();
        setContentView(R.layout.activity_my_contract);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        Intent intent = getIntent();
        if (intent != null && !TextUtils.isEmpty(intent.getStringExtra("pdfUrl"))
                && !TextUtils.isEmpty(intent.getStringExtra("studentId"))) {
            setPdf(intent.getStringExtra("pdfUrl"), intent.getStringExtra("studentId"));
            mPresenter.addDataTrack("sign_contract_page_viewed", getContext());
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
                mPresenter.addDataTrack("my_contract_page_top_right_button_tapped", getContext());
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
    public void setPdf(String url, String studentId) {
        pdfUrl = url;
        mPdfContract.fileFromLocalStorage(this, this, this, pdfUrl, "contract_" + studentId + ".pdf");   //设置pdf文件地址
        mCbSign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPresenter.addDataTrack("sign_contract_check_box_checked", getContext());
                    mPresenter.sign();
                }
            }
        });
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
        ShareAppDialog shareDialog = new ShareAppDialog(getContext(), shareText, false,
                new ShareAppDialog.onShareClickListener() {
                    @Override
                    public void share() {
                        setResult(RESULT_OK, null);
                        finish();
                    }
                });
        shareDialog.show();
        isShownShare = true;
    }

    public void openPdf() {
        mPresenter.addDataTrack("my_contract_page_download_tapped", getContext());
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
        mPresenter.addDataTrack("my_contract_page_send_by_email_tapped", getContext());
        EnterEmailDialog dialog = new EnterEmailDialog(getContext(), new EnterEmailDialog.OnButtonClickListener() {
            @Override
            public void send(String email) {
                mPresenter.setAgreementEmail(email);
            }
        });
        dialog.show();
    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isShownShare) {
            return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }
}
