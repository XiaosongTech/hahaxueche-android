package com.hahaxueche.ui.activity.findCoach;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.MyApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.model.coach.BriefCoachInfo;
import com.hahaxueche.model.coach.Coach;
import com.hahaxueche.model.city.FieldModel;
import com.hahaxueche.model.response.FollowResponse;
import com.hahaxueche.model.response.GetReviewsResponse;
import com.hahaxueche.model.review.ReviewInfo;
import com.hahaxueche.model.city.City;
import com.hahaxueche.model.city.CostItem;
import com.hahaxueche.model.user.Session;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.base.BaseApiResponse;
import com.hahaxueche.model.base.BaseBoolean;
import com.hahaxueche.model.base.BaseKeyValue;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.ui.activity.base.BaseWebViewActivity;
import com.hahaxueche.ui.activity.mySetting.ReferFriendsActivity;
import com.hahaxueche.ui.activity.signupLogin.StartActivity;
import com.hahaxueche.ui.adapter.findCoach.PeerCoachItemAdapter;
import com.hahaxueche.ui.adapter.findCoach.ReviewItemAdapter;
import com.hahaxueche.ui.dialog.AppointmentDialog;
import com.hahaxueche.ui.dialog.BaseConfirmDialog;
import com.hahaxueche.ui.dialog.BaseConfirmSimpleDialog;
import com.hahaxueche.ui.dialog.FeeDetailDialog;
import com.hahaxueche.ui.dialog.ShareAppDialog;
import com.hahaxueche.ui.dialog.ZoomImgDialog;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.ui.widget.imageSwitcher.ImageSwitcher;
import com.hahaxueche.ui.widget.monitorScrollView.MonitorScrollView;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.pingplusplus.android.PaymentActivity;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;
import com.squareup.picasso.Picasso;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

/**
 * 教练详情Activity
 * Created by gibxin on 2016/2/13.
 */
