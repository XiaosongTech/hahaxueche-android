package com.hahaxueche.ui.activity.base;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.presenter.base.MainPresenter;
import com.hahaxueche.ui.activity.community.ArticleActivity;
import com.hahaxueche.ui.activity.community.ExamLibraryActivity;
import com.hahaxueche.ui.activity.findCoach.CoachDetailActivity;
import com.hahaxueche.ui.activity.findCoach.PaySuccessActivity;
import com.hahaxueche.ui.activity.myPage.MyContractActivity;
import com.hahaxueche.ui.activity.myPage.MyInsuranceActivity;
import com.hahaxueche.ui.activity.myPage.MyReferActivity;
import com.hahaxueche.ui.activity.myPage.PurchaseInsuranceActivity;
import com.hahaxueche.ui.activity.myPage.ReferFriendsActivity;
import com.hahaxueche.ui.activity.myPage.StudentReferActivity;
import com.hahaxueche.ui.activity.myPage.UploadIdCardActivity;
import com.hahaxueche.ui.dialog.BaseConfirmSimpleDialog;
import com.hahaxueche.ui.dialog.MainShareDialog;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.dialog.myPage.ShareReferDialog;
import com.hahaxueche.ui.fragment.community.CommunityFragment;
import com.hahaxueche.ui.fragment.findCoach.FindCoachFragment;
import com.hahaxueche.ui.fragment.homepage.HomepageFragment;
import com.hahaxueche.ui.fragment.myPage.MypageFragment;
import com.hahaxueche.ui.view.base.MainView;
import com.hahaxueche.ui.widget.FragmentTabHost;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;

import java.util.HashMap;

import butterknife.BindView;
import me.shaohui.shareutil.ShareUtil;
import me.shaohui.shareutil.share.ShareListener;
import me.shaohui.shareutil.share.SharePlatform;

/**
 * Created by wangshirui on 16/9/15.
 */
public class MainActivity extends HHBaseActivity implements MainView {
    private MainPresenter mPresenter;
    private FragmentTabHost mTabHost = null;
    private View mViewBadgeMyPage;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    /*****************
     * 分享
     ******************/
    private ShareReferDialog shareDialog;
    private String mTitle;
    private String mDescription;
    private String mImageUrl;
    private String mUrl;

    /*****************
     * end
     ******************/

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
        mTabHost.addTab(mTabHost.newTabSpec("myPage").setIndicator(myPageIndicator), MypageFragment.class, null);

