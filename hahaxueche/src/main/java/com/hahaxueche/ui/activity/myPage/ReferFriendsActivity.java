package com.hahaxueche.ui.activity.myPage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.presenter.myPage.ReferFriendsPresenter;
import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.dialog.BaseConfirmSimpleDialog;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.dialog.myPage.ReferDetailDialog;
import com.hahaxueche.ui.dialog.myPage.ShareReferDialog;
import com.hahaxueche.ui.view.myPage.ReferFriendsView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shaohui.shareutil.ShareUtil;
import me.shaohui.shareutil.share.ShareListener;
import me.shaohui.shareutil.share.SharePlatform;

/**
 * Created by wangshirui on 16/9/21.
 */

public class ReferFriendsActivity extends HHBaseActivity implements ReferFriendsView {
    @BindView(R.id.sv_main)
    ScrollView mSvMain;
    @BindView(R.id.tv_refer_double)
    TextView mTvReferDouble;
    private ReferFriendsPresenter mPresenter;
    /*****************
     * 分享
     ******************/
    private ShareReferDialog shareDialog;
    private String mTitle;
    private String mDescription;
    private String mImageUrl;
    private String mUrl;
    private String mShareSmsUrl;
    /*****************
     * end
     ******************/
    private ReferDetailDialog mReferDetailDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ReferFriendsPresenter();
        setContentView(R.layout.activity_refer_friends);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        String referDouble = mTvReferDouble.getText().toString();
        SpannableString spReferDouble = new SpannableString(referDouble);
        spReferDouble.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (mReferDetailDialog == null) {
                    mReferDetailDialog = new ReferDetailDialog(getContext(), true,
                            new ReferDetailDialog.OnButtonClickListener() {
                                @Override
                                public void callCustomerService() {
                                    createCallCustomerService();
                                }

                                @Override
                                public void onlineAsk() {
                                    mPresenter.onlineAsk();
                                }
                            });
                }
                mReferDetailDialog.show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getContext(), R.color.haha_red));
                ds.setUnderlineText(false);
                ds.clearShadowLayer();
            }
        }, referDouble.indexOf("详情"), referDouble.indexOf("详情") + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvReferDouble.setText(spReferDouble);
        mTvReferDouble.setHighlightColor(ContextCompat.getColor(getContext(), R.color.haha_red));
        mTvReferDouble.setMovementMethod(LinkMovementMethod.getInstance());
        Intent intent = getIntent();
        if (intent.getBooleanExtra("isFromLinkedMe", false)) {
            alertToLogin();
        }
    }


    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_refer_friends);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        TextView mTvWithdraw = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_withdraw);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("邀请好友 平分400元");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReferFriendsActivity.this.finish();
            }
        });
        mTvWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.clickWithdraw();
            }
        });
    }

    @Override
    public void initShareData(String shareUrl) {
        mTitle = "送你￥200元学车券，怕你考不过，再送你挂科险。比心 ❤";
        mDescription = "Hi~朋友，知道你最近想学车，我把我学车的地方告诉你了，要一把考过哟！️️";
        mImageUrl = "https://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
        mUrl = shareUrl;
        HHLog.v("mUrl -> " + mUrl);
    }

    @Override
    public void startToShare(int shareType, String shareUrl) {
        switch (shareType) {
            case 0:
                shareToWeixin(shareUrl);
                break;
            case 1:
                shareToFriendCircle(shareUrl);
                break;
            case 2:
                shareToQQ(shareUrl);
                break;
            case 3:
                shareToWeibo(shareUrl);
                break;
            case 4:
                shareToQZone(shareUrl);
                break;
            case 5:
                mShareSmsUrl = shareUrl;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, RequestCode.PERMISSIONS_REQUEST_SEND_SMS_FOR_SHARE);
                } else {
                    shareToSms(shareUrl);
                }
            default:
                break;
        }
    }

    @OnClick({R.id.tv_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_share:
                mPresenter.clickShareCount();
                break;
            default:
                break;
        }
    }

    private void shareToQQ(String shareUrl) {
        ShareUtil.shareMedia(this, SharePlatform.QQ, mTitle, mDescription, shareUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                mPresenter.clickShareSuccessCount("QQ_friend");
                showMessage("分享成功");
            }

            @Override
            public void shareFailure(Exception e) {
                showMessage("分享失败");
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                showMessage("取消分享");
            }
        });
    }

    private void shareToQZone(String shareUrl) {
        ShareUtil.shareMedia(this, SharePlatform.QZONE, mTitle, mDescription, shareUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                mPresenter.clickShareSuccessCount("qzone");
                showMessage("分享成功");
            }

            @Override
            public void shareFailure(Exception e) {
                showMessage("分享失败");
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                showMessage("取消分享");
            }
        });
    }

    private void shareToWeibo(String shareUrl) {
        ShareUtil.shareMedia(this, SharePlatform.WEIBO, mTitle, mDescription, shareUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                mPresenter.clickShareSuccessCount("weibo");
                showMessage("分享成功");
            }

            @Override
            public void shareFailure(Exception e) {
                showMessage("分享失败");
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                showMessage("取消分享");
            }
        });
    }

    private void shareToWeixin(String shareUrl) {
        ShareUtil.shareMedia(this, SharePlatform.WX, mTitle, mDescription, shareUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                mPresenter.clickShareSuccessCount("wechat_friend");
                showMessage("分享成功");
            }

            @Override
            public void shareFailure(Exception e) {
                showMessage("分享失败");
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                showMessage("取消分享");
            }
        });
    }

    private void shareToFriendCircle(String shareUrl) {
        ShareUtil.shareMedia(this, SharePlatform.WX_TIMELINE, mTitle, mDescription, shareUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                mPresenter.clickShareSuccessCount("wechat_friend_zone");
                showMessage("分享成功");
            }

            @Override
            public void shareFailure(Exception e) {
                showMessage("分享失败");
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                showMessage("取消分享");
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mSvMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToMyRefer() {
        startActivity(new Intent(getContext(), MyReferActivity.class));
    }

    @Override
    public void showShareDialog() {
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

    @Override
    public void alertToLogin() {
        BaseConfirmSimpleDialog dialog = new BaseConfirmSimpleDialog(getContext(), "提示", "请先登录或者注册", "去登录", "知道了",
                new BaseConfirmSimpleDialog.onClickListener() {
                    @Override
                    public void clickConfirm() {
                        ActivityCollector.finishAll();
                        Intent intent = new Intent(getContext(), StartLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void clickCancel() {

                    }
                });
        dialog.show();
    }

    private void createCallCustomerService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CUSTOMER_SERVICE);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            contactService();
        }
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
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CUSTOMER_SERVICE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactService();
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系客服");
            }
        } else if (requestCode == RequestCode.PERMISSIONS_REQUEST_SEND_SMS_FOR_SHARE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                shareToSms(mShareSmsUrl);
            } else {
                showMessage("请允许发送短信权限，不然无法分享到短信");
            }
        }
    }

    private void shareToSms(String shareUrl) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", "［哈哈学车］" + mDescription + shareUrl);
        startActivity(intent);
    }
}