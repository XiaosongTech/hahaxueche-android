package com.hahaxueche.ui.activity.base;

import android.content.Intent;
import android.os.Bundle;

import com.hahaxueche.presenter.base.SplashPresenter;
import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.view.base.SplashView;
import com.hahaxueche.ui.view.login.CompleteUserInfoView;
import com.hahaxueche.util.HHLog;
import com.microquation.linkedme.android.LinkedME;
import com.microquation.linkedme.android.callback.LMReferralCloseListener;
import com.microquation.linkedme.android.callback.LMSimpleInitListener;
import com.microquation.linkedme.android.indexing.LMUniversalObject;
import com.microquation.linkedme.android.referral.LMError;
import com.microquation.linkedme.android.util.LinkProperties;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/9/8.
 */
public class SplashActivity extends HHBaseActivity implements SplashView {
    private SplashPresenter mPresenter;
    private LinkedME linkedME;

    public void onStart() {
        super.onStart();
        try {
            //如果消息未处理则会初始化initSession，因此不会每次都去处理数据，不会影响应用原有性能问题
            if (!LinkedME.getInstance().isHandleStatus()) {
                HHLog.i("LinkedME +++++++ initSession... " + this.getClass().getSimpleName());
                //初始化LinkedME实例
                linkedME = LinkedME.getInstance();
                //初始化Session，获取Intent内容及跳转参数
                linkedME.initSession(simpleInitListener, this.getIntent().getData(), this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPresenter.startApplication();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new SplashPresenter();
        mPresenter.attachView(this);
        ButterKnife.bind(this);
        MobclickAgent.setDebugMode(false);
    }

    @Override
    public void navigateToStartLogin() {
        startActivity(new Intent(getContext(), StartLoginActivity.class));
        SplashActivity.this.finish();
    }

    @Override
    public void navigateToCompleteInfo() {
        startActivity(new Intent(getContext(), CompleteUserInfoView.class));
        SplashActivity.this.finish();
    }

    @Override
    public void navigateToHomepage(Bundle bundle) {
        ActivityCollector.finishAll();
        Intent intent = new Intent(getContext(), MainActivity.class);
        if (bundle != null) {
            intent.putExtra("shareObject", bundle);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (linkedME != null) {
            linkedME.closeSession(new LMReferralCloseListener() {
                @Override
                public void onCloseFinish() {
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        simpleInitListener.reset();
        setIntent(intent);
    }

    /**
     * 解析深度链获取跳转参数，开发者自己实现参数相对应的页面内容
     * 通过LinkProperties对象调用getControlParams方法获取自定义参数的HashMap对象,
     * 通过创建的自定义key获取相应的值,用于数据处理。
     */
    LMSimpleInitListener simpleInitListener = new LMSimpleInitListener() {
        @Override
        public void onSimpleInitFinished(LMUniversalObject lmUniversalObject, LinkProperties linkProperties, LMError error) {
            try {
                HHLog.i("开始处理deep linking数据... " + this.getClass().getSimpleName());
                if (error != null) {
                    HHLog.e("LinkedME初始化失败. " + error.getMessage());
                } else {
                    //LinkedME SDK初始化成功，获取跳转参数，具体跳转参数在LinkProperties中，和创建深度链接时设置的参数相同；
                    HHLog.i("LinkedME初始化完成");
                    if (linkProperties != null) {
                        HHLog.i("Channel " + linkProperties.getChannel());
                        HHLog.i("control params " + linkProperties.getControlParams());
                        //获取自定义参数封装成的hashmap对象,参数键值对由集成方定义
                        HashMap<String, String> hashMap = linkProperties.getControlParams();
                        HHLog.v("linkProperties.getControlParams() -> " + hashMap.toString());
                        //根据key获取传入的参数的值,该key关键字View可为任意值,由集成方规定,请与web端商议,一致即可
                        String type = hashMap.get("type");
                        Bundle shareObject = new Bundle();
                        shareObject.putString("type", type);
                        if (type.equals("coach_detail")) {
                            //分享教练详情页
                            shareObject.putString("objectId", hashMap.get("coach_id"));
                            mPresenter.setShareObject(shareObject);
                        } else if (type.equals("training_partner_detail")) {
                            //分享陪练教练详情页
                            shareObject.putString("objectId", hashMap.get("training_partner_id"));
                            mPresenter.setShareObject(shareObject);
                        } else if (type.equals("article")) {
                            //分享小哈俱乐部文章
                            shareObject.putString("objectId", hashMap.get("id"));
                            mPresenter.setShareObject(shareObject);
                        } else if (type.equals("refer_record") || type.equals("test_practice") || type.equals("coach_list")) {
                            //分享推荐有奖，练题，教练列表
                            mPresenter.setShareObject(shareObject);
                        }
                    }

                    if (lmUniversalObject != null) {
                        HHLog.i("title " + lmUniversalObject.getTitle());
                        HHLog.i("control " + linkProperties.getControlParams());
                        HHLog.i("metadata " + lmUniversalObject.getMetadata());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
