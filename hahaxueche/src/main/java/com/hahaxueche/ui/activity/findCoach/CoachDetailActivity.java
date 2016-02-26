package com.hahaxueche.ui.activity.findCoach;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hahaxueche.R;
import com.hahaxueche.model.findCoach.BriefCoachInfo;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.model.findCoach.FieldModel;
import com.hahaxueche.model.findCoach.FollowResponse;
import com.hahaxueche.model.signupLogin.CityModel;
import com.hahaxueche.model.util.BaseApiResponse;
import com.hahaxueche.model.util.BaseBoolean;
import com.hahaxueche.model.util.BaseKeyValue;
import com.hahaxueche.model.util.ConstantsModel;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.ui.activity.signupLogin.StartActivity;
import com.hahaxueche.ui.dialog.ShareAppDialog;
import com.hahaxueche.ui.dialog.ZoomImgDialog;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.ui.widget.imageSwitcher.ImageSwitcher;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 教练详情Activity
 * Created by gibxin on 2016/2/13.
 */
public class CoachDetailActivity extends FCBaseActivity implements ImageSwitcher.OnSwitchItemClickListener {
    private CircleImageView civCdCoachAvatar;//教练头像
    private CircleImageView cirCommentStuAvatar1;//评价学员1头像
    private CircleImageView cirCommentStuAvatar2;//评价学员1头像
    private TextView tvCdCoachName;//教练姓名
    private TextView tvCdCoachDescription;//教练描述
    private ImageSwitcher isCdCoachDetail;//教练照片
    private ZoomImgDialog zoomImgDialog = null;
    private ImageButton ibtnCoachDetialBack;//回退按钮
    private LinearLayout llyShare;//分享
    private TextView tvSkillLevel;
    private ShareAppDialog shareAppDialog;
    private CoachModel mCoach;//教练
    private ProgressDialog pd;//进度框
    private ImageView ivIsGoldenCoach;
    private ImageView ivIsGoldenCoachSmall;
    private TextView tvSatisfactionRate;//满意度
    private ConstantsModel mConstants;
    private TextView tvTakeCertPrice;
    private TextView tvTrainLocation;
    private LinearLayout llyPeerCoachTitle;
    private LinearLayout llyPeerCoach1;
    private LinearLayout llyPeerCoach2;
    private TextView tvPeerCoach1;
    private TextView tvPeerCoach2;
    private CircleImageView civPeerCoachAvater1; //合作教练1头像
    private CircleImageView civPeerCoachAvater2; //合作教练2头像
    private TextView tvLicenseType;
    //关注
    private LinearLayout llyFollow;
    private ImageView ivFollow;
    private TextView tvFollow;
    private boolean isLogin = false;
    private String access_token;
    private boolean isFollow = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_detail);
        initView();
        initEvent();
        loadDatas();
    }

    private void initView() {
        civCdCoachAvatar = Util.instence(this).$(this, R.id.cir_cd_coach_avatar);
        civPeerCoachAvater1 = Util.instence(this).$(this, R.id.cir_peer_coach1);
        civPeerCoachAvater2 = Util.instence(this).$(this, R.id.cir_peer_coach2);
        cirCommentStuAvatar1 = Util.instence(this).$(this, R.id.cir_cd_comment_stu1);
        cirCommentStuAvatar2 = Util.instence(this).$(this, R.id.cir_cd_comment_stu2);
        llyPeerCoachTitle = Util.instence(this).$(this, R.id.lly_peer_coach_title);
        llyPeerCoach1 = Util.instence(this).$(this, R.id.lly_peer_coach1);
        llyPeerCoach2 = Util.instence(this).$(this, R.id.lly_peer_coach2);
        tvPeerCoach1 = Util.instence(this).$(this, R.id.tv_peer_coach1);
        tvPeerCoach2 = Util.instence(this).$(this, R.id.tv_peer_coach2);

        tvCdCoachName = Util.instence(this).$(this, R.id.tv_cd_coach_name);
        tvCdCoachDescription = Util.instence(this).$(this, R.id.tv_cd_coach_description);
        ibtnCoachDetialBack = Util.instence(this).$(this, R.id.ibtn_coach_detail_back);
        ivIsGoldenCoach = Util.instence(this).$(this, R.id.iv_cd_is_golden_coach);
        tvSatisfactionRate = Util.instence(this).$(this, R.id.tv_satisfaction_rate);
        tvSkillLevel = Util.instence(this).$(this, R.id.tv_skill_level);
        ivIsGoldenCoachSmall = Util.instence(this).$(this, R.id.iv_cd_is_golden_coach_small);
        tvTakeCertPrice = Util.instence(this).$(this, R.id.tv_take_cert_price);
        tvTrainLocation = Util.instence(this).$(this, R.id.tv_train_location);
        tvLicenseType = Util.instence(this).$(this, R.id.tv_license_type);
        //关注
        llyFollow = Util.instence(this).$(this, R.id.lly_follow);
        ivFollow = Util.instence(this).$(this, R.id.iv_follow);
        tvFollow = Util.instence(this).$(this, R.id.tv_follow);

        getCoachAvatar("http://img001.21cnimg.com/photos/album/20160204/m600/14F9F83CD7AC2266503030C7620299FE.jpeg", cirCommentStuAvatar1);
        getCoachAvatar("http://i3.sinaimg.cn/gm/cr/2013/0226/3279497539.jpg", cirCommentStuAvatar2);
        isCdCoachDetail = Util.instence(this).$(this, R.id.is_cd_coach_switcher);
        ArrayList<String> s = new ArrayList<String>();
        s.add("http://img2.3lian.com/2014/f5/158/d/87.jpg");
        s.add("http://img2.3lian.com/2014/f5/158/d/88.jpg");
        s.add("http://img2.3lian.com/2014/f5/158/d/89.jpg");
        s.add("http://img2.3lian.com/2014/f5/158/d/90.jpg");
        isCdCoachDetail.updateImages(s);

        llyShare = Util.instence(this).$(this, R.id.lly_share);
        shareAppDialog = new ShareAppDialog(this);
        SharedPreferences sharedPreferences = getSharedPreferences("constants", Activity.MODE_PRIVATE);
        String constantsStr = sharedPreferences.getString("constants", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ConstantsModel>() {
        }.getType();
        mConstants = gson.fromJson(constantsStr, type);

        SharedPreferences spSession = getSharedPreferences("session", Activity.MODE_PRIVATE);
        String accessToken = spSession.getString("access_token", "");
        if (!TextUtils.isEmpty(accessToken)) {
            isLogin = true;
            access_token = accessToken;
        }
    }

    private void initEvent() {
        isCdCoachDetail.setIndicatorRadius(Util.instence(this).dip2px(3));
        isCdCoachDetail.setIndicatorDivide(Util.instence(this).dip2px(15));
        isCdCoachDetail.setOnSwitchItemClickListener(this);
        ibtnCoachDetialBack.setOnClickListener(mClickListener);
        llyShare.setOnClickListener(mClickListener);
        llyFollow.setOnClickListener(mClickListener);
    }

    /**
     * 数据加载
     */
    private void loadDatas() {
        Intent intent = getIntent();
        if (intent.getSerializableExtra("coach") != null) {
            mCoach = (CoachModel) intent.getSerializableExtra("coach");
            loadDetail();
        } else {
            String coach_id = getIntent().getStringExtra("coach_id");
            if (pd != null) {
                pd.dismiss();
            }
            pd = ProgressDialog.show(CoachDetailActivity.this, null, "数据加载中，请稍后……");
            this.fcPresenter.getCoach(coach_id, new FCCallbackListener<CoachModel>() {
                @Override
                public void onSuccess(CoachModel coachModel) {
                    if (pd != null) {
                        pd.dismiss();
                    }
                    mCoach = coachModel;
                    loadDetail();
                }

                @Override
                public void onFailure(String errorEvent, String message) {
                    if (pd != null) {
                        pd.dismiss();
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
        //显示关注按钮
        if (isLogin) {
            this.fcPresenter.isFollow(mCoach.getUser_id(), access_token, new FCCallbackListener<BaseBoolean>() {
                @Override
                public void onSuccess(BaseBoolean baseBoolean) {
                    //已经关注
                    if (baseBoolean.isTrue()) {
                        setFollow();
                        isFollow = true;
                    }
                }

                @Override
                public void onFailure(String errorEvent, String message) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 加载详情
     */
    private void loadDetail() {
        tvCdCoachName.setText(mCoach.getName());
        tvCdCoachDescription.setText(mCoach.getBio());
        getCoachAvatar(mCoach.getAvatar_url(), civCdCoachAvatar);
        //金牌教练显示
        if (mCoach.getSkill_level().equals("1")) {
            ivIsGoldenCoach.setVisibility(View.VISIBLE);
            ivIsGoldenCoachSmall.setVisibility(View.VISIBLE);
        } else {
            ivIsGoldenCoach.setVisibility(View.GONE);
            ivIsGoldenCoachSmall.setVisibility(View.GONE);
        }
        tvSatisfactionRate.setText(mCoach.getSatisfaction_rate() + "%");
        //金牌教练，优秀教练
        List<BaseKeyValue> skillLevelList = mConstants.getSkill_levels();
        for (BaseKeyValue skillLevel : skillLevelList) {
            if (skillLevel.getId().equals(mCoach.getSkill_level())) {
                tvSkillLevel.setText(skillLevel.getReadable_name());
                break;
            }
        }
        tvTakeCertPrice.setText(Util.getMoney(mCoach.getCoach_group().getTraing_cost()));
        //训练场地址
        List<CityModel> cityList = mConstants.getCities();
        List<FieldModel> fieldList = mConstants.getFields();
        for (FieldModel field : fieldList) {
            if (field.getId().equals(mCoach.getCoach_group().getField_id())) {
                for (CityModel city : cityList) {
                    if (city.getId().equals(field.getCity_id())) {
                        tvTrainLocation.setText(city.getName() + field.getSection() + field.getStreet());
                    }
                }
            }
        }
        List<BriefCoachInfo> peerCoachList = mCoach.getPeer_coaches();
        if (peerCoachList != null && peerCoachList.size() > 0) {
            if (peerCoachList.size() > 1) {
                getCoachAvatar(peerCoachList.get(0).getAvatar_url(), civPeerCoachAvater1);
                tvPeerCoach1.setText(peerCoachList.get(0).getName());
                getCoachAvatar(peerCoachList.get(1).getAvatar_url(), civPeerCoachAvater2);
                tvPeerCoach2.setText(peerCoachList.get(1).getName());
            } else {
                getCoachAvatar(peerCoachList.get(0).getAvatar_url(), civPeerCoachAvater2);
                tvPeerCoach1.setText(peerCoachList.get(0).getName());
                llyPeerCoach2.setVisibility(View.GONE);
            }
        } else {
            llyPeerCoachTitle.setVisibility(View.GONE);
            llyPeerCoach1.setVisibility(View.GONE);
            llyPeerCoach2.setVisibility(View.GONE);
        }
        if (mCoach.getLicense_type().equals("1")) {
            tvLicenseType.setText("C1手动档");
        } else if (mCoach.getLicense_type().equals("2")) {
            tvLicenseType.setText("C2自动档");
        } else {
            tvLicenseType.setText("C1手动档，C2自动挡");
        }
    }

    private void getCoachAvatar(String url, CircleImageView civCoachAvatar) {
        final int iconWidth = Util.instence(this).dip2px(60);
        final int iconHeight = iconWidth;
        Picasso.with(this).load(url).resize(iconWidth, iconHeight)
                .into(civCoachAvatar);
    }

    @Override
    public void onSwitchClick(String url, List<String> urls) {
        if (zoomImgDialog == null)
            zoomImgDialog = new ZoomImgDialog(this, R.style.zoom_dialog);
        zoomImgDialog.setZoomImgeRes(url, urls, "");
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ibtn_coach_detail_back:
                    CoachDetailActivity.this.finish();
                    break;
                //分享
                case R.id.lly_share:
                    shareAppDialog.show();
                    break;
                //关注
                case R.id.lly_follow:
                    if (isLogin) {
                        if (isFollow) {
                            CoachDetailActivity.this.fcPresenter.cancelFollow(mCoach.getUser_id(), access_token, new FCCallbackListener<BaseApiResponse>() {
                                @Override
                                public void onSuccess(BaseApiResponse data) {
                                    setUnFollow();
                                    isFollow = false;
                                    Toast.makeText(context, "已取消关注！", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(String errorEvent, String message) {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            CoachDetailActivity.this.fcPresenter.follow(mCoach.getUser_id(), "", access_token, new FCCallbackListener<FollowResponse>() {
                                @Override
                                public void onSuccess(FollowResponse data) {
                                    setFollow();
                                    isFollow=true;
                                    Toast.makeText(context, "关注成功！", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(String errorEvent, String message) {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CoachDetailActivity.this);
                        builder.setTitle("提示");
                        builder.setIcon(R.drawable.ic_launcher);
                        builder.setMessage(R.string.fCPleaseLoginFirst);
                        builder.setPositiveButton(R.string.fCGoNow, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(CoachDetailActivity.this, StartActivity.class);
                                intent.putExtra("isBack", "1");
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton(R.string.fCLookAround, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
            }
        }
    };

    /**
     * 设置显示关注
     */
    private void setFollow() {
        ivFollow.setImageDrawable(getResources().getDrawable(R.drawable.ic_coachmsg_attention_on));
        tvFollow.setTextColor(getResources().getColor(R.color.app_theme_color));
    }

    /**
     * 设置未关注
     */
    private void setUnFollow() {
        ivFollow.setImageDrawable(getResources().getDrawable(R.drawable.ic_coachmsg_attention_hold));
        tvFollow.setTextColor(getResources().getColor(R.color.fCTxtGrayFade));
    }
}
