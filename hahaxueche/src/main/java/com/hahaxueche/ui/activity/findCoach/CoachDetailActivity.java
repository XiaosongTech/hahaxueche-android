package com.hahaxueche.ui.activity.findCoach;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.hahaxueche.model.signupLogin.CitiesModel;
import com.hahaxueche.model.signupLogin.CityModel;
import com.hahaxueche.model.util.BaseKeyValue;
import com.hahaxueche.model.util.ConstantsModel;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
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
    }

    private void initEvent() {
        isCdCoachDetail.setIndicatorRadius(Util.instence(this).dip2px(3));
        isCdCoachDetail.setIndicatorDivide(Util.instence(this).dip2px(15));
        isCdCoachDetail.setOnSwitchItemClickListener(this);
        ibtnCoachDetialBack.setOnClickListener(mClickListener);
        llyShare.setOnClickListener(mClickListener);
    }

    /**
     * 数据加载
     */
    private void loadDatas() {
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
                case R.id.lly_share:
                    shareAppDialog.show();
                    break;
            }
        }
    };
}
