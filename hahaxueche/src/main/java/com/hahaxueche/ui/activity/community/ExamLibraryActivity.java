package com.hahaxueche.ui.activity.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.presenter.community.ExamPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.community.ExamLibraryPageAdapter;
import com.hahaxueche.ui.dialog.ShareAppDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class ExamLibraryActivity extends HHBaseActivity {
    ImageView mIvBack;
    TextView mTvTitle;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    private ShareAppDialog mShareDialog;
    private ExamPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ExamPresenter();
        setContentView(R.layout.activity_exam_library);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        ExamLibraryPageAdapter adapter = new ExamLibraryPageAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        mShareDialog = null;
        super.onDestroy();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExamLibraryActivity.this.finish();
            }
        });
        mTvTitle.setText("在线题库");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data.getBooleanExtra("isShowShare", false)) {
                if (mShareDialog == null) {
                    mShareDialog = new ShareAppDialog(getContext(), mPresenter.getBonus());
                }
                mShareDialog.show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
