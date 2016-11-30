package com.hahaxueche.ui.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.presenter.base.MainPresenter;
import com.hahaxueche.ui.activity.findCoach.CoachDetailActivity;
import com.hahaxueche.ui.activity.findCoach.PartnerDetailActivity;
import com.hahaxueche.ui.fragment.community.CommunityFragment;
import com.hahaxueche.ui.fragment.findCoach.FindCoachFragment;
import com.hahaxueche.ui.fragment.homepage.HomepageFragment;
import com.hahaxueche.ui.fragment.myPage.MyPageFragment;
import com.hahaxueche.ui.view.base.MainView;
import com.hahaxueche.ui.widget.FragmentTabHost;
import com.hahaxueche.util.HHLog;
import com.jauker.widget.BadgeView;

/**
 * Created by wangshirui on 16/9/15.
 */
public class MainActivity extends HHBaseActivity implements MainView {
    private MainPresenter mPresenter;
    private FragmentTabHost mTabHost = null;
    private View indicator = null;
    private View mViewBadgeMyPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MainPresenter();
        mPresenter.attachView(this);
        setTheme(R.style.AppThemeNoTitle);
        setContentView(R.layout.activity_main);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("homepage").setIndicator(getLayoutInflater().inflate(R.layout.indicator_homepage, null)),
                HomepageFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("findCoach").setIndicator(getLayoutInflater().inflate(R.layout.indicator_find_coach, null)),
                FindCoachFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("community").setIndicator(getLayoutInflater().inflate(R.layout.indicator_community, null)),
                CommunityFragment.class, null);

        View myPageIndicator = getLayoutInflater().inflate(R.layout.indicator_my_page, null);
        mViewBadgeMyPage = myPageIndicator.findViewById(R.id.view_badge_my_page);
        mTabHost.addTab(mTabHost.newTabSpec("myPage").setIndicator(myPageIndicator), MyPageFragment.class, null);

        mPresenter.viewHomepageCount();

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mPresenter.setMyPageBadge();
                switch (tabId) {
                    case "homepage":
                        mPresenter.viewHomepageCount();
                        break;
                    case "findCoach":
                        mPresenter.viewFindCoachCount();
                        break;
                    case "community":
                        mPresenter.viewCommunityCount();
                        break;
                    case "myPage":
                        mPresenter.viewMyPageCount();
                        break;
                    default:
                        break;
                }
            }
        });
        mTabHost.getTabWidget().setDividerDrawable(null);
        Intent intent = getIntent();
        Bundle shareObject = intent.getBundleExtra("shareObject");
        if (shareObject != null && !TextUtils.isEmpty(shareObject.getString("objectId", ""))) {
            if (shareObject.getString("type", "").equals("coach_detail")) {
                Intent startIntent = new Intent(getContext(), CoachDetailActivity.class);
                startIntent.putExtra("coach_id", shareObject.getString("objectId", ""));
                startActivity(startIntent);
            } else if (shareObject.getString("type", "").equals("training_partner_detail")) {
                Intent startIntent = new Intent(getContext(), PartnerDetailActivity.class);
                startIntent.putExtra("partnerId", shareObject.getString("objectId", ""));
                startActivity(startIntent);
            }
        }
        controlMyPageBadge();
    }

    public void controlMyPageBadge() {
        mPresenter.setMyPageBadge();
    }

    public void selectTab(int tab) {
        if (mTabHost != null) {
            mTabHost.setCurrentTab(tab);
        }
    }

    @Override
    public void setMyPageBadge(boolean hasBadge) {
        if (hasBadge) {
            mViewBadgeMyPage.setVisibility(View.VISIBLE);
            BadgeView badge = new BadgeView(getContext());
            badge.setTargetView(mViewBadgeMyPage);
            badge.setBadgeCount(1);
        } else {
            mViewBadgeMyPage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
        mTabHost = null;
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
