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
import com.hahaxueche.presenter.myPage.StudentReferPresenter;
import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.dialog.BaseConfirmSimpleDialog;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.dialog.myPage.ReferDetailDialog;
import com.hahaxueche.ui.dialog.myPage.ShareReferDialog;
import com.hahaxueche.ui.view.myPage.StudentReferView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shaohui.shareutil.ShareUtil;
import me.shaohui.shareutil.share.ShareListener;
import me.shaohui.shareutil.share.SharePlatform;

/**
 * Created by wangshirui on 2017/2/18.
 */

public class StudentReferActivity extends HHBaseActivity implements StudentReferView {
    @BindView(R.id.sv_main)
    ScrollView mSvMain;
    @BindView(R.id.tv_refer_secret)
    TextView mTvReferSecret;
    private StudentReferPresenter mPresenter;
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
    private ReferDetailDialog mReferDetailDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new StudentReferPresenter();
        setContentView(R.layout.activity_student_refer);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        String referDouble = mTvReferSecret.getText().toString();
        SpannableString spReferDouble = new SpannableString(referDouble);
        spReferDouble.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (mReferDetailDialog == null) {
                    mReferDetailDialog = new ReferDetailDialog(getContext(), false,
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
        mTvReferSecret.setText(spReferDouble);
        mTvReferSecret.setHighlightColor(ContextCompat.getColor(getContext(), R.color.haha_red));
        mTvReferSecret.setMovementMethod(LinkMovementMethod.getInstance());
        Intent intent = getIntent();
        if (intent.getBooleanExtra("isFromLinkedMe", false)) {
            alertToLogin();
        }
    }


    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_student_refer);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvReferList = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_refer_list);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentReferActivity.this.finish();
            }
        });
        mTvReferList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MyReferActivity.class));
            }
        });
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

    @Override
    public void initShareData(String shareUrl) {
        mTitle = "送你￥200元学车券，怕你考不过，再送你挂科险。比心 ❤";
        mDescription = "Hi~朋友，知道你最近想学车，我把我学车的地方告诉你了，要一把考过哟！️️";
        mImageUrl = "https://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
        mUrl = shareUrl;
        HHLog.v("mUrl -> " + mUrl);
    }

    @Override
    public void showShareDialog() {
        if (mPresenter.isLogin()) {
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
        } else {
            alertToLogin();
        }
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
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, RequestCode.PERMISSIONS_REQUEST_SEND_SMS);
                } else {
                    shareToSms();
                }
            default:
                break;
        }
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

    private void shareToQQ() {
        ShareUtil.shareMedia(this, SharePlatform.QQ, mTitle, mDescription, mUrl, mImageUrl, new ShareListener() {
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

    private void shareToQZone() {
        ShareUtil.shareMedia(this, SharePlatform.QZONE, mTitle, mDescription, mUrl, mImageUrl, new ShareListener() {
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

    private void shareToWeibo() {
        ShareUtil.shareMedia(this, SharePlatform.WEIBO, mTitle, mDescription, mUrl, mImageUrl, new ShareListener() {
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

    private void shareToWeixin() {
        ShareUtil.shareMedia(this, SharePlatform.WX, mTitle, mDescription, mUrl, mImageUrl, new ShareListener() {
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

    private void shareToFriendCircle() {
        ShareUtil.shareMedia(this, SharePlatform.WX_TIMELINE, mTitle, mDescription, mUrl, mImageUrl, new ShareListener() {
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

    private void shareToSms() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", "［哈哈学车］" + mDescription + mUrl);
        startActivity(intent);
    }

    private void createCallCustomerService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RequestCode.PERMISSIONS_REQUEST_CELL_PHONE);
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
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_CELL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactService();
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系客服");
            }
        } else if (requestCode == RequestCode.PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                shareToSms();
            } else {
                showMessage("请允许发送短信权限，不然无法分享到短信");
            }
        }
    }

}
