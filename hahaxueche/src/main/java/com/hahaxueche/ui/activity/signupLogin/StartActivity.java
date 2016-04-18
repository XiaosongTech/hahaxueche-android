package com.hahaxueche.ui.activity.signupLogin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.hahaxueche.R;
import com.hahaxueche.model.student.StudentModel;
import com.hahaxueche.model.base.ConstantsModel;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.ui.widget.bannerView.NetworkImageHolderView;
import com.hahaxueche.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gibxin on 2016/1/19.
 */
public class StartActivity extends SLBaseActivity implements AdapterView.OnItemClickListener, ViewPager.OnPageChangeListener, OnItemClickListener {
    private ConvenientBanner convenientBanner;//顶部广告栏控件
    private List<String> networkImages;
    //private String[] images = {"http://haha-staging.oss-cn-shanghai.aliyuncs.com/images%2Fbanner01.jpg" };
    private ArrayAdapter transformerArrayAdapter;
    private ArrayList<String> transformerList = new ArrayList<String>();
    private TextView tvIsTour;
    private ImageView ivBack;
    private SharedPreferencesUtil spUtil;
    private ConstantsModel mConstants;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_start);
        spUtil = new SharedPreferencesUtil(this);
        mConstants = spUtil.getConstants();
        convenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, width);
        convenientBanner.setLayoutParams(p);
        transformerArrayAdapter = new ArrayAdapter(this, R.layout.adapter_transformer, transformerList);
        if (mConstants != null) {
            networkImages = mConstants.getLogin_banners();
        }
        //网络加载例子
        //networkImages= Arrays.asList(images);
        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, networkImages)
                .setPageIndicator(new int[]{R.drawable.icon_point, R.drawable.icon_point_pre})
                        //设置指示器的方向
//                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
//                .setOnPageChangeListener(this)//监听翻页事件
                .setOnItemClickListener(this);
        tvIsTour = (TextView) findViewById(R.id.tv_is_tourist);
        tvIsTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空session
                spUtil.clearCurrentCoach();
                spUtil.clearStudent();
                spUtil.clearSession();
                //游客登录，虚拟用户
                StudentModel fakeStudent = new StudentModel();
                spUtil.setStudent(fakeStudent);
                Intent intent = new Intent(context, IndexActivity.class);
                startActivity(intent);
                StartActivity.this.finish();
            }
        });
        ivBack = (ImageView) findViewById(R.id.iv_back);
        //是否显示回退按钮
        String canBack = getIntent().getStringExtra("isBack");
        if (!TextUtils.isEmpty(canBack)) {
            ivBack.setVisibility(View.VISIBLE);
            ivBack.setClickable(true);
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StartActivity.this.finish();
                }
            });
        } else {
            ivBack.setVisibility(View.GONE);
            ivBack.setClickable(false);
        }
    }

    /**
     * 登录
     *
     * @param view
     */
    public void startLogin(View view) {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * 注册
     *
     * @param view
     */
    public void startSignUp(View view) {
        Intent intent = new Intent(context, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int i) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String transforemerName = transformerList.get(position);
        try {
            Class cls = Class.forName("com.ToxicBakery.viewpager.transforms." + transforemerName);
            ABaseTransformer transforemer = (ABaseTransformer) cls.newInstance();
            convenientBanner.getViewPager().setPageTransformer(true, transforemer);

            //部分3D特效需要调整滑动速度
            if (transforemerName.equals("StackTransformer")) {
                convenientBanner.setScrollDuration(1200);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
    // 开始自动翻页

    @Override

    public void onResume() {
        super.onResume();
        //开始自动翻页
        convenientBanner.startTurning(2500);
    }



    // 停止自动翻页

    @Override

    public void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();

    }
}
