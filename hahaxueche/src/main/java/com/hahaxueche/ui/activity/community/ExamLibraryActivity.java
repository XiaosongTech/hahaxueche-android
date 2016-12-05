package com.hahaxueche.ui.activity.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.community.ExamLibraryPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.community.ExamLibraryPageAdapter;
import com.hahaxueche.ui.dialog.ShareAppDialog;
import com.hahaxueche.ui.view.community.ExamLibraryView;
import com.hahaxueche.util.HHLog;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class ExamLibraryActivity extends HHBaseActivity implements ExamLibraryView {
    ImageView mIvBack;
    TextView mTvTitle;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.iv_pass)
    ImageView mIvPass;
    @BindView(R.id.lly_not_login)
    LinearLayout mLlyNotLogin;
    @BindView(R.id.lly_not_purchase)
    LinearLayout mLlyNotPurchase;
    @BindView(R.id.lly_scores)
    LinearLayout mLlyScores;
    @BindView(R.id.tv_insurance_count)
    TextView mTvInsuranceCount;
    @BindView(R.id.iv_score1)
    ImageView mIvScore1;
    @BindView(R.id.iv_score2)
    ImageView mIvScore2;
    @BindView(R.id.iv_score3)
    ImageView mIvScore3;
    @BindView(R.id.iv_score4)
    ImageView mIvScore4;
    @BindView(R.id.iv_score5)
    ImageView mIvScore5;
    @BindView(R.id.tv_pass_score_text)
    TextView mTvPassScoreText;
    private ShareAppDialog mShareDialog;
    private ExamLibraryPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ExamLibraryPresenter();
        setContentView(R.layout.activity_exam_library);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        ExamLibraryPageAdapter adapter = new ExamLibraryPageAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        HHBaseApplication application = HHBaseApplication.get(getContext());
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(getContext(), "online_test_page_viewed", map);
        } else {
            MobclickAgent.onEvent(getContext(), "online_test_page_viewed");
        }
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
        mTvTitle.setText("科一保过");
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
        } else if (requestCode == 2) {
            mPresenter.fetchScoures();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showNotLogin() {
        mIvPass.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.protectioncard_noget));
        mLlyNotLogin.setVisibility(View.VISIBLE);
        mLlyNotPurchase.setVisibility(View.GONE);
        mLlyScores.setVisibility(View.GONE);
    }

    @Override
    public void showNotPurchase() {
        mIvPass.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.protectioncard_get));
        mLlyNotLogin.setVisibility(View.GONE);
        mLlyNotPurchase.setVisibility(View.VISIBLE);
        mLlyScores.setVisibility(View.GONE);
    }

    @Override
    public void showScores(int passCount) {
        mIvPass.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.protectioncard_get));
        mLlyNotLogin.setVisibility(View.GONE);
        mLlyNotPurchase.setVisibility(View.GONE);
        mLlyScores.setVisibility(View.VISIBLE);
        mIvScore1.setImageDrawable(ContextCompat.getDrawable(this, passCount > 0 ? R.drawable.ic_hahapass1 : R.drawable.ic_nopass1));
        mIvScore2.setImageDrawable(ContextCompat.getDrawable(this, passCount > 1 ? R.drawable.ic_hahapass2 : R.drawable.ic_nopass2));
        mIvScore3.setImageDrawable(ContextCompat.getDrawable(this, passCount > 2 ? R.drawable.ic_hahapass3 : R.drawable.ic_nopass3));
        mIvScore4.setImageDrawable(ContextCompat.getDrawable(this, passCount > 3 ? R.drawable.ic_hahapass4 : R.drawable.ic_nopass4));
        mIvScore5.setImageDrawable(ContextCompat.getDrawable(this, passCount > 4 ? R.drawable.ic_hahapass5 : R.drawable.ic_nopass5));
        if (passCount > 0) {
            mTvPassScoreText.setText("您已在" + (passCount > 5 ? 5 : passCount) + "次模拟考试中获得90分以上的成绩。");
        }
    }

    @Override
    public void setInsuranceCount(SpannableString ss) {
        mTvInsuranceCount.setText(ss);
    }
}
