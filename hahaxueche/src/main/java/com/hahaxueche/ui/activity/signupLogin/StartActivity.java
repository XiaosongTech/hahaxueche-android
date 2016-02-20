package com.hahaxueche.ui.activity.signupLogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.hahaxueche.R;
import com.hahaxueche.ui.fragment.index.IndexActivity;
import com.hahaxueche.ui.widget.bannerView.NetworkImageHolderView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gibxin on 2016/1/19.
 */
public class StartActivity extends SLBaseActivity implements AdapterView.OnItemClickListener, ViewPager.OnPageChangeListener, OnItemClickListener {
    private ConvenientBanner convenientBanner;//顶部广告栏控件
    private List<String> networkImages;
    private String[] images = {"http://img.sgamer.com/dota2_sgamer_com/images/20141219/bca3930007de96526e5367618d2a51bf.png",
            "http://img.sgamer.com/dota2_sgamer_com/images/20141219/a2ac302699ccc818f2d5017a447cb770.png",
            "http://img.sgamer.com/dota2_sgamer_com/images/20140419/23abc29bada61a2e0ea9245ab58b8c94.jpg",
            "http://img.sgamer.com/dota2_sgamer_com/images/20141219/a876c9ace286e3ad9e6a23f35b64b484.png"
    };
    private ArrayAdapter transformerArrayAdapter;
    private ArrayList<String> transformerList = new ArrayList<String>();
    private TextView tvIsTour;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_start);
        convenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);
        transformerArrayAdapter = new ArrayAdapter(this,R.layout.adapter_transformer,transformerList);
        //网络加载例子
        networkImages= Arrays.asList(images);
        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        },networkImages)
                .setPageIndicator(new int[]{R.drawable.icon_point, R.drawable.icon_point_pre})
                        //设置指示器的方向
//                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
//                .setOnPageChangeListener(this)//监听翻页事件
                .setOnItemClickListener(this);
        tvIsTour = (TextView)findViewById(R.id.tv_is_tourist);
        tvIsTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, IndexActivity.class);
                startActivity(intent);
                StartActivity.this.finish();
            }
        });

    }

    /**
     * 登录
     * @param view
     */
    public void startLogin(View view){
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * 注册
     * @param view
     */
    public void startSignUp(View view){
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
            ABaseTransformer transforemer= (ABaseTransformer)cls.newInstance();
            convenientBanner.getViewPager().setPageTransformer(true,transforemer);

            //部分3D特效需要调整滑动速度
            if(transforemerName.equals("StackTransformer")){
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
}
