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
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.myPage.MyCoachDetailPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.findCoach.ClassTypeIntroActivity;
import com.hahaxueche.ui.activity.findCoach.CoachDetailActivity;
import com.hahaxueche.ui.activity.findCoach.FieldMapActivity;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.view.myPage.MyCoachDetailView;
import com.hahaxueche.ui.widget.imageSwitcher.ImageSwitcher;
import com.hahaxueche.util.RequestCode;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shaohui.shareutil.ShareUtil;
import me.shaohui.shareutil.share.ShareListener;
import me.shaohui.shareutil.share.SharePlatform;

/**
 * Created by wangshirui on 2016/10/26.
 */

public class MyCoachDetailActivity extends HHBaseActivity implements MyCoachDetailView {
    private MyCoachDetailPresenter mPresenter;
    @BindView(R.id.sv_main)
    ScrollView mSvMain;
    @BindView(R.id.tv_coach_name)
    TextView mTvName;
    @BindView(R.id.tv_description)
    TextView mTvDescription;
    @BindView(R.id.iv_coach_avatar)
    SimpleDraweeView mIvAvatar;
    @BindView(R.id.is_coach_images)
    ImageSwitcher mIsImages;
    @BindView(R.id.rly_info_line)
    RelativeLayout mRlyInfoLine;
    @BindView(R.id.tv_phone)
    TextView mTvCoachPhone;
    @BindView(R.id.tv_train_location)
    TextView mTvTrainLocation;
    @BindView(R.id.lly_train_school)
    LinearLayout mLlyTrainSchool;
    @BindView(R.id.tv_train_school)
    TextView mTvTrainSchoolName;
    @BindView(R.id.lly_peer_coaches)
    LinearLayout mLlyPeerCoaches;
    @BindView(R.id.iv_follow)
    ImageView mIvFollow;
    @BindView(R.id.tv_applaud_count)
    TextView mTvApplaud;
    @BindView(R.id.tv_course_name)
    TextView mTvCourseName;
    /*****************
     * 分享
     ******************/
    private ShareDialog shareDialog;
    private HHBaseApplication myApplication;
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
        mPresenter = new MyCoachDetailPresenter();
        setContentView(R.layout.activity_my_coach_detail);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        Intent intent = getIntent();
        String coach_id = intent.getStringExtra("coachId");
        if (!TextUtils.isEmpty(coach_id)) {
            mPresenter.setCoach(coach_id);
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base_share);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("我的教练");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyCoachDetailActivity.this.finish();
            }
        });
        ImageView mIvShare = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_share);
        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.clickShareCount();
                if (shareDialog == null) {
                    shareDialog = new ShareDialog(getContext(), new ShareDialog.OnShareListener() {
                        @Override
                        public void onShare(int shareType) {
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
                    });
                }
                shareDialog.show();
            }
        });
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

    @OnClick({R.id.iv_follow,
            R.id.tv_applaud_count,
            R.id.rly_fee_detail,
            R.id.rly_contact,
            R.id.rly_training_field
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_follow:
                mPresenter.follow();
                break;
            case R.id.tv_applaud_count:
                mPresenter.applaud();
                break;
            case R.id.rly_fee_detail:
                Intent intent = new Intent(getContext(), ClassTypeIntroActivity.class);
                intent.putExtra("totalAmount", mPresenter.getClassTypeByPs().price);
                intent.putExtra("coach", mPresenter.getCoach());
                intent.putExtra("classType", mPresenter.getClassTypeByPs());
                intent.putExtra("isShowPurchase", false);
                startActivity(intent);
                break;
            case R.id.rly_contact:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RequestCode.PERMISSIONS_REQUEST_CELL_PHONE);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    callMyCoach(mPresenter.getCoach().cell_phone);
                }
                break;
            case R.id.rly_training_field:
                Field field = mPresenter.getTrainingField();
                if (field != null) {
                    intent = new Intent(getContext(), FieldMapActivity.class);
                    intent.putExtra("field", field);
                    startActivity(intent);
                }
            default:
                break;
        }
    }

    @Override
    public void showCoachDetail(Coach coach) {
        mTvName.setText(coach.name);
        mTvDescription.setText(coach.bio);
        mIvAvatar.setImageURI(coach.avatar);
        mIsImages.updateImages(coach.images);
        int width = Utils.instence(this).getDm().widthPixels;
        int height = Math.round(width * 4 / 5);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        mIsImages.setLayoutParams(p);
        RelativeLayout.LayoutParams paramAvatar = new RelativeLayout.LayoutParams(Utils.instence(this).dip2px(70), Utils.instence(this).dip2px(70));
        paramAvatar.setMargins(Utils.instence(this).dip2px(30), height - Utils.instence(this).dip2px(35), 0, 0);
        mIvAvatar.setLayoutParams(paramAvatar);
        RelativeLayout.LayoutParams paramLlyFlCd = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramLlyFlCd.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.iv_coach_avatar);
        paramLlyFlCd.addRule(RelativeLayout.RIGHT_OF, R.id.iv_coach_avatar);
        mRlyInfoLine.setLayoutParams(paramLlyFlCd);
        mTvCoachPhone.setText(coach.cell_phone);
        mTvTrainLocation.setText(mPresenter.getTrainingFieldName());
        ArrayList<Coach> peerCoaches = coach.peer_coaches;
        if (peerCoaches != null && peerCoaches.size() > 0) {
            for (Coach peerCoach : peerCoaches) {
                mLlyPeerCoaches.addView(getPeerCoachAdapter(peerCoach));
            }
        } else {
            mLlyPeerCoaches.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(coach.driving_school)) {
            mLlyTrainSchool.setVisibility(View.VISIBLE);
            mTvTrainSchoolName.setText(coach.driving_school);
        } else {
            mLlyTrainSchool.setVisibility(View.GONE);
        }
        mTvCourseName.setText(coach.service_type_label);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mSvMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void enableFollow(boolean enable) {
        mIvFollow.setClickable(enable);
    }

    @Override
    public void showFollow(boolean isFollow) {
        if (isFollow) {
            mIvFollow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_coachmsg_attention_on));
        } else {
            mIvFollow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_coachmsg_attention_hold));
        }
    }

    @Override
    public void enableApplaud(boolean enable) {
        mTvApplaud.setClickable(enable);
    }

    @Override
    public void showApplaud(boolean isApplaud) {
        mTvApplaud.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, isApplaud ? R.drawable.ic_list_best_click : R.drawable.ic_list_best_unclick), null, null, null);
    }

    @Override
    public void setApplaudCount(int count) {
        mTvApplaud.setText(String.valueOf(count));
    }

    @Override
    public void startApplaudAnimation() {
        //点赞
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(2f, 1f, 2f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(200);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setFillAfter(true); //让其保持动画结束时的状态。
        mTvApplaud.startAnimation(animationSet);
    }

    private RelativeLayout getPeerCoachAdapter(final Coach coach) {
        RelativeLayout rly = new RelativeLayout(this);
        LinearLayout.LayoutParams rlyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rly.setLayoutParams(rlyParams);

        SimpleDraweeView ivAvatar = new SimpleDraweeView(this);
        GenericDraweeHierarchy hierarchy = ivAvatar.getHierarchy();
        hierarchy.setPlaceholderImage(R.drawable.ic_coach_ava);
        hierarchy.setRoundingParams(new RoundingParams().setRoundAsCircle(true));
        RelativeLayout.LayoutParams ivAvatarParams = new RelativeLayout.LayoutParams(Utils.instence(this).dip2px(40), Utils.instence(this).dip2px(40));
        ivAvatarParams.setMargins(Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15), Utils.instence(this).dip2px(10), Utils.instence(this).dip2px(15));
        ivAvatar.setLayoutParams(ivAvatarParams);
        ivAvatar.setImageURI(coach.avatar);
        int ivAvatarId = Utils.generateViewId();
        ivAvatar.setId(ivAvatarId);
        rly.addView(ivAvatar);

        TextView tvName = new TextView(this);
        RelativeLayout.LayoutParams tvNameParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvNameParams.addRule(RelativeLayout.RIGHT_OF, ivAvatarId);
        tvNameParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        tvName.setLayoutParams(tvNameParams);
        tvName.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        tvName.setTextSize(16);
        tvName.setText(coach.name);
        rly.addView(tvName);

        ImageView ivArrow = new ImageView(this);
        RelativeLayout.LayoutParams ivArrowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ivArrowParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        ivArrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        ivArrowParams.setMargins(0, 0, Utils.instence(this).dip2px(20), 0);
        ivArrow.setLayoutParams(ivArrowParams);
        ivArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_coachmsg_more_arrow));
        rly.addView(ivArrow);

        View divider = new View(this);
        RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
        dividerParams.setMargins(Utils.instence(this).dip2px(20), 0, 0, 0);
        dividerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        divider.setLayoutParams(dividerParams);
        divider.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
        rly.addView(divider);

        //点击合作教练,查看详情
        rly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CoachDetailActivity.class);
                intent.putExtra("coach_id", coach.id);
                startActivity(intent);
            }
        });

        return rly;
    }

    /**
     * 联系教练
     */
    private void callMyCoach(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
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
                callMyCoach(mPresenter.getCoach().cell_phone);
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系教练");
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

    private void shareToSms() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", mTitle + mDescription + mUrl);
        startActivity(intent);
    }
}