        mPresenter.addDataTrack("home_page_viewed", getContext());

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mPresenter.setMyPageBadge();
                switch (tabId) {
                    case "homepage":
                        mPresenter.addDataTrack("home_page_viewed", getContext());
                        break;
                    case "findCoach":
                        mPresenter.addDataTrack("find_coach_page_viewed", getContext());
                        break;
                    case "community":
                        mPresenter.addDataTrack("club_page_viewed", getContext());
                        break;
                    case "myPage":
                        mPresenter.addDataTrack("my_page_viewed", getContext());
                        break;
                    default:
                        break;
                }
            }
        });
        mTabHost.getTabWidget().setDividerDrawable(null);
        //LinkedME分享的东西
        Intent intent = getIntent();
        Bundle shareObject = intent.getBundleExtra("shareObject");
        if (shareObject != null) {
            if (!TextUtils.isEmpty(shareObject.getString("objectId", ""))) {
                if (shareObject.getString("type", "").equals("coach_detail")) {
                    Intent startIntent = new Intent(getContext(), CoachDetailActivity.class);
                    startIntent.putExtra("coach_id", shareObject.getString("objectId", ""));
                    startActivity(startIntent);
                } else if (shareObject.getString("type", "").equals("article")) {
                    Intent startIntent = new Intent(getContext(), ArticleActivity.class);
                    startIntent.putExtra("articleId", shareObject.getString("objectId", ""));
                    startActivity(startIntent);
                }
            } else if (shareObject.getString("type", "").equals("refer_record")) {
                if (shareObject.getBoolean("isLogin", false)) {
                    Intent startIntent = new Intent(getContext(), MyReferActivity.class);
                    startActivity(startIntent);
                } else {
                    Intent startIntent = new Intent(getContext(), StudentReferActivity.class);
                    startIntent.putExtra("isFromLinkedMe", true);
                    startActivity(startIntent);
                }
            } else if (shareObject.getString("type", "").equals("test_practice")) {
                Intent startIntent = new Intent(getContext(), ExamLibraryActivity.class);
                startActivityForResult(startIntent, RequestCode.REQUEST_CODE_EXAM_LIBRARY);
            } else if (shareObject.getString("type", "").equals("coach_list")) {
                selectTab(1);
            } else if (shareObject.getString("type", "").equals("peifubao")) {
                startActivityForResult(new Intent(getContext(), MyInsuranceActivity.class), RequestCode.REQUEST_CODE_MY_INSURANCE);
            }
        }
        controlMyPageBadge();
        mPresenter.controlSignDialog();
    }

    @Override
    public void initShareData(String shareUrl) {
        mTitle = "送你￥200元学车券，怕你考不过，再送你一张挂科险。比心 ❤";
        mDescription = "Hi~朋友，知道你最近想学车，我把我学车的地方告诉你了，要一把考过哟！️️";
        mImageUrl = "https://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
        mUrl = shareUrl;
        HHLog.v("mUrl -> " + mUrl);
    }

    @Override
    public void startToShare(int shareType) {
        switch (shareType) {
            case 0:
                shareToWeixin();
                break;
            case 1:
                shareToFriendCircle();
                break;
            case 2:
                shareToQQ();
                break;
            case 3:
                shareToWeibo();
                break;
            case 4:
                shareToQZone();
                break;
            case 5:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, RequestCode.PERMISSIONS_REQUEST_SEND_SMS_FOR_SHARE);
                } else {
                    shareToSms();
                }
            default:
                break;
        }
    }

    @Override
    public void navigateToStudentRefer() {
        startActivity(new Intent(getContext(), StudentReferActivity.class));
    }

    @Override
    public void navigateToReferFriends() {
        startActivity(new Intent(getContext(), ReferFriendsActivity.class));
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
        } else {
            mViewBadgeMyPage.setVisibility(View.GONE);
        }
    }

    @Override
    public void showSignDialog() {
        BaseConfirmSimpleDialog dialog = new BaseConfirmSimpleDialog(getContext(), "友情提醒", "快去上传资料签署专属学员协议吧！",
                "去上传", "取消", new BaseConfirmSimpleDialog.onClickListener() {
            @Override
            public void clickConfirm() {
                mPresenter.clickMyContract();
            }

            @Override
            public void clickCancel() {

            }
        });
        dialog.show();
    }

    @Override
    public void navigateToUploadIdCard() {
        startActivityForResult(new Intent(getContext(), UploadIdCardActivity.class), RequestCode.REQUEST_CODE_UPLOAD_ID_CARD);
    }

    @Override
    public void navigateToSignContract() {
        startActivityForResult(new Intent(getContext(), MyContractActivity.class), RequestCode.REQUEST_CODE_MY_CONTRACT);
    }

    @Override
    public void navigateToMyContract() {
        startActivityForResult(new Intent(getContext(), MyContractActivity.class), RequestCode.REQUEST_CODE_MY_CONTRACT);
    }

    @Override
    public void showVoucherDialog(final String studentId, Voucher voucher) {
        MainShareDialog dialog = new MainShareDialog(getContext(), voucher,
                new MainShareDialog.OnButtonClickListener() {
                    @Override
                    public void shareToFriends() {
                        mPresenter.addDataTrack("home_page_voucher_popup_share_tapped", getContext());
                        if (TextUtils.isEmpty(mUrl)) return;
                        if (shareDialog == null) {
                            shareDialog = new ShareReferDialog(getContext(), mUrl, new ShareDialog.OnShareListener() {
                                @Override
                                public void onShare(int shareType) {
                                    mPresenter.convertUrlForShare(mUrl, shareType);
                                }
                            });
                        }
                        shareDialog.show();
                    }
                });
        dialog.show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.REQUEST_CODE_UPLOAD_ID_CARD) {
            if (resultCode == RESULT_OK) {
                controlMyPageBadge();
                mPresenter.toReferFriends();
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_MY_CONTRACT) {
            if (resultCode == RESULT_OK) {//已签订协议
                controlMyPageBadge();
                mPresenter.toReferFriends();
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_EXAM_LIBRARY) {
            if (resultCode == RESULT_OK && null != data) {
                if (data.getBooleanExtra("toFindCoach", false)) {
                    selectTab(1);
                }
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_MY_INSURANCE) {
            if (resultCode == RESULT_OK && null != data) {
                if (data.getBooleanExtra("toUploadInfo", false)) {
                    Intent intent = new Intent(getContext(), UploadIdCardActivity.class);
                    intent.putExtra("isFromPaySuccess", false);
                    intent.putExtra("isInsurance", true);
                    startActivityForResult(intent, RequestCode.REQUEST_CODE_UPLOAD_ID_CARD);
                } else if (data.getBooleanExtra("toFindCoach", false)) {
                    selectTab(1);
                } else {
                    Intent intent = new Intent(getContext(), PurchaseInsuranceActivity.class);
                    intent.putExtra("insuranceType", data.getIntExtra("insuranceType", Common.PURCHASE_INSURANCE_TYPE_WITHOUT_COACH));
                    startActivityForResult(intent, RequestCode.REQUEST_CODE_PURCHASE_INSURANCE);
                }
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_PURCHASE_INSURANCE) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(getContext(), PaySuccessActivity.class);
                intent.putExtra("isPurchasedInsurance", true);
                intent.putExtra("isFromPurchaseInsurance", true);
                startActivityForResult(intent, RequestCode.REQUEST_CODE_PAY_SUCCESS);
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_PAY_SUCCESS) {
            Intent intent = new Intent(getContext(), UploadIdCardActivity.class);
            intent.putExtra("isFromPaySuccess", false);
            intent.putExtra("isInsurance", true);
            startActivityForResult(intent, RequestCode.REQUEST_CODE_UPLOAD_ID_CARD);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void shareToQQ() {
        ShareUtil.shareMedia(this, SharePlatform.QQ, mTitle, mDescription, mUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                HashMap<String, String> map = new HashMap();
                map.put("share_channel", "QQ_friend");
                mPresenter.addDataTrack("home_page_voucher_popup_share_succeed", getContext(), map);
                Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void shareFailure(Exception e) {
                Toast.makeText(MainActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                Toast.makeText(MainActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareToQZone() {
        ShareUtil.shareMedia(this, SharePlatform.QZONE, mTitle, mDescription, mUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                HashMap<String, String> map = new HashMap();
                map.put("share_channel", "qzone");
                mPresenter.addDataTrack("home_page_voucher_popup_share_succeed", getContext(), map);
                Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void shareFailure(Exception e) {
                Toast.makeText(MainActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                Toast.makeText(MainActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareToWeibo() {
        ShareUtil.shareMedia(this, SharePlatform.WEIBO, mTitle, mDescription, mUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                HashMap<String, String> map = new HashMap();
                map.put("share_channel", "weibo");
                mPresenter.addDataTrack("home_page_voucher_popup_share_succeed", getContext(), map);
                Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void shareFailure(Exception e) {
                Toast.makeText(MainActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                Toast.makeText(MainActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareToWeixin() {
        ShareUtil.shareMedia(this, SharePlatform.WX, mTitle, mDescription, mUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                HashMap<String, String> map = new HashMap();
                map.put("share_channel", "wechat_friend");
                mPresenter.addDataTrack("home_page_voucher_popup_share_succeed", getContext(), map);
                Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void shareFailure(Exception e) {
                Toast.makeText(MainActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                Toast.makeText(MainActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareToFriendCircle() {
        ShareUtil.shareMedia(this, SharePlatform.WX_TIMELINE, mTitle, mDescription, mUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                HashMap<String, String> map = new HashMap();
                map.put("share_channel", "wechat_friend_zone");
                mPresenter.addDataTrack("home_page_voucher_popup_share_succeed", getContext(), map);
                Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void shareFailure(Exception e) {
                Toast.makeText(MainActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                Toast.makeText(MainActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_SEND_SMS_FOR_SHARE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                shareToSms();
            } else {
                Toast.makeText(MainActivity.this, "请允许发送短信权限，不然无法分享到短信", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareToSms() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", "［哈哈学车］" + mDescription + mUrl);
        startActivity(intent);
    }

    public void onCityChange() {
        FindCoachFragment findCoachFragment = (FindCoachFragment) getSupportFragmentManager().findFragmentByTag("findCoach");
        if (findCoachFragment != null) {
            findCoachFragment.onCityChange();
        }
    }
}
