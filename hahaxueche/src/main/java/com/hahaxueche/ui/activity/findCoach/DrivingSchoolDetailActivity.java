package com.hahaxueche.ui.activity.findCoach;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.user.coach.ClassType;
import com.hahaxueche.model.user.coach.Review;
import com.hahaxueche.presenter.findCoach.DrivingSchoolDetailPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.homepage.MapSearchActivity;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.dialog.homepage.GetUserIdentityDialog;
import com.hahaxueche.ui.view.findCoach.DrivingSchoolDetailView;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;
import com.hahaxueche.util.Utils;
import com.hahaxueche.util.WebViewUrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shaohui.shareutil.ShareUtil;
import me.shaohui.shareutil.share.ShareListener;
import me.shaohui.shareutil.share.SharePlatform;

/**
 * Created by wangshirui on 2017/5/8.
 */

public class DrivingSchoolDetailActivity extends HHBaseActivity implements DrivingSchoolDetailView {
    private DrivingSchoolDetailPresenter mPresenter;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    @BindView(R.id.vw_click_more_comments)
    View mVwClickMoreComments;
    @BindView(R.id.tv_click_more_comments)
    TextView mTvClickMoreComments;
    @BindView(R.id.lly_comments)
    LinearLayout mLlyReviews;
    @BindView(R.id.iv_image)
    ImageView mIvImage;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_consultant_count)
    TextView mTvConsultantCount;
    @BindView(R.id.tv_lowest_price)
    TextView mTvLowestPrice;
    @BindView(R.id.tv_field_count)
    TextView mTvFieldCount;
    @BindView(R.id.tv_pass_rate)
    TextView mTvPassRate;
    @BindView(R.id.tv_satisfaction_rate)
    TextView mTvSatisfactionRate;
    @BindView(R.id.tv_coach_count)
    TextView mTvCoachCount;
    @BindView(R.id.tv_bio)
    TextView mTvBio;
    @BindView(R.id.lly_classes)
    LinearLayout mLlyClasses;
    @BindView(R.id.lly_fields)
    LinearLayout mLlyFields;
    @BindView(R.id.iv_bell)
    SimpleDraweeView mIvBell;
    @BindView(R.id.lly_details)
    LinearLayout mLlyDetails;
    @BindView(R.id.tv_more_bio)
    TextView mTvMoreBio;
    @BindView(R.id.et_get_group_buy)
    EditText mEtGetGroupBuy;
    @BindView(R.id.tv_comment_count)
    TextView mTvCommentCount;

    private int mReviewStartLine = 2;
    private int mClassStartLine = 2;
    private int mFieldStartLine = 2;

    private boolean mIsBioExpand = false;

    /*****************
     * 分享
     ******************/
    private ShareDialog shareDialog;
    private String mTitle;
    private String mDescription;
    private String mImageUrl;
    private String mUrl;
    private String mShareSmsUrl;

    /*****************
     * end
     ******************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new DrivingSchoolDetailPresenter();
        setContentView(R.layout.activity_driving_school_detail);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        Intent intent = getIntent();
        if (intent.getIntExtra("drivingSchoolId", -1) > 0) {
            mPresenter.getDrivingSchoolDetail(intent.getIntExtra("drivingSchoolId", -1));
        }
        Uri uri = Uri.parse("res://com.hahaxueche)/" + R.drawable.ic_bell);
        DraweeController draweeController =
                Fresco.newDraweeControllerBuilder()
                        .setUri(uri)
                        .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
                        .build();
        mIvBell.setController(draweeController);
        GenericDraweeHierarchy hierarchy = mIvBell.getHierarchy();
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        mLlyDetails.addView(getHotDrivingSchoolView(), mLlyDetails.getChildCount());
    }

    @Override
    public void initShareData(DrivingSchool drivingSchool) {
        mTitle = "哈哈学车-选驾校，挑教练，上哈哈学车";
        mDescription = "好友力荐:" + drivingSchool.name;
        mImageUrl = "https://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
        mUrl = WebViewUrl.WEB_URL_JIAXIAO + "/" + drivingSchool.id;
        HHLog.v("mUrl -> " + mUrl);
    }

    @Override
    public void setCommentCount(String text) {
        mTvCommentCount.setText(text);
    }

    @Override
    public void setGroupBuyPhone(String cellPhone) {
        mEtGetGroupBuy.setText(cellPhone);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base_share);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("驾校详情");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrivingSchoolDetailActivity.this.finish();
            }
        });
        ImageView mIvShare = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_share);
        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareDialog == null) {
                    shareDialog = new ShareDialog(getContext(), new ShareDialog.OnShareListener() {
                        @Override
                        public void onShare(int shareType) {
                            mPresenter.shortenUrl(mUrl, shareType);
                        }
                    });
                }
                shareDialog.show();
            }
        });
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

    private void shareToQQ(String shareUrl) {
        ShareUtil.shareMedia(this, SharePlatform.QQ, mTitle, mDescription, shareUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
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

    @OnClick({R.id.tv_near_fields,
            R.id.tv_more_fields,
            R.id.tv_click_more_fields,
            R.id.tv_more_bio,
            R.id.fly_sms_coach,
            R.id.fly_online_ask,
            R.id.fly_call_coach,
            R.id.tv_free_try,
            R.id.tv_more_comments,
            R.id.tv_click_more_comments,
            R.id.tv_get_group_buy,
            R.id.tv_notice_me})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_near_fields:
                mPresenter.addDataTrack("school_detail_check_fields_tapped", getContext());
                mPresenter.clickToFields();
                break;
            case R.id.tv_more_fields:
                mPresenter.addDataTrack("school_detail_more_fields_tapped", getContext());
                mPresenter.clickToFields();
                break;
            case R.id.tv_click_more_fields:
                mPresenter.addDataTrack("school_detail_more_fields_tapped", getContext());
                mPresenter.clickToFields();
                break;
            case R.id.tv_more_bio:
                clickMoreBio();
                break;
            case R.id.tv_free_try:
                mPresenter.addDataTrack("school_detail_bot_free_trial_tapped", getContext());
                GetUserIdentityDialog dialog = new GetUserIdentityDialog(getContext(), "看过训练场才放心！",
                        "输入手机号，教练立即带你看场地", "预约看场地", new GetUserIdentityDialog.OnIdentityGetListener() {
                    @Override
                    public void getCellPhone(String cellPhone) {
                        mPresenter.addDataTrack("school_detail_bot_free_trial_confirmed", getContext());
                        mPresenter.getUserIdentity(cellPhone);
                    }
                });
                dialog.show();
                break;
            case R.id.fly_sms_coach:
                mPresenter.addDataTrack("school_detail_bot_SMS_tapped", getContext());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, RequestCode.PERMISSIONS_REQUEST_SEND_SMS_TO_COACH);
                } else {
                    sendSmsToCoach();
                }
                break;
            case R.id.fly_online_ask:
                mPresenter.addDataTrack("school_detail_bot_online_support_tapped", getContext());
                mPresenter.onlineAsk();
                break;
            case R.id.fly_call_coach:
                mPresenter.addDataTrack("school_detail_bot_call_school_tapped", getContext());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CONTACT_COACH);
                } else {
                    callMyCoach();
                }
                break;
            case R.id.tv_more_comments:
                mPresenter.addDataTrack("school_detail_more_review_tapped", getContext());
                Intent intent = new Intent(getContext(), ReviewListActivity.class);
                intent.putExtra("drivingSchool", mPresenter.getDrivingSchool());
                startActivity(intent);
                break;
            case R.id.tv_click_more_comments:
                mPresenter.addDataTrack("school_detail_more_review_tapped", getContext());
                intent = new Intent(getContext(), ReviewListActivity.class);
                intent.putExtra("drivingSchool", mPresenter.getDrivingSchool());
                startActivity(intent);
                break;
            case R.id.tv_get_group_buy:
                mPresenter.addDataTrack("school_detail_groupon_web_tapped", getContext());
                mPresenter.getGroupBuy(mEtGetGroupBuy.getText().toString());
                break;
            case R.id.tv_notice_me:
                mPresenter.addDataTrack("school_detail_price_notification_tapped", getContext());
                dialog = new GetUserIdentityDialog(getContext(), "我们将为您保密个人信息！",
                        "填写手机号，立即订阅降价通知", "立即订阅", new GetUserIdentityDialog.OnIdentityGetListener() {
                    @Override
                    public void getCellPhone(String cellPhone) {
                        mPresenter.addDataTrack("school_detail_price_notification_confirmed", getContext());
                        mPresenter.getUserIdentity(cellPhone);
                    }
                });
                dialog.show();
            default:
                break;
        }
    }

    private void clickMoreBio() {
        if (mIsBioExpand) {
            mTvBio.setMaxLines(Integer.MAX_VALUE);
            mTvMoreBio.setText("收起");
        } else {
            mTvBio.setMaxLines(2);
            mTvMoreBio.setText("更多");
        }
        mIsBioExpand = !mIsBioExpand;
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showNoReview() {
        mVwClickMoreComments.setVisibility(View.GONE);
        mTvClickMoreComments.setText("该驾校目前还没有评价");
        mTvClickMoreComments.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        mTvClickMoreComments.setClickable(false);

    }

    @Override
    public void addReview(Review review) {
        mLlyReviews.addView(getReviewAdapter(review, mReviewStartLine == 2), mReviewStartLine++);
    }

    @Override
    public void setImage(Uri uri) {
        mIvImage.setImageURI(uri);
    }

    @Override
    public void setName(String drivingSchoolName) {
        mTvName.setText(drivingSchoolName);
    }

    @Override
    public void setConsultantCount(SpannableString text) {
        mTvConsultantCount.setText(text);
    }

    @Override
    public void setLowestPrice(SpannableString text) {
        mTvLowestPrice.setText(text);
    }

    @Override
    public void setFieldCount(SpannableString text) {
        mTvFieldCount.setText(text);
    }

    @Override
    public void setPassRate(SpannableString text) {
        mTvPassRate.setText(text);
    }

    @Override
    public void setSatisfactionRate(SpannableString text) {
        mTvSatisfactionRate.setText(text);
    }

    @Override
    public void setCoachCount(SpannableString text) {
        mTvCoachCount.setText(text);
    }

    @Override
    public void setBio(String bio) {
        mTvBio.setText(bio);
    }

    @Override
    public void addClassType(final ClassType classType) {
        int length20 = Utils.instence(this).dip2px(20);
        int length10 = Utils.instence(this).dip2px(10);
        int length2 = Utils.instence(this).dip2px(2);
        int length4 = Utils.instence(this).dip2px(4);

        View vwDivider = new View(this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.divider_width));
        viewParams.setMargins(length20, 0, 0, 0);
        vwDivider.setLayoutParams(viewParams);
        vwDivider.setBackgroundResource(R.color.haha_gray_divider);
        mLlyClasses.addView(vwDivider);

        RelativeLayout rly = new RelativeLayout(this);
        LinearLayout.LayoutParams rlyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rly.setLayoutParams(rlyParams);
        rly.setPadding(length20, length20, length20, length20);


        TextView tvClassTypeName = new TextView(this);
        tvClassTypeName.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tvClassTypeName.setBackgroundResource(R.drawable.rect_bg_trans_bd_appcolor_ssm);
        tvClassTypeName.setPadding(length4, length2, length4, length2);
        tvClassTypeName.setText(classType.name);
        tvClassTypeName.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
        tvClassTypeName.setTextSize(12);
        int tvClassTypeNameId = Utils.generateViewId();
        tvClassTypeName.setId(tvClassTypeNameId);
        rly.addView(tvClassTypeName);

        TextView tvPrice = new TextView(this);
        RelativeLayout.LayoutParams tvPriceParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvPriceParams.addRule(RelativeLayout.RIGHT_OF, tvClassTypeNameId);
        tvPriceParams.addRule(RelativeLayout.ALIGN_TOP, tvClassTypeNameId);
        tvPriceParams.setMargins(length10, length4, 0, 0);
        tvPrice.setLayoutParams(tvPriceParams);
        tvPrice.setText(Utils.getMoney(classType.price));
        tvPrice.setTextColor(ContextCompat.getColor(this, R.color.haha_orange));
        tvPrice.setTextSize(16);
        rly.addView(tvPrice);

        ImageView ivArrow = new ImageView(this);
        RelativeLayout.LayoutParams ivArrowParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ivArrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        ivArrowParams.addRule(RelativeLayout.ALIGN_BOTTOM, tvClassTypeNameId);
        ivArrow.setLayoutParams(ivArrowParams);
        ivArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_coachmsg_more_arrow));
        rly.addView(ivArrow);

        TextView tvClassTypeDesc = new TextView(this);
        RelativeLayout.LayoutParams tvClassTypeDescParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvClassTypeDescParams.addRule(RelativeLayout.BELOW, tvClassTypeNameId);
        tvClassTypeDescParams.setMargins(0, length10, 0, 0);
        tvClassTypeDesc.setLayoutParams(tvClassTypeDescParams);
        tvClassTypeDesc.setText(classType.desc);
        tvClassTypeDesc.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        int tvClassTypeDescId = Utils.generateViewId();
        tvClassTypeDesc.setId(tvClassTypeDescId);
        rly.addView(tvClassTypeDesc);

        TextView tvContact = new TextView(this);
        RelativeLayout.LayoutParams tvPurchaseParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvPurchaseParams.addRule(RelativeLayout.ALIGN_BOTTOM, tvClassTypeDescId);
        tvPurchaseParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        tvContact.setLayoutParams(tvPurchaseParams);
        tvContact.setBackgroundResource(R.drawable.rect_bg_orange_ssm);
        tvContact.setPadding(length10, length2, length10, length2);
        tvContact.setText("联系驾校");
        tvContact.setTextSize(12);
        tvContact.setTextColor(ContextCompat.getColor(this, R.color.haha_white));
        int tvPurchaseId = Utils.generateViewId();
        tvContact.setId(tvPurchaseId);
        rly.addView(tvContact);

        TextView tvNotice = new TextView(this);
        RelativeLayout.LayoutParams tvPrePayParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvPrePayParams.addRule(RelativeLayout.ALIGN_BOTTOM, tvPurchaseId);
        tvPrePayParams.addRule(RelativeLayout.LEFT_OF, tvPurchaseId);
        tvPrePayParams.setMargins(0, 0, length4, 0);
        tvNotice.setLayoutParams(tvPrePayParams);
        tvNotice.setBackgroundResource(R.drawable.rect_bg_appcolor_ssm);
        tvNotice.setPadding(length10, length2, length10, length2);
        tvNotice.setText("降价通知");
        tvNotice.setTextSize(12);
        tvNotice.setTextColor(ContextCompat.getColor(this, R.color.haha_white));
        rly.addView(tvNotice);

        rly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addDataTrack("school_detail_price_detail_tapped", getContext());
                Intent intent = new Intent(getContext(), ClassTypeIntroActivity.class);
                intent.putExtra("totalAmount", classType.price);
                intent.putExtra("isWuyouClass", false);
                intent.putExtra("classType", classType);
                intent.putExtra("drivingSchool", mPresenter.getDrivingSchool());
                startActivity(intent);
            }
        });

        tvNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetUserIdentityDialog dialog = new GetUserIdentityDialog(getContext(), "我们将为您保密个人信息！",
                        "填写手机号，立即订阅降价通知", "立即订阅", new GetUserIdentityDialog.OnIdentityGetListener() {
                    @Override
                    public void getCellPhone(String cellPhone) {
                        mPresenter.getUserIdentity(cellPhone);
                    }
                });
                dialog.show();
            }
        });

        tvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CONTACT_COACH);
                } else {
                    callMyCoach();
                }
            }
        });

        mLlyClasses.addView(rly);
    }

    @Override
    public void addFieldView(Field field) {
        mLlyFields.addView(getFieldAdapter(field, mFieldStartLine == 2), mFieldStartLine++);
    }

    @Override
    public void navigateToMapSearch(int drivingSchoolId) {
        Intent intent = new Intent(getContext(), MapSearchActivity.class);
        intent.putExtra("drivingSchoolId", drivingSchoolId);
        startActivity(intent);
    }

    /**
     * 联系教练
     */
    private void callMyCoach() {
        DrivingSchool drivingSchool = mPresenter.getDrivingSchool();
        if (TextUtils.isEmpty(drivingSchool.consult_phone))
            return;
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + drivingSchool.consult_phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_SEND_SMS_FOR_SHARE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                shareToSms(mShareSmsUrl);
            } else {
                showMessage("请允许发送短信权限，不然无法分享到短信");
            }
        } else if (requestCode == RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CONTACT_COACH) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                callMyCoach();
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系教练");
            }
        } else if (requestCode == RequestCode.PERMISSIONS_REQUEST_SEND_SMS_TO_COACH) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                sendSmsToCoach();
            } else {
                showMessage("请允许发送短信权限，不然给教练发短信");
            }
        }
    }

    private void shareToSms(String shareUrl) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", mTitle + mDescription + shareUrl);
        startActivity(intent);
    }

    private void sendSmsToCoach() {
        DrivingSchool drivingSchool = mPresenter.getDrivingSchool();
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + drivingSchool.consult_phone));
        intent.putExtra("sms_body", drivingSchool.name + "，我在哈哈学车看到您的招生信息，我想详细了解一下。");
        startActivity(intent);
    }

    private RelativeLayout getReviewAdapter(Review review, boolean isFirstLine) {
        RelativeLayout rly = new RelativeLayout(this);
        LinearLayout.LayoutParams rlyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rly.setLayoutParams(rlyParams);

        if (!isFirstLine) {
            View divider = new View(this);
            RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
            dividerParams.setMargins(Utils.instence(this).dip2px(20), 0, 0, 0);
            divider.setLayoutParams(dividerParams);
            divider.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
            rly.addView(divider);
        }

        SimpleDraweeView ivAvatar = new SimpleDraweeView(this);
        GenericDraweeHierarchy hierarchy = ivAvatar.getHierarchy();
        hierarchy.setPlaceholderImage(R.drawable.ic_mypage_ava);
        hierarchy.setRoundingParams(new RoundingParams().setRoundAsCircle(true));
        RelativeLayout.LayoutParams ivAvatarParams = new RelativeLayout.LayoutParams(Utils.instence(this).dip2px(40), Utils.instence(this).dip2px(40));
        ivAvatarParams.setMargins(Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15), 0, Utils.instence(this).dip2px(15));
        ivAvatar.setLayoutParams(ivAvatarParams);
        ivAvatar.setImageURI(review.reviewer.avatar);
        int ivAvatarId = Utils.generateViewId();
        ivAvatar.setId(ivAvatarId);
        rly.addView(ivAvatar);

        TextView tvName = new TextView(this);
        RelativeLayout.LayoutParams tvNameParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvNameParams.addRule(RelativeLayout.RIGHT_OF, ivAvatarId);
        tvNameParams.addRule(RelativeLayout.ALIGN_TOP, ivAvatarId);
        tvNameParams.setMargins(Utils.instence(this).dip2px(10), 0, 0, 0);
        tvName.setLayoutParams(tvNameParams);
        tvName.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
        tvName.setTextSize(18);
        tvName.setText(review.reviewer.name);
        int tvNameId = Utils.generateViewId();
        tvName.setId(tvNameId);
        rly.addView(tvName);

        TextView tvReviewDate = new TextView(this);
        RelativeLayout.LayoutParams tvReviewDateParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvReviewDateParams.addRule(RelativeLayout.ALIGN_BOTTOM, tvNameId);
        tvReviewDateParams.addRule(RelativeLayout.RIGHT_OF, tvNameId);
        tvReviewDateParams.setMargins(Utils.instence(this).dip2px(6), 0, 0, 0);
        tvReviewDate.setLayoutParams(tvReviewDateParams);
        tvReviewDate.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_text));
        tvReviewDate.setTextSize(12);
        tvReviewDate.setText(review.updated_at.substring(0, 10));
        rly.addView(tvReviewDate);

        ScoreView svScore = new ScoreView(this);
        RelativeLayout.LayoutParams svScoreParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        svScoreParams.addRule(RelativeLayout.ALIGN_BOTTOM, tvNameId);
        svScoreParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        svScoreParams.setMargins(0, 0, Utils.instence(this).dip2px(20), 0);
        svScore.setLayoutParams(svScoreParams);
        float reviewerRating = 0;
        if (!TextUtils.isEmpty(review.rating)) {
            reviewerRating = Float.parseFloat(review.rating);
        }
        if (reviewerRating > 5) {
            reviewerRating = 5;
        }
        svScore.setScore(reviewerRating, false);
        rly.addView(svScore);

        TextView tvComment = new TextView(this);
        RelativeLayout.LayoutParams tvCommentParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvCommentParams.addRule(RelativeLayout.ALIGN_LEFT, tvNameId);
        tvCommentParams.addRule(RelativeLayout.BELOW, tvNameId);
        tvCommentParams.setMargins(0, Utils.instence(this).dip2px(10), Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15));
        tvComment.setLayoutParams(tvCommentParams);
        tvComment.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        tvComment.setTextSize(12);
        tvComment.setText(review.comment);
        tvComment.setLineSpacing(0, 1.2f);
        tvComment.setMaxLines(2);
        tvComment.setEllipsize(TextUtils.TruncateAt.END);
        int tvCommentId = Utils.generateViewId();
        tvComment.setId(tvCommentId);
        rly.addView(tvComment);

        return rly;
    }

    private View getFieldAdapter(final Field field, boolean isFirstLine) {
        int length3 = Utils.instence(this).dip2px(3);
        int length5 = Utils.instence(this).dip2px(5);
        int length10 = Utils.instence(this).dip2px(10);
        int length15 = Utils.instence(this).dip2px(15);
        int avatarLength = Utils.instence(this).dip2px(60);

        RelativeLayout rly = new RelativeLayout(this);
        rly.setPadding(length15, 0, length15, 0);
        LinearLayout.LayoutParams rlyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rly.setLayoutParams(rlyParams);

        if (!isFirstLine) {
            View divider = new View(this);
            RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
            divider.setLayoutParams(dividerParams);
            divider.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
            rly.addView(divider);
        }

        SimpleDraweeView ivAvatar = new SimpleDraweeView(this);
        RelativeLayout.LayoutParams ivAvatarParams = new RelativeLayout.LayoutParams(avatarLength, avatarLength);
        ivAvatarParams.setMargins(0, length15, 0, length15);
        ivAvatar.setLayoutParams(ivAvatarParams);
        ivAvatar.setImageURI(field.image);
        int ivAvatarId = Utils.generateViewId();
        ivAvatar.setId(ivAvatarId);
        rly.addView(ivAvatar);

        TextView tvName = new TextView(this);
        RelativeLayout.LayoutParams tvNameParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvNameParam.addRule(RelativeLayout.RIGHT_OF, ivAvatarId);
        tvNameParam.addRule(RelativeLayout.ALIGN_TOP, ivAvatarId);
        tvNameParam.setMargins(length10, length5, 0, 0);
        tvName.setLayoutParams(tvNameParam);
        tvName.setText(field.name);
        tvName.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_dark));
        tvName.setMaxLines(1);
        tvName.setMaxWidth(getResources().getDimensionPixelSize(R.dimen.width_160dp));
        tvName.setEllipsize(TextUtils.TruncateAt.END);
        int tvNameId = Utils.generateViewId();
        tvName.setId(tvNameId);
        rly.addView(tvName);

        TextView tvToField = new TextView(this);
        RelativeLayout.LayoutParams tvToFieldParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvToFieldParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        tvToFieldParam.addRule(RelativeLayout.ALIGN_TOP, ivAvatarId);
        tvToField.setLayoutParams(tvToFieldParam);
        tvToField.setText("去现场看看 >");
        tvToField.setTextColor(ContextCompat.getColor(this, R.color.haha_white));
        tvToField.setBackgroundResource(R.drawable.rect_bg_yellow_gradient);
        tvToField.setPadding(length5, length5, length5, length5);
        tvToField.setTextSize(12);
        int tvToFieldId = Utils.generateViewId();
        tvToField.setId(tvToFieldId);
        rly.addView(tvToField);

        TextView tvLocation = new TextView(this);
        RelativeLayout.LayoutParams tvLocationParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvLocationParam.addRule(RelativeLayout.BELOW, tvToFieldId);
        tvLocationParam.addRule(RelativeLayout.ALIGN_LEFT, tvNameId);
        tvLocationParam.setMargins(0, length5, 0, 0);
        tvLocation.setLayoutParams(tvLocationParam);
        tvLocation.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_list_local_btn),
                null, null, null);
        tvLocation.setCompoundDrawablePadding(length3);
        tvLocation.setMaxLines(1);
        tvLocation.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        tvLocation.setTextSize(12);
        tvLocation.setEllipsize(TextUtils.TruncateAt.END);
        tvLocation.setText(field.zone + " | " + field.display_address);
        rly.addView(tvLocation);

        TextView tvSendLocation = new TextView(this);
        RelativeLayout.LayoutParams tvSendLocationParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvSendLocationParam.addRule(RelativeLayout.BELOW, tvToFieldId);
        tvSendLocationParam.addRule(RelativeLayout.ALIGN_RIGHT, tvToFieldId);
        tvSendLocationParam.setMargins(0, length5, 0, 0);
        tvSendLocation.setLayoutParams(tvSendLocationParam);
        tvSendLocation.setTextColor(ContextCompat.getColor(this, R.color.haha_blue));
        tvSendLocation.setTextSize(12);
        tvSendLocation.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvSendLocation.setText("发我定位");
        tvSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetUserIdentityDialog dialog = new GetUserIdentityDialog(getContext(), "轻松定位训练场",
                        "输入手机号，立即接收详细地址", "发我定位", new GetUserIdentityDialog.OnIdentityGetListener() {
                    @Override
                    public void getCellPhone(String cellPhone) {
                        mPresenter.sendLocation(cellPhone, field);
                    }
                });
                dialog.show();
            }
        });
        rly.addView(tvSendLocation);

        rly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addDataTrack("school_detail_single_field_tapped", getContext());
                mPresenter.clickToFields();
            }
        });
        tvToField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addDataTrack("school_detail_see_field_tapped", getContext());
                GetUserIdentityDialog dialog = new GetUserIdentityDialog(getContext(), "看过训练场才放心！",
                        "输入手机号，教练立即带你看场地", "预约看场地", new GetUserIdentityDialog.OnIdentityGetListener() {
                    @Override
                    public void getCellPhone(String cellPhone) {
                        mPresenter.addDataTrack("school_detail_see_field_confirmed", getContext());
                        mPresenter.getUserIdentity(cellPhone);
                    }
                });
                dialog.show();
            }
        });

        return rly;
    }

    private LinearLayout getHotDrivingSchoolView() {
        int margin5dp = Utils.instence(this).dip2px(5);
        int margin8dp = Utils.instence(this).dip2px(8);
        int margin10dp = Utils.instence(this).dip2px(10);
        int margin15dp = Utils.instence(this).dip2px(15);
        int margin20dp = Utils.instence(this).dip2px(20);
        int padding3dp = Utils.instence(this).dip2px(3);

        LinearLayout llyHotDrivingSchool = new LinearLayout(this);
        LinearLayout.LayoutParams llyHotDrivingSchoolParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llyHotDrivingSchoolParam.setMargins(0, margin10dp, 0, margin10dp);
        llyHotDrivingSchool.setLayoutParams(llyHotDrivingSchoolParam);
        llyHotDrivingSchool.setBackgroundResource(R.color.haha_white);
        llyHotDrivingSchool.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout rlyHotSearch = new RelativeLayout(this);
        rlyHotSearch.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView tvHotSearch = new TextView(this);
        RelativeLayout.LayoutParams tvHotSearchParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvHotSearchParam.setMargins(margin20dp, margin15dp, 0, margin15dp);
        tvHotSearch.setLayoutParams(tvHotSearchParam);
        tvHotSearch.setText("大家都在搜");
        tvHotSearch.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_dark));
        tvHotSearch.setTextSize(16);
        int tvHotSearchId = Utils.generateViewId();
        tvHotSearch.setId(tvHotSearchId);
        rlyHotSearch.addView(tvHotSearch);
        TextView tvHot = new TextView(this);
        RelativeLayout.LayoutParams tvHotParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvHotParam.addRule(RelativeLayout.RIGHT_OF, tvHotSearchId);
        tvHotParam.setMargins(margin5dp, margin10dp, 0, 0);
        tvHot.setLayoutParams(tvHotParam);
        tvHot.setText("hot!");
        tvHot.setTextColor(ContextCompat.getColor(this, R.color.haha_red));
        rlyHotSearch.addView(tvHot);
        llyHotDrivingSchool.addView(rlyHotSearch);

        View vwDivider = new View(this);
        vwDivider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                this.getResources().getDimensionPixelSize(R.dimen.divider_width)));
        vwDivider.setBackgroundResource(R.color.haha_gray_divider);
        llyHotDrivingSchool.addView(vwDivider);

        TableLayout tbDrivingSchool = new TableLayout(this);
        LinearLayout.LayoutParams tbDrivingSchoolParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tbDrivingSchoolParam.setMargins(0, 0, 0, margin15dp);
        tbDrivingSchool.setLayoutParams(tbDrivingSchoolParam);
        tbDrivingSchool.setStretchAllColumns(true);

        int maxColCount = 4;
        List<DrivingSchool> hotDrivingSchoolList = mPresenter.getHotDrivingSchools(this);
        for (int row = 0; row < hotDrivingSchoolList.size() / maxColCount; row++) {
            TableRow tr = new TableRow(this);
            TableLayout.LayoutParams trParam = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            trParam.setMargins(0, margin10dp, 0, 0);
            tr.setLayoutParams(trParam);
            for (int col = 0; col < maxColCount; col++) {
                if (row * maxColCount + col > hotDrivingSchoolList.size() - 1) {
                    break;
                }
                final int position = row * maxColCount + col;
                final DrivingSchool drivingSchool = hotDrivingSchoolList.get(position);
                TextView tvDrivingSchool = new TextView(this);
                TableRow.LayoutParams tvDrivingSchoolParam = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                tvDrivingSchoolParam.setMargins(margin8dp, 0, margin8dp, 0);
                tvDrivingSchool.setLayoutParams(tvDrivingSchoolParam);
                tvDrivingSchool.setBackgroundResource(R.drawable.rect_bg_gray_bd_gray_corner);
                tvDrivingSchool.setGravity(Gravity.CENTER);
                tvDrivingSchool.setPadding(0, padding3dp, 0, padding3dp);
                tvDrivingSchool.setText(drivingSchool.name);
                tvDrivingSchool.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
                tvDrivingSchool.setTextSize(12);
                tvDrivingSchool.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //事件纪录
                        HashMap<String, String> map = new HashMap();
                        map.put("index", String.valueOf(position));
                        mPresenter.addDataTrack("school_detail_hot_school_tapped", getContext(), map);
                        Intent intent = new Intent(getContext(), DrivingSchoolDetailActivity.class);
                        intent.putExtra("drivingSchoolId", drivingSchool.id);
                        startActivity(intent);
                    }
                });
                tr.addView(tvDrivingSchool);
            }
            tbDrivingSchool.addView(tr);
        }
        llyHotDrivingSchool.addView(tbDrivingSchool);
        return llyHotDrivingSchool;
    }
}
