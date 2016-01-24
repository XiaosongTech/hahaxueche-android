package com.hahaxueche.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.ui.fragment.appointment.AppointmentFragment;
import com.hahaxueche.ui.fragment.findCoach.FindCoachFrgment;
import com.hahaxueche.ui.fragment.index.IndexFragment;
import com.hahaxueche.ui.fragment.mySetting.MySettingFragment;

/**
 * 底部导航栏基本Fragment
 * Created by gibxin on 2016/1/24.
 */
public class BaseBottomTabFragment extends FragmentActivity {
    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    //定义一个布局
    private LayoutInflater layoutInflater;
    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {IndexFragment.class, FindCoachFrgment.class, AppointmentFragment.class, MySettingFragment.class};
    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.tab_index_btn, R.drawable.tab_find_coach_btn,
            R.drawable.tab_appointment_btn, R.drawable.tab_my_setting_btn};
    //Tab选项卡的文字
    private String mTextviewArray[] = {"哈哈学车", "寻找教练", "预约学车", "我的页面"};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_base_bottom_tab);
        initView();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        //得到fragment的个数
        int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            //设置Tab按钮的背景
            //mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.view_tab_item, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }
}
