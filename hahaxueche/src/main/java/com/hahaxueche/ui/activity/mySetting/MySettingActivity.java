package com.hahaxueche.ui.activity.mySetting;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.student.PaymentStage;
import com.hahaxueche.model.student.PurchasedService;
import com.hahaxueche.model.user.Session;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.base.BaseApiResponse;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.ui.activity.appointment.AppointmentActivity;
import com.hahaxueche.ui.activity.base.BaseWebViewActivity;
import com.hahaxueche.ui.activity.findCoach.FindCoachActivity;
import com.hahaxueche.ui.activity.findCoach.MyCoachActivity;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.ui.activity.signupLogin.StartActivity;
import com.hahaxueche.ui.dialog.BaseConfirmDialog;
import com.hahaxueche.ui.dialog.FAQDialog;
import com.hahaxueche.ui.dialog.RegisterInfoPhotoDialog;
import com.hahaxueche.ui.dialog.mySetting.EditUsernameDialog;
import com.hahaxueche.ui.util.PhotoUtil;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.ui.widget.monitorScrollView.MonitorScrollView;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by gibxin on 2016/1/27.
 */
public class MySettingActivity extends MSBaseActivity {
    private LinearLayout llyTabIndex;
    private LinearLayout llyTabFindCoach;
    private LinearLayout llyTabAppointment;
    private LinearLayout llyTabMySetting;
    private CircleImageView cirMyAvatar;
    private RelativeLayout rllCustomerService;
    private TextView tvBackLogin;//跳转登录
    private LinearLayout llyNotLogin;//未登录页面
    private MonitorScrollView msvMain;
    private boolean isLogin = false;
    private Student mStudent;
    private PurchasedService mPurchasedService;
    private TextView tvStuName;
    private TextView tvUnpaidAmount;
    private RelativeLayout rlyMyFollowCoach;
    private RelativeLayout rlyMyCoach;
    private RelativeLayout rlyPaymentStage;
    private TextView tvPaymentStage;
    private ImageView ivPaymentStage;
    private LinearLayout llyLoginOff;
    private RelativeLayout rlyCustomerPhone;
    private RelativeLayout rlyAboutHaha;
    private RelativeLayout mRlyMyCoupon;//我的礼金券
    private View vwMyCoach;
    private ProgressDialog pd;//进度框
    private Session mSession;
    private SharedPreferencesUtil spUtil;
    private PhotoUtil mPhotoUtil;
    private SwipeRefreshLayout mSrlMySetting;
    private boolean isRefresh = false;//是否刷新中
    private RelativeLayout mRlyRefererFriends;
    private RelativeLayout mRlyStuFAQ;//学员常见问题
    private RelativeLayout mRlySupportHaha;//支持小哈
    private RelativeLayout mRlySoftwareInfo;//软件信息
    private FAQDialog faqDialog = null;
    private TextView mTvStudentPhase;
    private ImageView mIvEditUsername;//修改用户名
    private EditUsernameDialog mEditUsernameDialog;
    private String mAlbumPicturePath = null;
    private static final int PERMISSIONS_REQUEST = 600;
    private static final int PERMISSIONS_REQUEST_CELL_PHONE = 601;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spUtil = new SharedPreferencesUtil(this);
        mPhotoUtil = new PhotoUtil(this);
        setContentView(R.layout.activity_my_setting);
        initView();
        initEvent();
        loadDatas(false);
    }

    private void initView() {
        llyTabIndex = Util.instence(this).$(this, R.id.lly_tab_index);
        llyTabFindCoach = Util.instence(this).$(this, R.id.lly_tab_find_coach);
        llyTabAppointment = Util.instence(this).$(this, R.id.lly_tab_appointment);
        llyTabMySetting = Util.instence(this).$(this, R.id.lly_tab_my_setting);
        cirMyAvatar = Util.instence(this).$(this, R.id.cir_my_avatar);
        rllCustomerService = Util.instence(this).$(this, R.id.rll_customer_service_qq);
        tvBackLogin = Util.instence(this).$(this, R.id.tv_back_login);
        llyNotLogin = Util.instence(this).$(this, R.id.lly_not_login);
        msvMain = Util.instence(this).$(this, R.id.msv_main);
        tvStuName = Util.instence(this).$(this, R.id.tv_stu_name);
        tvUnpaidAmount = Util.instence(this).$(this, R.id.tv_unpaid_amount);
        rlyMyFollowCoach = Util.instence(this).$(this, R.id.rly_my_follow_coach);
        rlyMyCoach = Util.instence(this).$(this, R.id.rly_my_coach);
        rlyPaymentStage = Util.instence(this).$(this, R.id.rly_payment_stage);
        tvPaymentStage = Util.instence(this).$(this, R.id.tv_payment_stage);
        ivPaymentStage = Util.instence(this).$(this, R.id.iv_payment_stage);
        llyLoginOff = Util.instence(this).$(this, R.id.lly_login_off);
        rlyCustomerPhone = Util.instence(this).$(this, R.id.rly_customer_phone);
        rlyAboutHaha = Util.instence(this).$(this, R.id.rly_about_haha);
        vwMyCoach = Util.instence(this).$(this, R.id.vw_my_coach);
        mSrlMySetting = Util.instence(this).$(this, R.id.srl_my_setting);
        mRlyRefererFriends = Util.instence(this).$(this, R.id.rly_referer_friends);
        mRlyStuFAQ = Util.instence(this).$(this, R.id.rly_FAQ);
        mRlySupportHaha = Util.instence(this).$(this, R.id.rly_support_haha);
        mRlySoftwareInfo = Util.instence(this).$(this, R.id.rly_software_info);
        mTvStudentPhase = Util.instence(this).$(this, R.id.tv_student_phase);
        mIvEditUsername = Util.instence(this).$(this, R.id.iv_edit_username);
        mRlyMyCoupon = Util.instence(this).$(this, R.id.rly_my_coupon);
    }

    private void initEvent() {
        llyTabIndex.setOnClickListener(mClickListener);
        llyTabFindCoach.setOnClickListener(mClickListener);
        llyTabAppointment.setOnClickListener(mClickListener);
        llyTabMySetting.setOnClickListener(mClickListener);
        rllCustomerService.setOnClickListener(mClickListener);
        tvBackLogin.setOnClickListener(mClickListener);
        rlyMyFollowCoach.setOnClickListener(mClickListener);
        rlyMyCoach.setOnClickListener(mClickListener);
        rlyPaymentStage.setOnClickListener(mClickListener);
        llyLoginOff.setOnClickListener(mClickListener);
        rlyCustomerPhone.setOnClickListener(mClickListener);
        rlyAboutHaha.setOnClickListener(mClickListener);
        cirMyAvatar.setOnClickListener(mClickListener);
        mSrlMySetting.setOnRefreshListener(mRefreshListener);
        mSrlMySetting.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mRlyRefererFriends.setOnClickListener(mClickListener);
        mRlyStuFAQ.setOnClickListener(mClickListener);
        mRlySupportHaha.setOnClickListener(mClickListener);
        mRlySoftwareInfo.setOnClickListener(mClickListener);
        mIvEditUsername.setOnClickListener(mClickListener);
        mRlyMyCoupon.setOnClickListener(mClickListener);
    }

    private void loadDatas(boolean useCachePolicy) {
        mSession = spUtil.getUser().getSession();
        mStudent = spUtil.getUser().getStudent();
        if (mSession != null && mStudent != null && !TextUtils.isEmpty(mSession.getId()) && !TextUtils.isEmpty(mStudent.getId())) {
            isLogin = true;
        }
        if (isLogin) {
            llyNotLogin.setVisibility(View.GONE);
            msvMain.setVisibility(View.VISIBLE);
            mSrlMySetting.setVisibility(View.VISIBLE);
            tvStuName.setText(mStudent.getName());
            if (!TextUtils.isEmpty(mStudent.getAvatar())) {
                //头像
                int iconWidth = Util.instence(this).dip2px(90);
                int iconHeight = iconWidth;
                if (useCachePolicy) {
                    Picasso.with(this).invalidate(mStudent.getAvatar());
                    Picasso.with(this).load(mStudent.getAvatar()).resize(iconWidth, iconHeight).into(cirMyAvatar);
                } else {
                    Picasso.with(this).load(mStudent.getAvatar()).resize(iconWidth, iconHeight).into(cirMyAvatar);
                }
            }
            if (mStudent.getPurchased_services() != null && mStudent.getPurchased_services().size() > 0) {
                //有pruchased service，目前默认取第一个
                mPurchasedService = mStudent.getPurchased_services().get(0);
                //账户余额
                tvUnpaidAmount.setText(Util.getMoney(mPurchasedService.getUnpaid_amount()));
                List<PaymentStage> paymentStageList = mPurchasedService.getPayment_stages();
                String tempPaymentStageStr = "";
                for (PaymentStage paymentStage : paymentStageList) {
                    if (paymentStage.getStage_number().equals(mPurchasedService.getCurrent_payment_stage())) {
                        tempPaymentStageStr = paymentStage.getStage_name();
                        break;
                    }
                }
                if (TextUtils.isEmpty(tempPaymentStageStr)) {
                    if (paymentStageList.size() + 1 == Integer.parseInt(mPurchasedService.getCurrent_payment_stage())) {
                        tempPaymentStageStr = "已拿证";
                    }
                }
                tvPaymentStage.setText(tempPaymentStageStr);
                mTvStudentPhase.setText("目前阶段：" + tempPaymentStageStr);
                rlyMyCoach.setVisibility(View.VISIBLE);
                ivPaymentStage.setVisibility(View.VISIBLE);
                vwMyCoach.setVisibility(View.VISIBLE);
                rlyMyCoach.setClickable(true);
                rlyPaymentStage.setClickable(true);
            } else {
                tvUnpaidAmount.setText(Util.getMoney(mStudent.getBonus_balance()));
                tvPaymentStage.setText("未选择教练");
                mTvStudentPhase.setText("目前阶段：未付款");
                rlyMyCoach.setVisibility(View.GONE);
                ivPaymentStage.setVisibility(View.GONE);
                vwMyCoach.setVisibility(View.GONE);
                rlyPaymentStage.setClickable(false);
                rlyMyCoach.setClickable(false);
            }
        } else {
            llyNotLogin.setVisibility(View.VISIBLE);
            msvMain.setVisibility(View.GONE);
            mSrlMySetting.setVisibility(View.GONE);
        }


    }

    View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lly_tab_appointment:
                    Intent intent = new Intent(getApplication(), AppointmentActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.lly_tab_index:
                    intent = new Intent(getApplication(), IndexActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.lly_tab_find_coach:
                    intent = new Intent(getApplication(), FindCoachActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                //QQ客服
                case R.id.rll_customer_service_qq:
                    onlineAsk(MySettingActivity.this);
                    break;
                case R.id.tv_back_login:
                    intent = new Intent(getApplication(), StartActivity.class);
                    intent.putExtra("isBack", "1");
                    startActivity(intent);
                    break;
                //我的教练
                case R.id.rly_my_coach:
                    if (mStudent != null && !TextUtils.isEmpty(mStudent.getCurrent_coach_id())) {
                        intent = new Intent(getApplication(), MyCoachActivity.class);
                        intent.putExtra("coach_id", mStudent.getCurrent_coach_id());
                        startActivity(intent);
                    }
                    break;
                //我关注的教练
                case R.id.rly_my_follow_coach:
                    intent = new Intent(getApplication(), FollowCoachListActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rly_payment_stage:
                    intent = new Intent(getApplication(), PaymentStageActivity.class);
                    startActivity(intent);
                    break;
                //退出登录
                case R.id.lly_login_off:
                    logOff();
                    break;
                //客服热线
                case R.id.rly_customer_phone:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CELL_PHONE);
                        //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                    } else {
                        // Android version is lesser than 6.0 or the permission is already granted.
                        contactService();
                    }
                    break;
                //关于哈哈
                case R.id.rly_about_haha:
                    intent = new Intent(getApplication(), BaseWebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("url", "http://staging.hahaxueche.net/#/student");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.rly_referer_friends:
                    //推荐朋友,为哈哈代言
                    intent = new Intent(getApplication(), ReferFriendsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rly_FAQ:
                    //学员常见问题
                    intent = new Intent(getApplication(), FAQActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rly_support_haha:
                    Uri uri = Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.hahaxueche");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                case R.id.rly_software_info:
                    intent = new Intent(getApplication(), SoftwareInfoActivity.class);
                    startActivity(intent);
                    break;
                case R.id.cir_my_avatar:
                    // Check the SDK version and whether the permission is already granted or not.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                    || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_REQUEST);
                        //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                    } else {
                        // Android version is lesser than 6.0 or the permission is already granted.
                        showPhotoDialog();
                    }
                    break;
                case R.id.iv_edit_username:
                    if (null == mEditUsernameDialog) {
                        mEditUsernameDialog = new EditUsernameDialog(MySettingActivity.this, mEditUsernameSaveListener);
                    }
                    mEditUsernameDialog.show();
                    break;
                case R.id.rly_my_coupon:
                    navigateToCoupon();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showPhotoDialog();
            } else {
                Toast.makeText(this, "请允许读写sdcard权限，不然我们无法完成头像采集操作", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_CELL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactService();
            } else {
                Toast.makeText(this, "请允许拨打电话权限，不然无法直接拨号联系客服", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPhotoDialog() {
        RegisterInfoPhotoDialog dialog = new RegisterInfoPhotoDialog(MySettingActivity.this);
        dialog.show();
    }

    private EditUsernameDialog.OnEditUsernameSaveListener mEditUsernameSaveListener = new EditUsernameDialog.OnEditUsernameSaveListener() {
        @Override
        public boolean saveUserName(String username) {
            msPresenter.editUsername(mStudent.getId(), mStudent.getCity_id(), username, mSession.getAccess_token(), new MSCallbackListener<Student>() {
                @Override
                public void onSuccess(Student student) {
                    Toast.makeText(MySettingActivity.this, "用户名修改成功！", Toast.LENGTH_SHORT).show();
                    refreshStudent();
                }

                @Override
                public void onFailure(String errorEvent, String message) {

                }
            });
            return true;
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PhotoUtil.SELECT_A_PICTURE) {
            if (resultCode == RESULT_OK && null != data) {
                //4.4以下的;
                Bitmap bitmap = mPhotoUtil.decodeUriAsBitmap(Uri.fromFile(new File(PhotoUtil.IMGPATH,
                        PhotoUtil.TMP_IMAGE_FILE_NAME)));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MySettingActivity.this, "取消头像设置", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PhotoUtil.SELECET_A_PICTURE_AFTER_KIKAT) {
            if (resultCode == RESULT_OK && null != data) {
                cropImageUriAfterKikat(data);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MySettingActivity.this, "取消头像设置", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PhotoUtil.SET_ALBUM_PICTURE_KITKAT) {

            Log.i("lgx", "4.4以上上的 RESULT_OK");
            if (resultCode == RESULT_OK && null != data) {
                //Bitmap bitmap = mPhotoUtil.decodeUriAsBitmap(Uri.fromFile(new File(PhotoUtil.IMGPATH,
                //        PhotoUtil.TMP_IMAGE_FILE_NAME)));
                uploadAvatar();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MySettingActivity.this, "取消头像设置", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PhotoUtil.TAKE_A_PICTURE) {
            Log.i("lgx", "TAKE_A_PICTURE-resultCode:" + resultCode);
            if (resultCode == RESULT_OK) {
                mPhotoUtil.cameraCropImageUri(Uri.fromFile(new File(PhotoUtil.IMGPATH, PhotoUtil.IMAGE_FILE_NAME)),
                        Util.instence(MySettingActivity.this).dip2px(RegisterInfoPhotoDialog.output_X),
                        Util.instence(MySettingActivity.this).dip2px(RegisterInfoPhotoDialog.output_Y));
            } else {
                Toast.makeText(MySettingActivity.this, "取消头像设置", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PhotoUtil.SET_PICTURE) {
            //拍照的设置头像  不考虑版本
            Bitmap bitmap = null;
            if (resultCode == RESULT_OK && null != data) {
                if (mPhotoUtil.uritempFile != null) {
                    try {
                        bitmap = BitmapFactory
                                .decodeStream(getContentResolver().openInputStream(mPhotoUtil.uritempFile));
                        uploadAvatar();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MySettingActivity.this, "取消头像设置", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MySettingActivity.this, "设置头像失败", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void cropImageUriAfterKikat(Intent data) {
        mAlbumPicturePath = mPhotoUtil.getPath(getApplicationContext(), data.getData());
        mPhotoUtil.cropImageUriAfterKikat(Uri.fromFile(new File(mAlbumPicturePath)),
                Util.instence(MySettingActivity.this).dip2px(RegisterInfoPhotoDialog.output_X),
                Util.instence(MySettingActivity.this).dip2px(RegisterInfoPhotoDialog.output_Y));
    }

    private void uploadAvatar() {
        pd = ProgressDialog.show(MySettingActivity.this, null, "头像上传中，请稍后……");
        this.msPresenter.uploadAvatar(mStudent.getId(), mSession.getAccess_token(), PhotoUtil.IMGPATH + "/" + PhotoUtil.IMAGE_FILE_NAME, new MSCallbackListener<Student>() {
            @Override
            public void onSuccess(Student data) {
                Toast.makeText(MySettingActivity.this, "头像修改成功！", Toast.LENGTH_SHORT).show();
                refreshStudent();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                if (pd != null) {
                    pd.dismiss();
                }
            }
        });
    }

    SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!isRefresh) {
                isRefresh = true;
                /*new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mSrlMySetting.setRefreshing(false);
                        refreshStudent();
                        isRefresh = false;

                    }
                }, 1500);*/
                refreshStudent();
            }
        }
    };

    /**
     * 刷新用户信息
     */
    private void refreshStudent() {
        this.msPresenter.getStudent(mStudent.getId(), mSession.getAccess_token(), new MSCallbackListener<Student>() {
            @Override
            public void onSuccess(Student student) {
                User user = spUtil.getUser();
                user.setStudent(student);
                spUtil.setUser(user);
                loadDatas(true);
                if (pd != null) {
                    pd.dismiss();
                }
                mSrlMySetting.setRefreshing(false);
                isRefresh = false;
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                if (pd != null) {
                    pd.dismiss();
                }
                mSrlMySetting.setRefreshing(false);
                isRefresh = false;
            }
        });
    }

    /**
     * 联系客服
     */
    private void contactService() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4000016006"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    private void logOff() {
        BaseConfirmDialog baseConfirmDialog = new BaseConfirmDialog(MySettingActivity.this, "哈哈学车", "是否退出登录？", "", "", "确定", "取消", new BaseConfirmDialog.onConfirmListener() {
            @Override
            public boolean clickConfirm() {
                if (pd != null) {
                    pd.dismiss();
                }
                pd = ProgressDialog.show(MySettingActivity.this, null, "退出中，请稍后……");
                msPresenter.loginOff(mSession.getId(), mSession.getAccess_token(), new MSCallbackListener<BaseApiResponse>() {
                    @Override
                    public void onSuccess(BaseApiResponse data) {
                        if (pd != null) {
                            pd.dismiss();
                        }
                        spUtil.clearUser();
                        spUtil.clearCurrentCoach();
                        Intent intent = new Intent(getApplication(), StartActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        if (pd != null) {
                            pd.dismiss();
                        }
                    }
                });
                return true;
            }
        }, new BaseConfirmDialog.onCancelListener() {
            @Override
            public boolean clickCancel() {
                return false;
            }
        });
        baseConfirmDialog.show();
    }

    /**
     * 跳转到我的礼金券
     */
    private void navigateToCoupon() {
        if (isLogin) {
            Intent intent = new Intent(MySettingActivity.this, MyCouponActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(context, "对不起,您还没有礼金券", Toast.LENGTH_SHORT).show();
        }
    }
}
