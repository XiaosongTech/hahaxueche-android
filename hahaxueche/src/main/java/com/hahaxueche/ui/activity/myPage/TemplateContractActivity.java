package com.hahaxueche.ui.activity.myPage;

import android.Manifest;
import android.content.pm.PackageManager;
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

import com.github.barteksc.pdfviewer.PDFView;
import com.hahaxueche.R;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.base.HHBaseView;
import com.hahaxueche.util.DownloadContractManager;
import com.hahaxueche.util.RequestCode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/12/27.
 */

public class TemplateContractActivity extends HHBaseActivity implements HHBaseView {
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    @BindView(R.id.pdf_contract)
    PDFView mPdfContract;
    private String pdfUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_contract);
        ButterKnife.bind(this);
        initActionBar();
        setPdf("http://api.hahaxueche.net/share/students/agreement_template.pdf");
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

    public void setPdf(String url) {
        pdfUrl = url;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, RequestCode.PERMISSIONS_REQUEST_SDCARD);
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
                            mPdfContract.fromUri(uri).load();
                        }
                    });
            manager.downloadPdf();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_SDCARD) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                downloadPdf();
            } else {
                showMessage("请允许读写sdcard权限，不然我们无法完pdf的加载");
            }
        }
    }

    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }
}
