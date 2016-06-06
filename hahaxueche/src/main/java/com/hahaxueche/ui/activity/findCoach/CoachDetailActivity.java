package com.hahaxueche.ui.activity.findCoach;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
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
import com.hahaxueche.ui.activity.signupLogin.StartActivity;
import com.hahaxueche.ui.adapter.findCoach.PeerCoachItemAdapter;
import com.hahaxueche.ui.adapter.findCoach.ReviewItemAdapter;
import com.hahaxueche.ui.dialog.AppointmentDialog;
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
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

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
public class CoachDetailActivity extends FCBaseActivity implements ImageSwitcher.OnSwitchItemClickListener {
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
    private AppointmentDialog appointmentDialog;
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
    private LinearLayout llyFlCdCoachName;
    private SharedPreferencesUtil spUtil;
    private String coach_id;

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
            mCostItemList = spUtil.getMyCity().getFixed_cost_itemizer();
        }
        initView();
        initEvent();
        loadDatas();
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
        llyFlCdCoachName = Util.instence(this).$(this, R.id.fl_cd_coach_name);
        //价格
        mTvNormalPrice = Util.instence(this).$(this, R.id.tv_normal_price);
        mTvOldNormalPrice = Util.instence(this).$(this, R.id.tv_old_normal_price);
        mTvNormalPriceDetail = Util.instence(this).$(this, R.id.tv_normal_price_detail);
        mTvVIPPrice = Util.instence(this).$(this, R.id.tv_vip_price);
        mTvOldVIPPrice = Util.instence(this).$(this, R.id.tv_old_vip_price);
        mTvVIPPriceDetail = Util.instence(this).$(this, R.id.tv_vip_price_detail);
        mRlyVIPPrice = Util.instence(this).$(this, R.id.rly_vip_price);
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
            if (pd != null) {
                pd.dismiss();
            }
        } else {
            if (intent.getSerializableExtra("coachId") != null) {
                coach_id = (String) intent.getSerializableExtra("coachId");
            } else {
                coach_id = getIntent().getStringExtra("coach_id");
            }
            this.fcPresenter.getCoach(coach_id, new FCCallbackListener<Coach>() {
                @Override
                public void onSuccess(Coach coach) {
                    mCoach = coach;
                    loadDetail();
                    loadReviews();
                    loadFollow();
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
        llyFlCdCoachName.setLayoutParams(paramLlyFlCd);
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
        if (mCoach.getVip()==0) {
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
        Picasso.with(this).load(url).resize(iconWidth, iconHeight)
                .into(civCoachAvatar);
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
                    String name = "";
                    String phoneNumber = "";
                    if (mStudent != null) {
                        name = mStudent.getName();
                        phoneNumber = mStudent.getCell_phone();
                    }
                    appointmentDialog = new AppointmentDialog(CoachDetailActivity.this, name, phoneNumber, mCoach.getId());
                    appointmentDialog.show();
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
                Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(CoachDetailActivity.this, CoachDetailActivity.class);
            intent.putExtra("coach_id", peerCoachList.get(position).getId());
            startActivity(intent);
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
            public void onLinkCreate(String url, BranchError error) {
                pd.dismiss();
                if (error == null) {
                    Log.i("gibxin", "got my Branch link to share: " + url);
                    shareAppDialog = new ShareAppDialog(CoachDetailActivity.this, url, mCoach);
                    shareAppDialog.show();
                }
            }
        });
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

}
