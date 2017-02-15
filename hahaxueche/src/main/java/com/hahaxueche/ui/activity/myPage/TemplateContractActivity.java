package com.hahaxueche.ui.activity.myPage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.base.HHBaseView;
import com.hahaxueche.util.DownloadContractManager;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;
import com.lidong.pdf.PDFView;
import com.lidong.pdf.listener.OnDrawListener;
import com.lidong.pdf.listener.OnLoadCompleteListener;
import com.lidong.pdf.listener.OnPageChangeListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/12/27.
 */

public class TemplateContractActivity extends HHBaseActivity implements HHBaseView, OnPageChangeListener,
        OnLoadCompleteListener, OnDrawListener {
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    @BindView(R.id.pdf_contract)
    PDFView mPdfContract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_contract);
        ButterKnife.bind(this);
        initActionBar();
        mPdfContract.fileFromLocalStorage(this, this, this,
                "http://api.hahaxueche.net/share/students/agreement_template.pdf", "template.pdf");
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("协议模板");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TemplateContractActivity.this.finish();
            }
        });
    }

    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
        HHLog.v("onLayerDrawn");
    }

    @Override
    public void loadComplete(int nbPages) {
        HHLog.v("loadComplete");
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        HHLog.v("loadComplete");
    }
}
