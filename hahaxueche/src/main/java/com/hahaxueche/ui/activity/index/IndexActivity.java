package com.hahaxueche.ui.activity.index;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.google.gson.reflect.TypeToken;
import com.hahaxueche.R;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.model.util.ConstantsModel;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.ui.activity.appointment.AppointmentActivity;
import com.hahaxueche.ui.activity.base.BaseWebViewActivity;
import com.hahaxueche.ui.activity.findCoach.CoachDetailActivity;
import com.hahaxueche.ui.activity.findCoach.FindCoachActivity;
import com.hahaxueche.ui.activity.mySetting.MySettingActivity;
import com.hahaxueche.ui.dialog.CityChoseDialog;
import com.hahaxueche.ui.widget.bannerView.NetworkImageHolderView;
import com.hahaxueche.utils.JsonUtils;
import com.hahaxueche.utils.Util;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by gibxin on 2016/1/27.
 */
public class IndexActivity extends IndexBaseActivity implements AdapterView.OnItemClickListener, ViewPager.OnPageChangeListener,
        OnItemClickListener {
    private LinearLayout llyTabIndex;
    private LinearLayout llyTabFindCoach;
    private LinearLayout llyTabAppointment;
    private LinearLayout llyTabMySetting;
    private CityChoseDialog mCityChoseDialog;
    private ConvenientBanner cbannerIndex;
    private List<String> networkImages;
    private ArrayAdapter transformerArrayAdapter;
    private ArrayList<String> transformerList = new ArrayList<String>();
    private LinearLayout llyXiaohaMore;
    private LinearLayout llyCoachMore;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;
    //声明定位回调监听器
    private AMapLocationListener mLocationListener;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private double mLat;
    private double mLng;
    private TextView tvOneKeyFindCoach;
    private ProgressDialog pd;//进度框

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        initView();
        initEvent();
        SharedPreferences sharedPreferences = getSharedPreferences("session", Activity.MODE_PRIVATE);
        String city_id = sharedPreferences.getString("city_id", "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //游客没有city_id，需选择
        if (TextUtils.isEmpty(city_id)) {
            editor.putString("city_id", "0");//默认武汉
            editor.commit();
            mCityChoseDialog = new CityChoseDialog(this,
                    new CityChoseDialog.OnBtnClickListener() {
                        @Override
                        public void onCitySelected(String cityName, String cityId) {
                            mCityChoseDialog.dismiss();
                            SharedPreferences sharedPreferences = getSharedPreferences("session", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("city_id", cityId);
                            editor.commit();
                        }
                    });
            //mCityChoseDialog.show();
        }
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        mLat = aMapLocation.getLatitude();//获取纬度
                        mLng = aMapLocation.getLongitude();//获取经度
                        SharedPreferences sharedPreferences = getSharedPreferences("session", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("lat", mLat + "");
                        editor.putString("lng", mLng + "");
                        editor.commit();
                        if (mLat != 0d && mLng != 0d) {
                            mLocationClient.stopLocation();
                        }
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        };
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private void initView() {
        llyTabIndex = Util.instence(this).$(this, R.id.lly_tab_index);
        llyTabFindCoach = Util.instence(this).$(this, R.id.lly_tab_find_coach);
        llyTabAppointment = Util.instence(this).$(this, R.id.lly_tab_appointment);
        llyTabMySetting = Util.instence(this).$(this, R.id.lly_tab_my_setting);
        llyCoachMore = Util.instence(this).$(this, R.id.lly_coach_more);
        llyXiaohaMore = Util.instence(this).$(this, R.id.lly_xiaoha_more);
        tvOneKeyFindCoach = Util.instence(this).$(this, R.id.tv_onekey_find_coach);
        cbannerIndex = (ConvenientBanner) findViewById(R.id.indexBanner);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, width);
        cbannerIndex.setLayoutParams(p);
        transformerArrayAdapter = new ArrayAdapter(this, R.layout.adapter_transformer, transformerList);
        //网络加载例子
        SharedPreferences spConstants = getSharedPreferences("constants", Activity.MODE_PRIVATE);
        String constantsStr = spConstants.getString("constants", "");
        Type type = new TypeToken<ConstantsModel>() {
        }.getType();
        ConstantsModel constantsModel = JsonUtils.deserialize(constantsStr, type);
        if (constantsModel != null) {
            networkImages = constantsModel.getHome_page_banners();
        }
        cbannerIndex.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
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
    }

    private void initEvent() {
        llyTabIndex.setOnClickListener(mClickListener);
        llyTabFindCoach.setOnClickListener(mClickListener);
        llyTabAppointment.setOnClickListener(mClickListener);
        llyTabMySetting.setOnClickListener(mClickListener);
        llyCoachMore.setOnClickListener(mClickListener);
        llyXiaohaMore.setOnClickListener(mClickListener);
        tvOneKeyFindCoach.setOnClickListener(mClickListener);
    }


    View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lly_tab_find_coach:
                    Intent intent = new Intent(getApplication(), FindCoachActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.lly_tab_appointment:
                    intent = new Intent(getApplication(), AppointmentActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.lly_tab_my_setting:
                    intent = new Intent(getApplication(), MySettingActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.lly_xiaoha_more:
                    //Uri uri = Uri.parse("http://staging.hahaxueche.net/#/student");
                    //Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    intent = new Intent(getApplication(), BaseWebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("url", "http://staging.hahaxueche.net/#/student");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.lly_coach_more:
                    //uri = Uri.parse("http://staging.hahaxueche.net/#/coach");
                    //it = new Intent(Intent.ACTION_VIEW, uri);
                    intent = new Intent(getApplication(), BaseWebViewActivity.class);
                    bundle = new Bundle();
                    bundle.putString("url", "http://staging.hahaxueche.net/#/coach");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.tv_onekey_find_coach:
                    if (pd != null) {
                        pd.dismiss();
                    }
                    pd = ProgressDialog.show(IndexActivity.this, null, "教练寻找中，请稍后……");
                    fcPresenter.oneKeyFindCoach(mLat + "", mLng + "", new FCCallbackListener<CoachModel>() {
                        @Override
                        public void onSuccess(CoachModel data) {
                            if (pd != null) {
                                pd.dismiss();
                            }
                            if (data != null && !TextUtils.isEmpty(data.getId())) {
                                Intent intent = new Intent(getApplication(), CoachDetailActivity.class);
                                intent.putExtra("coach_id", data.getId());
                                startActivity(intent);
                            } else {
                                Toast.makeText(IndexActivity.this, "未找到合适的教练", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(String errorEvent, String message) {
                            if (pd != null) {
                                pd.dismiss();
                            }
                            Toast.makeText(IndexActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);

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
            cbannerIndex.getViewPager().setPageTransformer(true, transforemer);

            //部分3D特效需要调整滑动速度
            if (transforemerName.equals("StackTransformer")) {
                cbannerIndex.setScrollDuration(1200);
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

    protected void onResume() {

        super.onResume();

        //开始自动翻页

        cbannerIndex.startTurning(2500);

    }


    // 停止自动翻页

    @Override

    protected void onPause() {

        super.onPause();

        //停止翻页

        cbannerIndex.stopTurning();

    }


}
