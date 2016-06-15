package com.hahaxueche.ui.activity.findCoach;

import com.hahaxueche.model.student.Student;
import com.hahaxueche.ui.dialog.ShareAppDialog;
import com.hahaxueche.ui.widget.imageSwitcher.ImageSwitcher;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.coach.Coach;
import com.hahaxueche.model.city.FieldModel;
import com.hahaxueche.model.city.City;
import com.hahaxueche.model.city.CostItem;
import com.hahaxueche.model.base.BaseKeyValue;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.ui.dialog.FeeDetailDialog;
import com.hahaxueche.ui.dialog.ZoomImgDialog;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.ui.widget.monitorScrollView.MonitorScrollView;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

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
    private Coach mCoach;//教练
    private ProgressDialog pd;//进度框
    private ImageView ivIsGoldenCoach;
    private RelativeLayout llyTakeCertCost;//拿证价格
    private Constants mConstants;
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
    private ImageView mIvShare;
    private ShareAppDialog shareAppDialog;
    private static final int PERMISSIONS_REQUEST_CELL_PHONE = 601;

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
        tvMyCoachContact = Util.instence(this).$(this, R.id.tv_my_cd_coach_phone);
        mIvShare = Util.instence(this).$(this, R.id.iv_my_coach_share);
    }

    private void initEvent() {
        isCdCoachDetail.setIndicatorRadius(Util.instence(this).dip2px(3));
        isCdCoachDetail.setIndicatorDivide(Util.instence(this).dip2px(15));
        isCdCoachDetail.setOnSwitchItemClickListener(this);
        ibtnCoachDetialBack.setOnClickListener(mClickListener);
        llyTakeCertCost.setOnClickListener(mClickListener);
        llyTrainLoaction.setOnClickListener(mClickListener);
        rlyMyCoachContact.setOnClickListener(mClickListener);
        mIvShare.setOnClickListener(mClickListener);
    }

    /**
     * 数据加载
     */
    private void loadDatas() {
        Intent intent = getIntent();
        if (intent.getSerializableExtra("coach") != null) {
            mCoach = (Coach) intent.getSerializableExtra("coach");
            loadDetail();
        } else {
            String coach_id = getIntent().getStringExtra("coach_id");
            if (pd != null) {
                pd.dismiss();
            }
            pd = ProgressDialog.show(MyCoachActivity.this, null, "数据加载中，请稍后……");
            this.fcPresenter.getCoach(coach_id, new FCCallbackListener<Coach>() {
                @Override
                public void onSuccess(Coach coach) {
                    if (pd != null) {
                        pd.dismiss();
                    }
                    mCoach = coach;
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
        tvMyCoachContact.setText(mCoach.getCell_phone());
        //金牌教练显示
        if (mCoach.getSkill_level().equals("1")) {
            ivIsGoldenCoach.setVisibility(View.VISIBLE);
        } else {
            ivIsGoldenCoach.setVisibility(View.GONE);
        }
        tvTakeCertPrice.setText(Util.getMoney(mCoach.getCoach_group().getTraining_cost()));
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
            if (serviceType.getId().equals(mCoach.getService_type())) {
                tvTeachCourse.setText(serviceType.getReadable_name());
                break;
            }
        }
    }

    private void getCoachAvatar(String url, CircleImageView civCoachAvatar) {
        final int iconWidth = Util.instence(this).dip2px(70);
        final int iconHeight = iconWidth;
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(this).load(url).resize(iconWidth, iconHeight)
                    .into(civCoachAvatar);
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CELL_PHONE);
                        //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                    } else {
                        // Android version is lesser than 6.0 or the permission is already granted.
                        contactCoach();
                    }
                    break;
                case R.id.iv_my_coach_share:
                    showShareAppDialog();
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * SharedPreferences 数据，初始化处理
     */
    private void initSharedPreferences() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        //根据当前登录人的cityid，加载费用明细列表
        mCostItemList = spUtil.getMyCity().getFixed_cost_itemizer();
        mConstants = spUtil.getConstants();
    }

    private void showShareAppDialog() {
        if (pd != null) {
            pd.dismiss();
        }
        pd = ProgressDialog.show(MyCoachActivity.this, null, "数据加载中，请稍后……");
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
                    shareAppDialog = new ShareAppDialog(MyCoachActivity.this, url, mCoach);
                    shareAppDialog.show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CELL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactCoach();
            } else {
                Toast.makeText(this, "请允许拨打电话权限，不然无法直接拨号联系教练", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 联系客服
     */
    private void contactCoach() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCoach.getCell_phone()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

}
