package com.hahaxueche.ui.activity.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.user.User;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.community.ExamLibraryPageAdapter;
import com.hahaxueche.ui.adapter.community.InsurancePageAdapter;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

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
    @BindView(R.id.viewPager_insurance)
    ViewPager mViewPagerInsurance;
    @BindView(R.id.tabLayout_insurance)
    TabLayout mTabLayoutInsurance;
    @BindView(R.id.sv_main)
    ScrollView mSvMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_library);
        ButterKnife.bind(this);
        initActionBar();
        ExamLibraryPageAdapter adapter = new ExamLibraryPageAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        InsurancePageAdapter insurancePageAdapter = new InsurancePageAdapter(getSupportFragmentManager(), this);
        mViewPagerInsurance.setAdapter(insurancePageAdapter);
        mTabLayoutInsurance.setupWithViewPager(mViewPagerInsurance);
        mTabLayoutInsurance.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayoutInsurance.setTabMode(TabLayout.MODE_FIXED);

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
        mTvTitle.setText("赔付宝");
    }

    public void showMessage(String message) {
        Snackbar.make(mSvMain, message, Snackbar.LENGTH_SHORT).show();
    }

    public void finishToFindCoach() {
        Intent intent = new Intent();
        intent.putExtra("toFindCoach", true);
        setResult(RESULT_OK, intent);
        finish();
    }
}
