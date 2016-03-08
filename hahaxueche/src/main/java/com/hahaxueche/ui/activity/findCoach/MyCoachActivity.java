package com.hahaxueche.ui.activity.findCoach;

import com.hahaxueche.ui.widget.imageSwitcher.ImageSwitcher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hahaxueche.R;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.model.findCoach.FieldModel;
import com.hahaxueche.model.signupLogin.CityModel;
import com.hahaxueche.model.signupLogin.CostItem;
import com.hahaxueche.model.util.BaseKeyValue;
import com.hahaxueche.model.util.ConstantsModel;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.ui.dialog.FeeDetailDialog;
import com.hahaxueche.ui.dialog.ZoomImgDialog;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.ui.widget.monitorScrollView.MonitorScrollView;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;
/**
 * Created by gibxin on 2016/3/7.
 */
public class MyCoachActivity extends FCBaseActivity implements ImageSwitcher.OnSwitchItemClickListener {
    private MonitorScrollView msvCoachDetail;
    private CircleImageView civCdCoachAvatar;//教练头像
    private TextView tvCdCoachName;//教练姓名
    private TextView tvCdCoachDescription;//教练描述
    private ImageSwitcher isCdCoachDetail;//教练照片
    private ZoomImgDialog zoomImgDialog = null;
    private ImageButton ibtnCoachDetialBack;//回退按钮
    private CoachModel mCoach;//教练
    private ProgressDialog pd;//进度框
    private ImageView ivIsGoldenCoach;
    private RelativeLayout llyTakeCertCost;//拿证价格
    private ConstantsModel mConstants;
    private TextView tvTakeCertPrice;
    private TextView tvTrainLocation;
    private TextView tvLicenseType;
    private boolean isLogin = false;
    private String access_token;
    private FeeDetailDialog feeDetailDialog;
    private List<CostItem> mCostItemList;
    //训练场地
    private RelativeLayout llyTrainLoaction;
    private FieldModel mFieldModel;
    private TextView tvTeachCourse;
    private RelativeLayout rlyMyCoachContact;
    private TextView tvMyCoachContact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coach_detail);
        initSharedPreferences();
        initView();
        initEvent();
        loadDatas();
    }

    private void initView() {
        msvCoachDetail = Util.instence(this).$(this, R.id.msv_my_coach_detail);
        civCdCoachAvatar = Util.instence(this).$(this, R.id.cir_my_cd_coach_avatar);

        tvCdCoachName = Util.instence(this).$(this, R.id.tv_my_cd_coach_name);
        tvCdCoachDescription = Util.instence(this).$(this, R.id.tv_my_cd_coach_description);
        ibtnCoachDetialBack = Util.instence(this).$(this, R.id.ibtn_my_coach_detail_back);
        ivIsGoldenCoach = Util.instence(this).$(this, R.id.iv_my_cd_is_golden_coach);
        tvTakeCertPrice = Util.instence(this).$(this, R.id.tv_my_coach_fee_detail);
        tvTrainLocation = Util.instence(this).$(this, R.id.tv_my_cd_coach_location);
        tvLicenseType = Util.instence(this).$(this, R.id.tv_my_coach_course_type);
        isCdCoachDetail = Util.instence(this).$(this, R.id.is_my_cd_coach_switcher);

        llyTakeCertCost = Util.instence(this).$(this, R.id.rly_my_coach_fee_detail);
        llyTrainLoaction = Util.instence(this).$(this, R.id.rly_my_coach_location);
        tvTeachCourse = Util.instence(this).$(this, R.id.tv_my_coach_teach_course);
        rlyMyCoachContact = Util.instence(this).$(this, R.id.rly_my_coach_contact);
        tvMyCoachContact =  Util.instence(this).$(this, R.id.tv_my_cd_coach_phone);
    }

    private void initEvent() {
        isCdCoachDetail.setIndicatorRadius(Util.instence(this).dip2px(3));
        isCdCoachDetail.setIndicatorDivide(Util.instence(this).dip2px(15));
        isCdCoachDetail.setOnSwitchItemClickListener(this);
        ibtnCoachDetialBack.setOnClickListener(mClickListener);
        llyTakeCertCost.setOnClickListener(mClickListener);
        llyTrainLoaction.setOnClickListener(mClickListener);
        rlyMyCoachContact.setOnClickListener(mClickListener);
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
            pd = ProgressDialog.show(MyCoachActivity.this, null, "数据加载中，请稍后……");
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

    }

    /**
     * 加载详情
     */
    private void loadDetail() {
        tvCdCoachName.setText(mCoach.getName());
        tvCdCoachDescription.setText(mCoach.getBio());
        getCoachAvatar(mCoach.getAvatar(), civCdCoachAvatar);
        isCdCoachDetail.updateImages(mCoach.getImages());
        tvMyCoachContact.setText(mCoach.getCell_phone());
        //金牌教练显示
        if (mCoach.getSkill_level().equals("1")) {
            ivIsGoldenCoach.setVisibility(View.VISIBLE);
        } else {
            ivIsGoldenCoach.setVisibility(View.GONE);
        }
        tvTakeCertPrice.setText(Util.getMoney(mCoach.getCoach_group().getTraining_cost()));
        //训练场地址
        List<CityModel> cityList = mConstants.getCities();
        List<FieldModel> fieldList = mConstants.getFields();
        for (FieldModel field : fieldList) {
            if (field.getId().equals(mCoach.getCoach_group().getField_id())) {
                for (CityModel city : cityList) {
                    if (city.getId().equals(field.getCity_id())) {
                        tvTrainLocation.setText(city.getName() + field.getSection() + field.getStreet());
                        mFieldModel = field;
                        break;
                    }
                }
            }
        }
        if (mCoach.getLicense_type().equals("1")) {
            tvLicenseType.setText("C1手动档");
        } else if (mCoach.getLicense_type().equals("2")) {
            tvLicenseType.setText("C2自动档");
        } else {
            tvLicenseType.setText("C1手动档，C2自动挡");
        }
        //教授课程
        List<BaseKeyValue> serviceTypeList = mConstants.getService_types();
        for (BaseKeyValue serviceType : serviceTypeList) {
            if(serviceType.getId().equals(mCoach.getService_type())){
                tvTeachCourse.setText(serviceType.getReadable_name());
                break;
            }
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
                case R.id.ibtn_my_coach_detail_back:
                    MyCoachActivity.this.finish();
                    break;
                //拿证价格
                case R.id.rly_my_coach_fee_detail:
                    feeDetailDialog = new FeeDetailDialog(MyCoachActivity.this, mCostItemList, mCoach.getCoach_group().getTraining_cost(), "1",
                            new FeeDetailDialog.OnBtnClickListener() {
                                @Override
                                public void onPay() {
                                    feeDetailDialog.dismiss();
                                }
                            });
                    feeDetailDialog.show();
                    break;
                //训练场
                case R.id.rly_my_coach_location:
                    Intent intent = new Intent(MyCoachActivity.this, FieldMapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("fieldModel", mFieldModel);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                //联系方式
                case R.id.rly_my_coach_contact:
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+mCoach.getCell_phone()));
                    startActivity(intent);
                    break;
            }
        }
    };


    /**
     * SharedPreferences 数据，初始化处理
     */
    private void initSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("constants", Activity.MODE_PRIVATE);
        String constantsStr = sharedPreferences.getString("constants", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ConstantsModel>() {
        }.getType();
        mConstants = gson.fromJson(constantsStr, type);
        SharedPreferences spSession = getSharedPreferences("session", Activity.MODE_PRIVATE);
        //根据当前登录人的cityid，加载费用明细列表
        List<CityModel> cityList = mConstants.getCities();
        String cityId = spSession.getString("city_id", "0");
        for (CityModel city : cityList) {
            if (city.getId().equals(cityId)) {
                mCostItemList = city.getFixed_cost_itemizer();
                break;
            }
        }
    }

}
