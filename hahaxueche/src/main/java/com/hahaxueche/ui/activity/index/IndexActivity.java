package com.hahaxueche.ui.activity.index;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Banner;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.ui.activity.appointment.AppointmentActivity;
import com.hahaxueche.ui.activity.findCoach.FindCoachActivity;
import com.hahaxueche.ui.activity.mySetting.MySettingActivity;
import com.hahaxueche.ui.dialog.AppointmentDialog;
import com.hahaxueche.ui.dialog.BaseAlertDialog;
import com.hahaxueche.ui.dialog.CityChoseDialog;
import com.hahaxueche.ui.dialog.GroupBuyDialog;
import com.hahaxueche.ui.widget.bannerView.NetworkImageHolderView;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.UpdateManager;
import com.hahaxueche.utils.Util;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;

import java.util.ArrayList;
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
    private RelativeLayout mRlyAboutHaha;//关于小哈
    private RelativeLayout mRlyAboutCoach;//关于教练
    private RelativeLayout mRlyMyStrengths;//我的优势
    private RelativeLayout mRlyProcedure;//学车流程
    private List<String> networkImages;
    private ArrayAdapter transformerArrayAdapter;
    private ArrayList<String> transformerList = new ArrayList<String>();
    private double mLat;
    private double mLng;
    private TextView mTvFreeTry;
    private ProgressDialog pd;//进度框
    private SharedPreferencesUtil spUtil;
    private Constants mConstants;
    private User mUser;
    private GroupBuyDialog mGroupBuyDialog;
    private AppointmentDialog appointmentDialog;
    private FrameLayout mFrlTelAsk;
    private FrameLayout mFrlOnlineAsk;
    private static final String WEB_URL_ABOUT_HAHA = "http://staging.hahaxueche.net/#/student";
    private static final String WEB_URL_ABOUT_COACH = "http://staging.hahaxueche.net/#/coach";
    private static final String WEB_URL_MY_STRENGTHS = "http://activity.hahaxueche.com/share/features";
    private static final String WEB_URL_PROCEDURE = "http://activity.hahaxueche.com/share/steps";
    private static final int PERMISSIONS_REQUEST_CELL_PHONE = 601;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        spUtil = new SharedPreferencesUtil(this);
        mConstants = spUtil.getConstants();
        mUser = spUtil.getUser();
        initView();
        initEvent();
        //游客没有city_id，需选择
        if (null == mUser) {
            mUser = new User();
        }
        if (null == mUser.getStudent()) {
            mUser.setStudent(new Student());
        }
        if (TextUtils.isEmpty(mUser.getStudent().getCity_id())) {
            mUser.getStudent().setCity_id("0");
            spUtil.setUser(mUser);
            mCityChoseDialog = new CityChoseDialog(this,
                    new CityChoseDialog.OnBtnClickListener() {
                        @Override
                        public void onCitySelected(String cityName, String cityId) {
                            mCityChoseDialog.dismiss();
                            mUser.getStudent().setCity_id(cityId);
                            spUtil.setUser(mUser);
                        }
                    });
            mCityChoseDialog.show();
        }
        if (!TextUtils.isEmpty(spUtil.getRefererId())) {
            showFirstBonusAlert();
        }
        doAutoVersionCheck();
    }

    private void initView() {
        llyTabIndex = Util.instence(this).$(this, R.id.lly_tab_index);
        llyTabFindCoach = Util.instence(this).$(this, R.id.lly_tab_find_coach);
        llyTabAppointment = Util.instence(this).$(this, R.id.lly_tab_appointment);
        llyTabMySetting = Util.instence(this).$(this, R.id.lly_tab_my_setting);
        mRlyAboutHaha = Util.instence(this).$(this, R.id.rly_about_haha);
        mRlyAboutCoach = Util.instence(this).$(this, R.id.rly_about_coach);
        mRlyMyStrengths = Util.instence(this).$(this, R.id.rly_my_strengths);
        mRlyProcedure = Util.instence(this).$(this, R.id.rly_procedure);
        mTvFreeTry = Util.instence(this).$(this, R.id.tv_free_try);
        cbannerIndex = (ConvenientBanner) findViewById(R.id.indexBanner);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = Math.round(width / 5 * 4);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(width, height);
        cbannerIndex.setLayoutParams(p);
        transformerArrayAdapter = new ArrayAdapter(this, R.layout.adapter_transformer, transformerList);
        //网络加载例子
        if (mConstants != null && mConstants.getNew_home_page_banners() != null) {
            networkImages = new ArrayList<>();
            for (Banner banner : mConstants.getNew_home_page_banners()) {
                networkImages.add(banner.getImage_url());
            }
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
        cbannerIndex.notifyDataSetChanged();
        mFrlTelAsk = Util.instence(this).$(this, R.id.frl_tel_ask);
        mFrlOnlineAsk = Util.instence(this).$(this, R.id.frl_online_ask);
    }

    private void initEvent() {
        llyTabIndex.setOnClickListener(mClickListener);
        llyTabFindCoach.setOnClickListener(mClickListener);
        llyTabAppointment.setOnClickListener(mClickListener);
        llyTabMySetting.setOnClickListener(mClickListener);
        mTvFreeTry.setOnClickListener(mClickListener);
        mRlyAboutHaha.setOnClickListener(mClickListener);
        mRlyAboutCoach.setOnClickListener(mClickListener);
        mRlyMyStrengths.setOnClickListener(mClickListener);
        mRlyProcedure.setOnClickListener(mClickListener);
        mFrlOnlineAsk.setOnClickListener(mClickListener);
        mFrlTelAsk.setOnClickListener(mClickListener);
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
                case R.id.tv_free_try:
                    freeTry();
                    break;
                case R.id.rly_about_haha:
                    openWebView(WEB_URL_ABOUT_HAHA);
                    break;
                case R.id.rly_about_coach:
                    openWebView(WEB_URL_ABOUT_COACH);
                    break;
                case R.id.rly_my_strengths:
                    openWebView(WEB_URL_MY_STRENGTHS);
                    break;
                case R.id.rly_procedure:
                    openWebView(WEB_URL_PROCEDURE);
                    break;
                case R.id.frl_online_ask:
                    onlineAsk(IndexActivity.this);
                    break;
                case R.id.frl_tel_ask:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CELL_PHONE);
                        //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                    } else {
                        // Android version is lesser than 6.0 or the permission is already granted.
                        contactService();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onItemClick(int i) {
        if (mConstants != null && mConstants.getNew_home_page_banners() != null &&
                !TextUtils.isEmpty(mConstants.getNew_home_page_banners().get(i).getTarget_url())) {
            openWebView(mConstants.getNew_home_page_banners().get(i).getTarget_url());
        }
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

    public void onResume() {
        super.onResume();
        //开始自动翻页
        cbannerIndex.startTurning(2500);
    }


    // 停止自动翻页

    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        cbannerIndex.stopTurning();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 第一次加载推荐有奖通知
     */
    public void showFirstBonusAlert() {
//        if (!spUtil.getNoticeBouns() && mUser.getStudent() != null && !TextUtils.isEmpty(mUser.getId())) {
//            BaseAlertDialog baseAlertDialog = new BaseAlertDialog(IndexActivity.this, "注册成功！", "恭喜您获得50元学车卷！", "50元已经打进您的账户余额，在支付过程中，系统会自动减现50元报名费。");
//            baseAlertDialog.show();
//            spUtil.setNoticeBonus(true);
//        }
    }


    private void freeTry() {
        //免费试学URL
        String url = "http://m.hahaxueche.com/free_trial";
        if (spUtil.getUser() != null && spUtil.getUser().getStudent() != null) {
            if (!TextUtils.isEmpty(spUtil.getUser().getStudent().getCity_id())) {
                url += "?city_id=" + spUtil.getUser().getStudent().getCity_id();
            }
            if (!TextUtils.isEmpty(spUtil.getUser().getStudent().getName())) {
                if (url.indexOf("?") > 0) {
                    url += "&name=" + spUtil.getUser().getStudent().getName();
                } else {
                    url += "?name=" + spUtil.getUser().getStudent().getName();
                }
            }
            if (!TextUtils.isEmpty(spUtil.getUser().getStudent().getCell_phone())) {
                if (url.indexOf("?") > 0) {
                    url += "&phone=" + spUtil.getUser().getStudent().getCell_phone();
                } else {
                    url += "?phone=" + spUtil.getUser().getStudent().getCell_phone();
                }
            }

        }
        Log.v("gibxin", "free try url -> " + url);
        openWebView(url);
    }

    /**
     * 版本检测
     */
    private void doAutoVersionCheck() {
        PackageManager pm = this.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(this.getPackageName(), 0);
            int versioncode = pi.versionCode;
            SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
            Constants constants = spUtil.getConstants();
            if (constants != null && constants.getVersion_code() > versioncode) {
                //有版本更新时
                UpdateManager updateManager = new UpdateManager(IndexActivity.this);
                updateManager.checkUpdateInfo();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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

    /**
     * 联系客服
     */
    private void contactService() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4000016006"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CELL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactService();
            } else {
                Toast.makeText(this, "请允许拨打电话权限，不然无法直接拨号联系客服", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
