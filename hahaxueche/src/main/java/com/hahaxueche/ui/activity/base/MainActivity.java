package com.hahaxueche.ui.activity.base;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.ui.fragment.community.CommunityFragment;
import com.hahaxueche.ui.fragment.findCoach.FindCoachFragment;
import com.hahaxueche.ui.fragment.homepage.HomepageFragment;
import com.hahaxueche.ui.fragment.myPage.MyPageFragment;

/**
 * Created by wangshirui on 16/9/15.
 */
public class MainActivity extends HHBaseActivity {
    private FragmentTabHost mTabHost = null;
    private View indicator = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeNoTitle);
        setContentView(R.layout.activity_main);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        indicator = getIndicatorView("哈哈学车", R.layout.indicator_homepage);
        mTabHost.addTab(mTabHost.newTabSpec("homepage").setIndicator(indicator), HomepageFragment.class, null);

        indicator = getIndicatorView("寻找教练", R.layout.indicator_find_coach);
        mTabHost.addTab(mTabHost.newTabSpec("findCoach").setIndicator(indicator), FindCoachFragment.class, null);

        indicator = getIndicatorView("小哈俱乐部", R.layout.indicator_community);
        mTabHost.addTab(mTabHost.newTabSpec("community").setIndicator(indicator), CommunityFragment.class, null);

        indicator = getIndicatorView("我的页面", R.layout.indicator_my_page);
        mTabHost.addTab(mTabHost.newTabSpec("myPage").setIndicator(indicator), MyPageFragment.class, null);
    }

    private View getIndicatorView(String name, int layoutId) {
        View v = getLayoutInflater().inflate(layoutId, null);
        TextView tv = (TextView) v.findViewById(R.id.tabText);
        tv.setText(name);
        return v;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabHost = null;
    }
}
