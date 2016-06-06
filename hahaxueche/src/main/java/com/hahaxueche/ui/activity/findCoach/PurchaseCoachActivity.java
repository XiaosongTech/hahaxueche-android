package com.hahaxueche.ui.activity.findCoach;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.city.City;
import com.hahaxueche.model.city.CostItem;
import com.hahaxueche.model.city.FieldModel;
import com.hahaxueche.model.coach.Coach;
import com.hahaxueche.model.student.Payment;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.user.Session;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.ui.adapter.findCoach.PaymentAdapter;
import com.hahaxueche.ui.dialog.FeeDetailDialog;
import com.hahaxueche.ui.dialog.MapDialog;
import com.hahaxueche.ui.util.DistanceUtil;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.pingplusplus.android.PaymentActivity;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/27.
 */
public class PurchaseCoachActivity extends FCBaseActivity {
    private TextView mTvCoachName;
    private TextView mTvCoachTeachTime;
    private TextView mTvCoachPoints;
    private TextView mTvCoachActualPrice;
    private TextView mTvCoachVIPPrice;
    private TextView mTvCoachVIPPriceLabel;
    private CircleImageView mCivCoachAvatar;
    private ImageView mIvIsGoldenCoach;
    private ScoreView mSvCoachScore;
    private TextView mTvCoachLocation;
    private LinearLayout mLlyCoachLocation;
    private TextView mTvDistance;
    private ListView mLvPayment;
    private PaymentAdapter mPaymentAdapter;
    private TextView mTvSurePay;
    private ImageButton mIbtnBack;
    private ProgressDialog pd;//进度框
    //拿证价格
    private RelativeLayout mRlyNormalPrice;
    private ImageView mIvSelectNormal;
    private TextView mTvNormalPrice;//通用价格
    private TextView mTvOldNormalPrice;//原来的通用价格
    private TextView mTvNormalPriceDetail;//通用价格明细
    private RelativeLayout mRlyVIPPrice;
    private ImageView mIvSelectVIP;
    private TextView mTvVIPPrice;//VIP班价格
    private TextView mTvOldVIPPrice;//原来的VIP价格
    private TextView mTvVIPPriceDetail;//VIP班价格明细
    private RelativeLayout mRlyMorePayment;//更多支付方式