public class CoachDetailActivity extends FCBaseActivity implements ImageSwitcher.OnSwitchItemClickListener, IWeiboHandler.Response {
    private MonitorScrollView msvCoachDetail;
    private CircleImageView civCdCoachAvatar;//教练头像
    private TextView tvCdCoachName;//教练姓名
    private TextView tvCdCoachDescription;//教练描述
    private ImageSwitcher isCdCoachDetail;//教练照片
    private ZoomImgDialog zoomImgDialog = null;
    private ImageButton ibtnCoachDetialBack;//回退按钮
    private LinearLayout llyShare;//分享
    private TextView tvSkillLevel;
    private ShareAppDialog shareAppDialog;
    private Coach mCoach;//教练
    private ProgressDialog pd;//进度框
    private ImageView ivIsGoldenCoach;
    private ImageView ivIsGoldenCoachSmall;
    private TextView tvSatisfactionRate;//满意度
    private Constants mConstants;
    private Session mSession;
    private TextView tvTrainLocation;
    private LinearLayout llyPeerCoachTitle;
    private View vwMoreReviews;
    private TextView tvLicenseType;
    private TextView tvMoreReviews;
    //关注
    private LinearLayout llyFollow;
    private ImageView ivFollow;
    private TextView tvFollow;
    private boolean isLogin = false;
    private boolean isFollow = false;
    private FeeDetailDialog feeDetailDialog;
    private List<CostItem> mCostItemList;
    private TextView tvCommentCounts;//学员评价数量
    private ScoreView svAverageRating;//综合得分
    //评论
    private ListView lvReviewList;
    private ReviewItemAdapter reviewItemAdapter;
    //合作教练
    private List<BriefCoachInfo> peerCoachList;
    private ListView lvPeerCoach;
    private PeerCoachItemAdapter peerCoachItemAdapter;
    private GetReviewsResponse mGetReviewsResponse;
    private LinearLayout llyMoreReviews;
    //确认付款
    private LinearLayout llySurePay;
    //免费试学
    private LinearLayout llyFreeLearn;
    //学员
    private Student mStudent;
    //训练场地
    private RelativeLayout rlyTrainLoaction;
    //拿证价格
    private TextView mTvNormalPrice;//通用价格
    private TextView mTvOldNormalPrice;//原来的通用价格
    private TextView mTvNormalPriceDetail;//通用价格明细
    private RelativeLayout mRlyVIPPrice;
    private TextView mTvVIPPrice;//VIP班价格
    private TextView mTvOldVIPPrice;//原来的VIP价格
    private TextView mTvVIPPriceDetail;//VIP班价格明细
    private FieldModel mFieldModel;
    private RelativeLayout mRlyCoachName;
    private SharedPreferencesUtil spUtil;
    private String coach_id;
    /*****************
     * 分享
     ******************/
    private IWXAPI wxApi; //微信api
    private Tencent mTencent;//QQ
    private IWeiboShareAPI mWeiboShareAPI;//新浪微博
    private MyApplication myApplication;
    private String mTitle;
    private String mDescription;
    private String mImageUrl;
    private String mUrl;
    /*****************
     * end
     ******************/
    private TextView mTvApplaudCount;
    private ImageView mIvApplaud;
    private boolean isApplaud;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = Locale.CHINA;
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        config.locale = Locale.SIMPLIFIED_CHINESE;
        getResources().updateConfiguration(config, metrics);
        setContentView(R.layout.activity_coach_detail);
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        mConstants = spUtil.getConstants();
        mSession = spUtil.getUser().getSession();
        mStudent = spUtil.getUser().getStudent();
        if (mSession != null && mStudent != null) {
            isLogin = true;
        }
        //根据当前登录人的cityid，加载费用明细列表
        if (mConstants != null) {
            City myCity = spUtil.getMyCity();
            if (myCity != null) {
                mCostItemList = myCity.getFixed_cost_itemizer();
            }
        }
        initView();
        initEvent();
        loadDatas();
        regShareApi();
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
    }

    /**
     * 获取分享API
     */
    private void regShareApi() {
        myApplication = (MyApplication) this.getApplication();
        wxApi = myApplication.getIWXAPI();
        mTencent = myApplication.getTencentAPI();
        mWeiboShareAPI = myApplication.getWeiboAPI();
    }

    private void initView() {
        msvCoachDetail = Util.instence(this).$(this, R.id.msv_coach_detail);
        civCdCoachAvatar = Util.instence(this).$(this, R.id.cir_cd_coach_avatar);
        llyPeerCoachTitle = Util.instence(this).$(this, R.id.lly_peer_coach_title);
        vwMoreReviews = Util.instence(this).$(this, R.id.vw_more_reviews);
        tvMoreReviews = Util.instence(this).$(this, R.id.tv_more_reviews);

        tvCdCoachName = Util.instence(this).$(this, R.id.tv_cd_coach_name);
        tvCdCoachDescription = Util.instence(this).$(this, R.id.tv_cd_coach_description);
        ibtnCoachDetialBack = Util.instence(this).$(this, R.id.ibtn_coach_detail_back);
        ivIsGoldenCoach = Util.instence(this).$(this, R.id.iv_cd_is_golden_coach);
        tvSatisfactionRate = Util.instence(this).$(this, R.id.tv_satisfaction_rate);
        tvSkillLevel = Util.instence(this).$(this, R.id.tv_skill_level);
        ivIsGoldenCoachSmall = Util.instence(this).$(this, R.id.iv_cd_is_golden_coach_small);
        tvTrainLocation = Util.instence(this).$(this, R.id.tv_train_location);
        tvLicenseType = Util.instence(this).$(this, R.id.tv_license_type);
        tvCommentCounts = Util.instence(this).$(this, R.id.tv_comments_count);
        svAverageRating = Util.instence(this).$(this, R.id.sv_average_rating);
        //关注
        llyFollow = Util.instence(this).$(this, R.id.lly_follow);
        ivFollow = Util.instence(this).$(this, R.id.iv_follow);
        tvFollow = Util.instence(this).$(this, R.id.tv_follow);
        //评论
        lvReviewList = Util.instence(this).$(this, R.id.lv_reviews_list);
        llyMoreReviews = Util.instence(this).$(this, R.id.lly_more_reviews);
        lvPeerCoach = Util.instence(this).$(this, R.id.lv_peer_coach_list);
        //确认付款
        llySurePay = Util.instence(this).$(this, R.id.lly_sure_pay);
        //免费试学
        llyFreeLearn = Util.instence(this).$(this, R.id.lly_free_learn);

        isCdCoachDetail = Util.instence(this).$(this, R.id.is_cd_coach_switcher);

        llyShare = Util.instence(this).$(this, R.id.lly_share);
        rlyTrainLoaction = Util.instence(this).$(this, R.id.rly_location);
        mRlyCoachName = Util.instence(this).$(this, R.id.fl_cd_coach_name);
        //价格
        mTvNormalPrice = Util.instence(this).$(this, R.id.tv_normal_price);
        mTvOldNormalPrice = Util.instence(this).$(this, R.id.tv_old_normal_price);
        mTvNormalPriceDetail = Util.instence(this).$(this, R.id.tv_normal_price_detail);
        mTvVIPPrice = Util.instence(this).$(this, R.id.tv_vip_price);
        mTvOldVIPPrice = Util.instence(this).$(this, R.id.tv_old_vip_price);
        mTvVIPPriceDetail = Util.instence(this).$(this, R.id.tv_vip_price_detail);
        mRlyVIPPrice = Util.instence(this).$(this, R.id.rly_vip_price);
        //点赞
        mTvApplaudCount = Util.instence(this).$(this, R.id.tv_applaud_count);
        mIvApplaud = Util.instence(this).$(this, R.id.iv_applaud);
    }

    private void initEvent() {
        isCdCoachDetail.setIndicatorRadius(Util.instence(this).dip2px(3));
        isCdCoachDetail.setIndicatorDivide(Util.instence(this).dip2px(15));
        isCdCoachDetail.setOnSwitchItemClickListener(this);
        ibtnCoachDetialBack.setOnClickListener(mClickListener);
        llyShare.setOnClickListener(mClickListener);
        llyFollow.setOnClickListener(mClickListener);
        llySurePay.setOnClickListener(mClickListener);
        llyFreeLearn.setOnClickListener(mClickListener);
        llyMoreReviews.setOnClickListener(mClickListener);
        rlyTrainLoaction.setOnClickListener(mClickListener);
        lvPeerCoach.setOnItemClickListener(mItemClickListener);
        //价格
        mTvNormalPriceDetail.setOnClickListener(mClickListener);
        mTvVIPPriceDetail.setOnClickListener(mClickListener);
        mIvApplaud.setOnClickListener(mClickListener);
    }

    /**
     * 数据加载
     */
    private void loadDatas() {
        if (pd != null) {
            pd.dismiss();
        }
        pd = ProgressDialog.show(CoachDetailActivity.this, null, "数据加载中，请稍后……");
        Intent intent = getIntent();
        if (intent.getSerializableExtra("coach") != null) {
            mCoach = (Coach) intent.getSerializableExtra("coach");
            loadDetail();
            loadReviews();
            loadFollow();
            loadApplaud();
            if (pd != null) {
                pd.dismiss();
            }
        } else {
            if (intent.getSerializableExtra("coachId") != null) {
                coach_id = (String) intent.getSerializableExtra("coachId");
            } else {
                coach_id = getIntent().getStringExtra("coach_id");
            }
            String studentId = "";
            if (isLogin) {
                studentId = mStudent.getId();
            }
            this.fcPresenter.getCoach(coach_id, studentId, new FCCallbackListener<Coach>() {
                @Override
                public void onSuccess(Coach coach) {
                    mCoach = coach;
                    loadDetail();
                    loadReviews();
                    loadFollow();
                    loadApplaud();
                    if (pd != null) {
                        pd.dismiss();
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

    }

    /**
     * 加载详情
     */
    private void loadDetail() {
        tvCdCoachName.setText(mCoach.getName());
        tvCdCoachDescription.setText(mCoach.getBio());
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = Math.round(width * 4 / 5);
        getCoachAvatar(mCoach.getAvatar(), civCdCoachAvatar);
        isCdCoachDetail.updateImages(mCoach.getImages(), height);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        isCdCoachDetail.setLayoutParams(p);
        RelativeLayout.LayoutParams paramAvatar = new RelativeLayout.LayoutParams(Util.instence(this).dip2px(70), Util.instence(this).dip2px(70));
        paramAvatar.setMargins(Util.instence(this).dip2px(30), height - Util.instence(this).dip2px(35), 0, 0);
        civCdCoachAvatar.setLayoutParams(paramAvatar);
        RelativeLayout.LayoutParams paramLlyFlCd = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramLlyFlCd.addRule(RelativeLayout.ALIGN_BOTTOM, civCdCoachAvatar.getId());
        mRlyCoachName.setLayoutParams(paramLlyFlCd);
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
        //价格
        mTvNormalPrice.setText(Util.getMoney(mCoach.getCoach_group().getTraining_cost()));
        mTvOldNormalPrice.setText("门市价：" + Util.getMoney(mCoach.getCoach_group().getMarket_price()));
        mTvOldNormalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        if (mCoach.getVip() == 0) {
            mRlyVIPPrice.setVisibility(View.GONE);
        } else {
            mRlyVIPPrice.setVisibility(View.VISIBLE);
            mTvVIPPrice.setText(Util.getMoney(mCoach.getCoach_group().getVip_price()));
            mTvOldVIPPrice.setText("门市价：" + Util.getMoney(mCoach.getCoach_group().getVip_market_price()));
            mTvOldVIPPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        //训练场地址
        List<City> cityList = mConstants.getCities();
        List<FieldModel> fieldList = mConstants.getFields();
        for (FieldModel field : fieldList) {
            if (field.getId().equals(mCoach.getCoach_group().getField_id())) {
                for (City city : cityList) {
                    if (city.getId().equals(field.getCity_id())) {
                        tvTrainLocation.setText(city.getName() + field.getSection() + field.getStreet());
                        mFieldModel = field;
                        break;
                    }
                }
            }
        }
        peerCoachList = mCoach.getPeer_coaches();
        if (peerCoachList != null && peerCoachList.size() > 0) {
            peerCoachItemAdapter = new PeerCoachItemAdapter(CoachDetailActivity.this, peerCoachList, R.layout.view_peer_coach_item);
            lvPeerCoach.setAdapter(peerCoachItemAdapter);
            setListViewHeightBasedOnChildren(lvPeerCoach);
            msvCoachDetail.smoothScrollTo(0, 0);
        } else {
            llyPeerCoachTitle.setVisibility(View.GONE);
        }


        if (mCoach.getLicense_type().equals("1")) {
            tvLicenseType.setText("C1手动档");
        } else if (mCoach.getLicense_type().equals("2")) {
            tvLicenseType.setText("C2自动档");
        } else {
            tvLicenseType.setText("C1手动档，C2自动挡");
        }
        //学员评价数量
        tvCommentCounts.setText("学员评价（" + mCoach.getReview_count() + "）");
        //综合得分
        float averageRating = 0;
        if (!TextUtils.isEmpty(mCoach.getAverage_rating())) {
            averageRating = Float.parseFloat(mCoach.getAverage_rating());
        }
        if (averageRating > 5) {
            averageRating = 5;
        }
        svAverageRating.setScore(averageRating, true);
    }

    private void getCoachAvatar(String url, CircleImageView civCoachAvatar) {
        final int iconWidth = Util.instence(this).dip2px(70);
        final int iconHeight = iconWidth;
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(this).load(url).resize(iconWidth, iconHeight)
                    .into(civCoachAvatar);
        }
    }

    private void loadReviews() {
        this.fcPresenter.getReviewList(mCoach.getUser_id(), "", "10", new FCCallbackListener<GetReviewsResponse>() {
            @Override
            public void onSuccess(GetReviewsResponse getReviewsResponse) {
                mGetReviewsResponse = getReviewsResponse;
                if (getReviewsResponse.getData() != null && getReviewsResponse.getData().size() > 0) {
                    List<ReviewInfo> reviewInfos = new ArrayList<ReviewInfo>();
                    for (int i = 0; i < getReviewsResponse.getData().size(); i++) {
                        if (i == 2) break;
                        reviewInfos.add(getReviewsResponse.getData().get(i));
                    }

                    reviewItemAdapter = new ReviewItemAdapter(CoachDetailActivity.this, reviewInfos, R.layout.view_review_list_item, true);
                    lvReviewList.setAdapter(reviewItemAdapter);
                    setListViewHeightBasedOnChildren(lvReviewList);
                    msvCoachDetail.smoothScrollTo(0, 0);

                } else {
                    vwMoreReviews.setVisibility(View.GONE);
                    tvMoreReviews.setText(mCoach.getName() + "教练目前还没有评价");
                    tvMoreReviews.setTextColor(ContextCompat.getColor(CoachDetailActivity.this, R.color.haha_gray_heavy));
                    llyMoreReviews.setClickable(false);
                }
            }

            @Override
            public void onFailure(String errorEvent, String message) {

            }
        });
    }

    private void loadFollow() {
        //显示关注按钮
        if (isLogin) {
            this.fcPresenter.isFollow(mCoach.getUser_id(), mSession.getAccess_token(), new FCCallbackListener<BaseBoolean>() {
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
                }
            });
        }
    }

    private void loadApplaud() {
        if (isLogin) {
            if (!TextUtils.isEmpty(mCoach.getLiked()) && mCoach.getLiked().equals("1")) {
                mIvApplaud.setImageDrawable(ContextCompat.getDrawable(CoachDetailActivity.this, R.drawable.ic_list_best_click));
                isApplaud = !isApplaud;
            }
        }
        mTvApplaudCount.setText(String.valueOf(mCoach.getLike_count()));
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
                    showShareAppDialog();
                    break;
                //关注
                case R.id.lly_follow:
                    if (isLogin) {
                        if (isFollow) {
                            CoachDetailActivity.this.fcPresenter.cancelFollow(mCoach.getUser_id(), mSession.getAccess_token(), new FCCallbackListener<BaseApiResponse>() {
                                @Override
                                public void onSuccess(BaseApiResponse data) {
                                    setUnFollow();
                                    isFollow = false;
                                    Toast.makeText(context, "已取消关注！", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(String errorEvent, String message) {
                                }
                            });
                        } else {
                            CoachDetailActivity.this.fcPresenter.follow(mCoach.getUser_id(), "", mSession.getAccess_token(), new FCCallbackListener<FollowResponse>() {
                                @Override
                                public void onSuccess(FollowResponse data) {
                                    setFollow();
                                    isFollow = true;
                                    Toast.makeText(context, "关注成功！", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(String errorEvent, String message) {
                                }
                            });
                        }
                    } else {
                        alertToLogin();
                    }
                    break;
                //拿证价格
                case R.id.tv_normal_price_detail:
                    feeDetailDialog = new FeeDetailDialog(CoachDetailActivity.this, mCostItemList, mCoach.getCoach_group().getTraining_cost(), "1", null);
                    feeDetailDialog.show();
                    break;
                case R.id.tv_vip_price_detail:
                    feeDetailDialog = new FeeDetailDialog(CoachDetailActivity.this, mCostItemList, mCoach.getCoach_group().getVip_price(), "1", null);
                    feeDetailDialog.show();
                    break;
                //确认付款
                case R.id.lly_sure_pay:
                    if (isLogin) {
                        if (mStudent.getPurchased_services() != null && mStudent.getPurchased_services().size() > 0) {
                            Toast.makeText(context, "您已经选择过教练", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(CoachDetailActivity.this, PurchaseCoachActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("coach", mCoach);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 1);
                    } else {
                        alertToLogin();
                    }
                    break;
                //免费试学
                case R.id.lly_free_learn:
                    freeTry();
                    break;
                //加载评论列表页面
                case R.id.lly_more_reviews:
                    Intent intent = new Intent(CoachDetailActivity.this, ReviewListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("getReviewsResponse", mGetReviewsResponse);
                    bundle.putSerializable("coach", mCoach);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                //训练场
                case R.id.rly_location:
                    intent = new Intent(CoachDetailActivity.this, FieldMapActivity.class);
                    bundle = new Bundle();
                    bundle.putSerializable("fieldModel", mFieldModel);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                //点赞
                case R.id.iv_applaud:
                    applaudClick();
                    break;
                default:
                    break;
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


    /**
     * 提示去登录
     */
    private void alertToLogin() {
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

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //params.height = Util.instence(this).dip2px(height) * listAdapter.getCount() + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(getApplication(), PaySuccessActivity.class);
                startActivityForResult(intent, 2);
            }
        } else if (requestCode == 2) {
            navigateToShare();
        }
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (peerCoachList != null && peerCoachList.size() > 0 && position > -1 && position < peerCoachList.size()) {
                Intent intent = new Intent(CoachDetailActivity.this, CoachDetailActivity.class);
                intent.putExtra("coach_id", peerCoachList.get(position).getId());
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shareAppDialog = null;
    }

    private void showShareAppDialog() {
        if (pd != null) {
            pd.dismiss();
        }
        pd = ProgressDialog.show(CoachDetailActivity.this, null, "数据加载中，请稍后……");
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("coach/" + mCoach.getId())
                .setTitle("Share Coach")
                .setContentDescription("Share coach link")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("coachId", mCoach.getId());
        LinkProperties linkProperties = new LinkProperties().setChannel("android").setFeature("sharing");
        branchUniversalObject.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(final String url, BranchError error) {
                pd.dismiss();
                if (error == null) {
                    Log.i("gibxin", "got my Branch link to share: " + url);
                    shareAppDialog = new ShareAppDialog(CoachDetailActivity.this, new ShareAppDialog.OnShareListener() {
                        @Override
                        public void onShare(int shareType) {
                            mTitle = "哈哈学车-开启快乐学车之旅吧～";
                            mDescription = "好友力荐:\n哈哈学车优秀教练" + mCoach.getName();
                            mImageUrl = "http://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
                            try {
                                mUrl = HttpEngine.BASE_SERVER_IP + "/share/coaches/" + mCoach.getId() + "?target=" + URLEncoder.encode(url, "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            share(shareType);
                        }
                    });
                    shareAppDialog.show();
                }
            }
        });
    }

    private void share(int shareType) {
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
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Branch.isAutoDeepLinkLaunch(this)) {
            try {
                String autoDeeplinkedValue = Branch.getInstance().getLatestReferringParams().getString("coachId");
                coach_id = autoDeeplinkedValue;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    private void shareToQQ() {
        ShareListener myListener = new ShareListener();
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, mTitle);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "哈哈学车");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mDescription);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mImageUrl);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mUrl);
        mTencent.shareToQQ(this, params, myListener);
    }

    private void shareToQZone() {
        ShareListener myListener = new ShareListener();
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_APP);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, mTitle);
        params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, "哈哈学车");
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mDescription);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mUrl);
        ArrayList<String> imgUrlList = new ArrayList<>();
        imgUrlList.add(mImageUrl);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrlList);
        mTencent.shareToQzone(this, params, myListener);
    }

    private void shareToWeibo() {
        // 1. 初始化微博的分享消息
        WeiboMessage weiboMessage = new WeiboMessage();
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = mTitle;
        mediaObject.description = mDescription;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = mUrl;
        mediaObject.defaultText = mTitle + mDescription;
        weiboMessage.mediaObject = mediaObject;
        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(CoachDetailActivity.this, request);
    }

    private void shareToWeixin() {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mTitle;
        msg.description = mDescription;
        Bitmap thumb = BitmapFactory.decodeResource(CoachDetailActivity.this.getResources(), R.drawable.ic_launcher);
        msg.thumbData = Util.bmpToByteArray(thumb, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        //SendMessageToWX.Req.WXSceneTimeline
        req.scene = SendMessageToWX.Req.WXSceneSession;
        wxApi.sendReq(req);
    }

    private void shareToFriendCircle() {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mTitle;
        msg.description = mDescription;
        Bitmap thumb = BitmapFactory.decodeResource(CoachDetailActivity.this.getResources(), R.drawable.ic_launcher);
        msg.thumbData = Util.bmpToByteArray(thumb, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private class ShareListener implements IUiListener {

        @Override
        public void onCancel() {
            Log.v("gibxin", "onCancel");
        }

        @Override
        public void onComplete(Object arg0) {
            Log.v("gibxin", "onComplete");
        }

        @Override
        public void onError(UiError arg0) {
            Log.v("gibxin", "onError");
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    @Override
    public void onResponse(BaseResponse baseResp) {
        if (baseResp != null) {
            Log.v("gibxin", "baseResp.errCode" + baseResp.errCode);
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    Toast.makeText(CoachDetailActivity.this, "分享成功", Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    Toast.makeText(CoachDetailActivity.this, "取消分享", Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    Toast.makeText(CoachDetailActivity.this, "分享失败，原因：" + baseResp.errMsg, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    /**
     * 点赞
     */
    private void applaudClick() {
        if (isLogin) {
            mIvApplaud.setClickable(false);
            fcPresenter.applaudCoach(isApplaud, mStudent.getId(), mCoach.getId(), mSession.getAccess_token(), new FCCallbackListener<Coach>() {
                @Override
                public void onSuccess(final Coach coach) {
                    mCoach = coach;
                    if (isApplaud) {
                        //取消点赞
                        mIvApplaud.setImageDrawable(ContextCompat.getDrawable(CoachDetailActivity.this, R.drawable.ic_list_best_unclick));
                        mTvApplaudCount.setText(String.valueOf(mCoach.getLike_count()));
                        mIvApplaud.setClickable(true);
                        isApplaud = !isApplaud;
                    } else {
                        //点赞
                        AnimationSet animationSet = new AnimationSet(true);
                        ScaleAnimation scaleAnimation = new ScaleAnimation(2f, 1f, 2f, 1f,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        scaleAnimation.setDuration(200);
                        animationSet.addAnimation(scaleAnimation);
                        animationSet.setFillAfter(true); //让其保持动画结束时的状态。
                        mIvApplaud.startAnimation(animationSet);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                mIvApplaud.setImageDrawable(ContextCompat.getDrawable(CoachDetailActivity.this, R.drawable.ic_list_best_click));
                                mTvApplaudCount.setText(String.valueOf(mCoach.getLike_count()));
                                mIvApplaud.setClickable(true);
                                isApplaud = !isApplaud;
                            }
                        }, 200);
                    }
                }

                @Override
                public void onFailure(String errorEvent, String message) {

                }
            });
        } else {
            BaseConfirmSimpleDialog baseConfirmDialog = new BaseConfirmSimpleDialog(CoachDetailActivity.this, "请登录", "您只有注册后\n才可以给教练点赞哦~", "去登录", "知道了", new BaseConfirmSimpleDialog.onConfirmListener() {
                @Override
                public boolean clickConfirm() {
                    Intent intent = new Intent(getApplication(), StartActivity.class);
                    intent.putExtra("isBack", "1");
                    startActivity(intent);
                    return true;
                }
            }, new BaseConfirmSimpleDialog.onCancelListener() {
                @Override
                public boolean clickCancel() {
                    return true;
                }
            });
            baseConfirmDialog.show();
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("coach", mCoach);
        intent.putExtras(bundle);
        Log.v("gibxin", "setResult -> " + RESULT_OK);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    private void freeTry() {
        Intent intent = new Intent(getApplication(), BaseWebViewActivity.class);
        Bundle bundle = new Bundle();
        //免费试学URL
        String url = "http://m.hahaxueche.com/free_trial?promo_code=553353";
        if (spUtil == null) {
            spUtil = new SharedPreferencesUtil(getApplicationContext());
        }
        if (spUtil.getUser() != null && spUtil.getUser().getStudent() != null) {
            if (!TextUtils.isEmpty(mCoach.getId())) {
                url += "&coach_id=" + mCoach.getId();
            }
            if (!TextUtils.isEmpty(spUtil.getUser().getStudent().getCity_id())) {
                url += "&city_id=" + spUtil.getUser().getStudent().getCity_id();
            }
            if (!TextUtils.isEmpty(spUtil.getUser().getStudent().getName())) {
                url += "&name=" + spUtil.getUser().getStudent().getName();
            }
            if (!TextUtils.isEmpty(spUtil.getUser().getStudent().getCell_phone())) {
                url += "&phone=" + spUtil.getUser().getStudent().getCell_phone();
            }

        }
        Log.v("gibxin", "free try url -> " + url);
        bundle.putString("url", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void navigateToShare() {
        Intent intent = new Intent(getApplication(), ReferFriendsActivity.class);
        startActivity(intent);
    }

}
