package com.hahaxueche.ui.activity.myPage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.hahaxueche.R;
import com.hahaxueche.presenter.myPage.MyContractPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.myPage.MyContractView;
import com.hahaxueche.util.DownloadContractManager;

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
            DownloadContractManager manager = new DownloadContractManager(getContext(), intent.getStringExtra("pdfUrl"),
                    new DownloadContractManager.onDownloadListener() {
                        @Override
                        public void finish(Uri uri) {
                            mPdfContract.fromUri(uri)
                                    .load();
                        }
                    });
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
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
        mPdfContract.fromUri(Uri.parse(url))
                .load();
    }

    @Override
    public void setSignEnable(boolean enable) {
        mFlySign.setVisibility(enable ? View.VISIBLE : View.GONE);
    }
}