    private Coach mCoach;
    private List<Payment> mPaymentList;
    private MapDialog mapDialog;
    private FieldModel mFieldModel;
    private SharedPreferencesUtil spUtil;
    private String myLat;
    private String myLng;
    private Constants mConstants;
    private List<FieldModel> fieldsList;
    private List<City> cityList;
    private Session mSession;
    private Student mStudent;
    private String mProductType = "";
    private List<CostItem> mCostItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_coach);
        initViews();
        loadDatas();
        initEvents();
    }

    private void initViews() {
        mTvCoachName = Util.instence(this).$(this, R.id.tv_coach_name);
        mTvCoachTeachTime = Util.instence(this).$(this, R.id.tv_coach_teach_time);
        mTvCoachPoints = Util.instence(this).$(this, R.id.tv_coach_points);
        mTvCoachActualPrice = Util.instence(this).$(this, R.id.tv_coach_actual_price);
        mTvCoachVIPPrice = Util.instence(this).$(this, R.id.tv_coach_vip_price);
        mTvCoachVIPPriceLabel = Util.instence(this).$(this, R.id.tv_coach_vip_label);
        mCivCoachAvatar = Util.instence(this).$(this, R.id.cir_coach_avatar);
        mIvIsGoldenCoach = Util.instence(this).$(this, R.id.iv_is_golden_coach);
        mSvCoachScore = Util.instence(this).$(this, R.id.sv_coach_score);
        mTvCoachLocation = Util.instence(this).$(this, R.id.tv_coach_location);
        mLlyCoachLocation = Util.instence(this).$(this, R.id.lly_coach_location);
        mTvDistance = Util.instence(this).$(this, R.id.tv_distance);
        mLvPayment = Util.instence(this).$(this, R.id.lv_payment);
        mTvSurePay = Util.instence(this).$(this, R.id.tv_sure_pay);
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        //价格
        mRlyNormalPrice = Util.instence(this).$(this, R.id.rly_normal_price);
        mIvSelectNormal = Util.instence(this).$(this, R.id.iv_select_normal);
        mTvNormalPrice = Util.instence(this).$(this, R.id.tv_normal_price);
        mTvOldNormalPrice = Util.instence(this).$(this, R.id.tv_old_normal_price);
        mTvNormalPriceDetail = Util.instence(this).$(this, R.id.tv_normal_price_detail);
        mRlyVIPPrice = Util.instence(this).$(this, R.id.rly_vip_price);
        mIvSelectVIP = Util.instence(this).$(this, R.id.iv_select_vip);
        mTvVIPPrice = Util.instence(this).$(this, R.id.tv_vip_price);
        mTvOldVIPPrice = Util.instence(this).$(this, R.id.tv_old_vip_price);
        mTvVIPPriceDetail = Util.instence(this).$(this, R.id.tv_vip_price_detail);
        mRlyMorePayment = Util.instence(this).$(this, R.id.rly_more_payment);
    }

    private void loadDatas() {
        //教练信息
        mCoach = (Coach) getIntent().getExtras().getSerializable("coach");
        spUtil = new SharedPreferencesUtil(context);
        mConstants = spUtil.getConstants();
        if (mConstants != null) {
            fieldsList = mConstants.getFields();
            cityList = mConstants.getCities();
            mCostItemList = spUtil.getMyCity().getFixed_cost_itemizer();
        }
        if (spUtil.getLocation() != null) {
            myLat = spUtil.getLocation().getLat();
            myLng = spUtil.getLocation().getLng();
        }
        mSession = spUtil.getUser().getSession();
        mStudent = spUtil.getUser().getStudent();
        //教练姓名
        mTvCoachName.setText(mCoach.getName());
        //教龄
        DecimalFormat dfInt = new DecimalFormat("#####");
        double coachExperiences = 0d;
        if (!TextUtils.isEmpty(mCoach.getExperiences())) {
            coachExperiences = Double.parseDouble(mCoach.getExperiences());
        }
        mTvCoachTeachTime.setText(dfInt.format(coachExperiences) + "年教龄");
        //价格
        mTvCoachActualPrice.setText(Util.getMoney(mCoach.getCoach_group().getTraining_cost()));
        mTvNormalPrice.setText(Util.getMoney(mCoach.getCoach_group().getTraining_cost()));
        mTvOldNormalPrice.setText(Util.getMoney(mCoach.getCoach_group().getMarket_price()));
        mTvOldNormalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        if (mCoach.getVip() == 0) {
            //没有vip的
            mTvCoachVIPPrice.setVisibility(View.GONE);
            mTvCoachVIPPriceLabel.setVisibility(View.GONE);
            mRlyVIPPrice.setVisibility(View.GONE);
            selectClass(0);
        } else {
            mTvCoachVIPPrice.setVisibility(View.VISIBLE);
            mTvCoachVIPPriceLabel.setVisibility(View.VISIBLE);
            mTvCoachVIPPrice.setText(Util.getMoney(mCoach.getCoach_group().getVip_price()));
            mRlyVIPPrice.setVisibility(View.VISIBLE);
            mTvVIPPrice.setText(Util.getMoney(mCoach.getCoach_group().getVip_price()));
            mTvOldVIPPrice.setText(Util.getMoney(mCoach.getCoach_group().getVip_market_price()));
            mTvOldVIPPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            refreshPayButton();
        }
        //头像
        getCoachAvatar(mCoach.getAvatar(), mCivCoachAvatar);
        //金牌教练
        if (mCoach.getSkill_level().equals("1")) {
            mIvIsGoldenCoach.setVisibility(View.VISIBLE);
        } else {
            mIvIsGoldenCoach.setVisibility(View.GONE);
        }
        //评分
        mTvCoachPoints.setText(mCoach.getAverage_rating() + " (" + mCoach.getReview_count() + ")");
        float score = Float.parseFloat(mCoach.getAverage_rating());
        if (score > 5) {
            score = 5;
        }
        mSvCoachScore.setScore(score, false);
        //训练场地址
        if (fieldsList != null) {
            for (FieldModel fieldsModel : fieldsList) {
                if (fieldsModel.getId().equals(mCoach.getCoach_group().getField_id())) {
                    for (City city : cityList) {
                        if (city.getId().equals(fieldsModel.getCity_id())) {
                            mTvCoachLocation.setText(city.getName() + fieldsModel.getSection());
                            break;
                        }
                    }
                    mFieldModel = fieldsModel;
                    break;
                }
            }
        }
        //距离
        if (!TextUtils.isEmpty(myLat) && !TextUtils.isEmpty(myLng) && !TextUtils.isEmpty(mFieldModel.getLat()) && !TextUtils.isEmpty(mFieldModel.getLng())) {
            String kmString = DistanceUtil.getDistanceKm(Double.parseDouble(myLng), Double.parseDouble(myLat), Double.parseDouble(mFieldModel.getLng()), Double.parseDouble(mFieldModel.getLat()));
            String infoText = "距您" + kmString + "km";
            SpannableStringBuilder style = new SpannableStringBuilder(infoText);
            style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.app_theme_color)), 2, 2 + kmString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            mTvDistance.setText(style);
        }
        mLlyCoachLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapDialog = new MapDialog(PurchaseCoachActivity.this, R.style.map_dialog, mFieldModel, v);
                mapDialog.show();
            }
        });
        //支付信息
        loadPaymentMethod();
        mPaymentAdapter = new PaymentAdapter(PurchaseCoachActivity.this, mPaymentList, R.layout.adapter_payment);
        mLvPayment.setAdapter(mPaymentAdapter);
        setListViewHeightBasedOnChildren(mLvPayment);
    }

    private void initEvents() {
        mIbtnBack.setOnClickListener(mClickListener);
        mLvPayment.setOnItemClickListener(mItemClickListener);
        mTvSurePay.setOnClickListener(mClickListener);
        mTvNormalPriceDetail.setOnClickListener(mClickListener);
        mTvVIPPriceDetail.setOnClickListener(mClickListener);
        mIvSelectNormal.setOnClickListener(mClickListener);
        mIvSelectVIP.setOnClickListener(mClickListener);
        mRlyMorePayment.setOnClickListener(mClickListener);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (mPaymentList.size() > i) {
                Payment selPayment = mPaymentList.get(i);
                if (!selPayment.isSelect() && mPaymentList.get(i).isActive()) {
                    for (Payment payment : mPaymentList) {
                        if (payment.isActive()) {
                            payment.setSelect(false);
                        }
                    }
                    mPaymentList.get(i).setSelect(true);
                    mPaymentAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ibtn_back:
                    PurchaseCoachActivity.this.finish();
                    break;
                case R.id.tv_sure_pay:
                    //确认支付
                    pay();
                    break;
                case R.id.tv_normal_price_detail:
                    //正常价格明细
                    FeeDetailDialog feeDetailDialog = new FeeDetailDialog(PurchaseCoachActivity.this, mCostItemList, mCoach.getCoach_group().getTraining_cost(), "1", null);
                    feeDetailDialog.show();
                    break;
                case R.id.tv_vip_price_detail:
                    //VIP价格明细
                    feeDetailDialog = new FeeDetailDialog(PurchaseCoachActivity.this, mCostItemList, mCoach.getCoach_group().getVip_price(), "1", null);
                    feeDetailDialog.show();
                    break;
                case R.id.iv_select_normal:
                    selectClass(0);
                    break;
                case R.id.iv_select_vip:
                    selectClass(1);
                    break;
                case R.id.rly_more_payment:
                    //更多支付方式
                    loadMorePaymentMethod();
                default:
                    break;
            }
        }
    };

    private void getCoachAvatar(String url, CircleImageView civCoachAvatar) {
        final int iconWidth = Util.instence(context).dip2px(60);
        final int iconHeight = iconWidth;
        Picasso.with(context).load(url).resize(iconWidth, iconHeight)
                .into(civCoachAvatar);
    }

    /**
     * 支付
     */
    private void pay() {
        //调用获取charge
        //method:0 是alipay 1 是分期乐
        String method = "";
        for (Payment payment : mPaymentList) {
            if (payment.isSelect()) {
                method = String.valueOf(mPaymentList.indexOf(payment));
            }
        }
        if (pd != null) {
            pd.dismiss();
        }
        pd = ProgressDialog.show(PurchaseCoachActivity.this, null, "跳转中，请稍后……");
        fcPresenter.createCharge(mCoach.getId(), mSession.getAccess_token(), method, mProductType,
                new FCCallbackListener<String>() {
                    @Override
                    public void onSuccess(String charge) {
                        pd.dismiss();
                        //调用ping++
                        try {
                            Intent intent = new Intent(PurchaseCoachActivity.this, PaymentActivity.class);
                            intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
                            startActivityForResult(intent, 1);
                        }catch (Exception e){
                        }

                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        pd.dismiss();
                    }
                });
    }

    private void loadPaymentMethod() {
        mPaymentList = new ArrayList<>();
        Payment alipay = new Payment(R.drawable.ic_alipay_icon, "支付宝", "推荐有支付宝账号的用户使用", true, true);
        Payment fqlpay = new Payment(R.drawable.logo_fenqile, "分期乐", "推荐分期使用", false, true);
        mPaymentList.add(alipay);
        mPaymentList.add(fqlpay);
    }

    private void loadMorePaymentMethod() {
        Payment wxpay = new Payment(R.drawable.ic_wechatpay_icon, "微信支付", "", false, false);
        Payment cardPay = new Payment(R.drawable.ic_cardpay_icon, "银行卡支付", "推荐有支付宝账号的用户使用", false, false);
        mPaymentList.add(wxpay);
        mPaymentList.add(cardPay);
        mRlyMorePayment.setVisibility(View.GONE);
        mPaymentAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(mLvPayment);
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
        //支付页面返回处理
        if (requestCode == 1) {
            if (pd != null) {
                pd.dismiss();
            }
            pd = ProgressDialog.show(PurchaseCoachActivity.this, null, "数据加载中，请稍后……");
            if (resultCode == Activity.RESULT_OK) {
                if (null == spUtil) {
                    spUtil = new SharedPreferencesUtil(this);
                }
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - 支付成功
                 * "fail"    - 支付失败
                 * "cancel"  - 取消支付
                 * "invalid" - 支付插件未安装（一般是微信客户端未安装的情况）
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                if (result.equals("success")) {
                    //更新SharedPreferences中的student
                    this.msPresenter.getStudentForever(mStudent.getId(), mSession.getAccess_token(), new MSCallbackListener<Student>() {
                        @Override
                        public void onSuccess(Student data) {
                            mStudent = data;
                            User user = spUtil.getUser();
                            user.setStudent(mStudent);
                            spUtil.setUser(user);
                            if (!TextUtils.isEmpty(data.getCurrent_coach_id())) {
                                fcPresenter.getCoach(data.getCurrent_coach_id(), new FCCallbackListener<Coach>() {
                                    @Override
                                    public void onSuccess(Coach coach) {
                                        if (pd != null) {
                                            pd.dismiss();
                                        }
                                        MobclickAgent.onEvent(context, "did_purchase_coach");
                                        spUtil.setCurrentCoach(coach);
                                        setResult(RESULT_OK, null);
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(String errorEvent, String message) {
                                        if (pd != null) {
                                            pd.dismiss();
                                        }
                                    }
                                });
                            } else {
                                if (pd != null) {
                                    pd.dismiss();
                                }
                                MobclickAgent.onEvent(context, "did_purchase_coach");
                                setResult(RESULT_OK, null);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(String errorEvent, String message) {
                            if (pd != null) {
                                pd.dismiss();
                            }
                        }
                    });
                } else if (result.equals("cancel")) {
                    if (pd != null) {
                        pd.dismiss();
                    }
                    Toast.makeText(context, "取消支付", Toast.LENGTH_SHORT).show();
                } else if (result.equals("invalid")) {
                    if (pd != null) {
                        pd.dismiss();
                    }
                    Toast.makeText(context, "支付插件未安装", Toast.LENGTH_SHORT).show();
                } else {
                    if (pd != null) {
                        pd.dismiss();
                    }
                    Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 选择班级
     *
     * @param selectClass
     */
    private void selectClass(int selectClass) {
        if (mProductType.equals(String.valueOf(selectClass))) {
            return;
        } else {
            mProductType = String.valueOf(selectClass);
        }
        mIvSelectVIP.setImageDrawable(ContextCompat.getDrawable(PurchaseCoachActivity.this, R.drawable.ic_cashout_unchack_btn));
        mIvSelectNormal.setImageDrawable(ContextCompat.getDrawable(PurchaseCoachActivity.this, R.drawable.ic_cashout_unchack_btn));
        if (mProductType.equals("0")) {
            mIvSelectNormal.setImageDrawable(ContextCompat.getDrawable(PurchaseCoachActivity.this, R.drawable.ic_cashout_chack_btn));
        } else if (mProductType.equals("1")) {
            mIvSelectVIP.setImageDrawable(ContextCompat.getDrawable(PurchaseCoachActivity.this, R.drawable.ic_cashout_chack_btn));
        }
        refreshPayButton();
    }

    /**
     * 付款按钮控制
     */
    private void refreshPayButton() {
        if (!TextUtils.isEmpty(mProductType)) {
            mTvSurePay.setAlpha(1);
            mTvSurePay.setClickable(true);
        } else {
            mTvSurePay.setAlpha(0.6f);
            mTvSurePay.setClickable(false);
        }
    }
}
