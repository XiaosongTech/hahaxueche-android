package com.hahaxueche.ui.activity.findCoach;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.city.City;
import com.hahaxueche.model.city.FieldModel;
import com.hahaxueche.model.coach.Coach;
import com.hahaxueche.model.student.Payment;
import com.hahaxueche.ui.adapter.findCoach.PaymentAdapter;
import com.hahaxueche.ui.dialog.MapDialog;
import com.hahaxueche.ui.util.DistanceUtil;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

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
    private TextView mTvCoachOldPrice;
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
        mTvCoachOldPrice = Util.instence(this).$(this, R.id.tv_coach_old_price);
        mCivCoachAvatar = Util.instence(this).$(this, R.id.cir_coach_avatar);
        mIvIsGoldenCoach = Util.instence(this).$(this, R.id.iv_is_golden_coach);
        mSvCoachScore = Util.instence(this).$(this, R.id.sv_coach_score);
        mTvCoachLocation = Util.instence(this).$(this, R.id.tv_coach_location);
        mLlyCoachLocation = Util.instence(this).$(this, R.id.lly_coach_location);
        mTvDistance = Util.instence(this).$(this, R.id.tv_distance);
        mLvPayment = Util.instence(this).$(this, R.id.lv_payment);
        mTvSurePay = Util.instence(this).$(this, R.id.tv_sure_pay);
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
    }

    private void loadDatas() {
        //教练信息
        mCoach = (Coach) getIntent().getExtras().getSerializable("coach");
        spUtil = new SharedPreferencesUtil(context);
        mConstants = spUtil.getConstants();
        if (mConstants != null) {
            fieldsList = mConstants.getFields();
            cityList = mConstants.getCities();
        }
        if (spUtil.getLocation() != null) {
            myLat = spUtil.getLocation().getLat();
            myLng = spUtil.getLocation().getLng();
        }

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
        mTvCoachOldPrice.setText(Util.getMoney(mCoach.getCoach_group().getMarket_price()));
        mTvCoachOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
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
        //确认支付按钮
        mTvSurePay.setText("确认支付 " + Util.getMoney(mCoach.getCoach_group().getTraining_cost()));
    }

    private void initEvents() {
        mIbtnBack.setOnClickListener(mClickListener);
        mLvPayment.setOnItemClickListener(mItemClickListener);
        mTvSurePay.setOnClickListener(mClickListener);
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
        setResult(RESULT_OK, null);
        finish();
    }

    private void loadPaymentMethod() {
        mPaymentList = new ArrayList<>();
        Payment alipay = new Payment(R.drawable.ic_alipay_icon, "支付宝", "推荐有支付宝账号的用户使用", true, true);
        Payment fqlpay = new Payment(R.drawable.logo_fenqile, "分期乐", "推荐分期使用", false, true);
        Payment wxpay = new Payment(R.drawable.ic_wechatpay_icon, "微信支付", "", false, false);
        Payment cardPay = new Payment(R.drawable.ic_cardpay_icon, "银行卡支付", "推荐有支付宝账号的用户使用", false, false);
        mPaymentList.add(alipay);
        mPaymentList.add(fqlpay);
        mPaymentList.add(wxpay);
        mPaymentList.add(cardPay);
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
}
