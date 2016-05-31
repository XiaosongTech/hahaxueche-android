package com.hahaxueche.ui.activity.mySetting;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.hahaxueche.share.ShareConstants;
import com.hahaxueche.ui.activity.appointment.AppointmentActivity;
import com.hahaxueche.ui.activity.base.BaseWebViewActivity;
import com.hahaxueche.ui.activity.findCoach.FindCoachActivity;
import com.hahaxueche.ui.activity.findCoach.MyCoachActivity;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.ui.activity.signupLogin.StartActivity;
import com.hahaxueche.ui.dialog.FAQDialog;
import com.hahaxueche.ui.dialog.RegisterInfoPhotoDialog;
import com.hahaxueche.ui.dialog.mySetting.EditUsernameDialog;
import com.hahaxueche.ui.util.PhotoUtil;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.ui.widget.monitorScrollView.MonitorScrollView;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;
import com.tencent.tauth.Tencent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private RelativeLayout rllCustomerServiceQQ;
    private Tencent mTencent;//QQ
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
    private View vwMyCoach;
    private ProgressDialog pd;//进度框
    private Session mSession;
    private SharedPreferencesUtil spUtil;
    private String mPhotoPath;
    private PhotoUtil mPhotoUtil;
    private SwipeRefreshLayout mSrlMySetting;
    private boolean isRefresh = false;//是否刷新中
    private RelativeLayout mRlyRefererFriends;
    private RelativeLayout mRlyCachOut;
    private RelativeLayout mRlyStuFAQ;//学员常见问题
    private RelativeLayout mRlySupportHaha;//支持小哈
    private RelativeLayout mRlySoftwareInfo;//软件信息
    private FAQDialog faqDialog = null;
    private TextView mTvStudentPhase;
    private ImageView mIvEditUsername;//修改用户名
    private EditUsernameDialog mEditUsernameDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spUtil = new SharedPreferencesUtil(this);
        mPhotoUtil = new PhotoUtil(this);
        setContentView(R.layout.activity_my_setting);
        mTencent = Tencent.createInstance(ShareConstants.APP_ID_QQ, MySettingActivity.this);
        initView();
        initEvent();
        loadDatas();
    }

    private void initView() {
        llyTabIndex = Util.instence(this).$(this, R.id.lly_tab_index);
        llyTabFindCoach = Util.instence(this).$(this, R.id.lly_tab_find_coach);
        llyTabAppointment = Util.instence(this).$(this, R.id.lly_tab_appointment);
        llyTabMySetting = Util.instence(this).$(this, R.id.lly_tab_my_setting);
        cirMyAvatar = Util.instence(this).$(this, R.id.cir_my_avatar);
        rllCustomerServiceQQ = Util.instence(this).$(this, R.id.rll_customer_service_qq);
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
        mRlyCachOut = Util.instence(this).$(this, R.id.rly_cash_out);
        mRlyStuFAQ = Util.instence(this).$(this, R.id.rly_FAQ);
        mRlySupportHaha = Util.instence(this).$(this, R.id.rly_support_haha);
        mRlySoftwareInfo = Util.instence(this).$(this, R.id.rly_software_info);
        mTvStudentPhase = Util.instence(this).$(this, R.id.tv_student_phase);
        mIvEditUsername = Util.instence(this).$(this, R.id.iv_edit_username);
    }

    private void initEvent() {
        llyTabIndex.setOnClickListener(mClickListener);
        llyTabFindCoach.setOnClickListener(mClickListener);
        llyTabAppointment.setOnClickListener(mClickListener);
        llyTabMySetting.setOnClickListener(mClickListener);
        rllCustomerServiceQQ.setOnClickListener(mClickListener);
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
        mRlyCachOut.setOnClickListener(mClickListener);
        mRlyStuFAQ.setOnClickListener(mClickListener);
        mRlySupportHaha.setOnClickListener(mClickListener);
        mRlySoftwareInfo.setOnClickListener(mClickListener);
        mIvEditUsername.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
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
            //头像
            int iconWidth = Util.instence(this).dip2px(90);
            int iconHeight = iconWidth;
            Picasso.with(this).load(mStudent.getAvatar()).resize(iconWidth, iconHeight).into(cirMyAvatar);
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
                    int ret = mTencent.startWPAConversation(MySettingActivity.this, ShareConstants.CUSTOMER_SERVICE_QQ, "");
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
                    break;
                //客服热线
                case R.id.rly_customer_phone:
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4000016006"));
                    if (ActivityCompat.checkSelfPermission(MySettingActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(intent);
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
                    //推荐朋友
                    intent = new Intent(getApplication(), ReferFriendsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rly_cash_out:
                    //提现
                    intent = new Intent(getApplication(), MakeMoneyInfoActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rly_FAQ:
                    //学员常见问题
                    if (faqDialog == null)
                        faqDialog = new FAQDialog(MySettingActivity.this);
                    faqDialog.show();
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
                    RegisterInfoPhotoDialog dialog = new RegisterInfoPhotoDialog(MySettingActivity.this);
                    dialog.show();
                    break;
                case R.id.iv_edit_username:
                    if (null == mEditUsernameDialog) {
                        mEditUsernameDialog = new EditUsernameDialog(MySettingActivity.this, mEditUsernameSaveListener);
                    }
                    mEditUsernameDialog.show();
                default:
                    break;
            }
        }
    };

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {//相机
            if (resultCode == RESULT_OK && null != data) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = mPhotoUtil.resizeBitmapByWidth((Bitmap) bundle.get("data"), 300);
                FileOutputStream b = null;
                String str = null;
                Date date = null;
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");// 获取当前时间，进一步转化为字符串
                date = new Date(System.currentTimeMillis());
                str = format.format(date);
                mPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "haha" + File.separator +
                        "icon_cache" + File.separator + str + ".jpg";
                File photo = new File(mPhotoPath);
                photo.getParentFile().mkdirs();
                if (!photo.exists()) {
                    try {
                        photo.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    b = new FileOutputStream(mPhotoPath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        b.flush();
                        b.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //mPhotoPath = mPhotoUtil.getPath(getApplicationContext(), uri);
                Toast.makeText(this, "照片拍摄成功！", Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MySettingActivity.this, "取消头像设置", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 200) {//从图库选择
            if (resultCode == RESULT_OK && null != data) {
                Uri uri = data.getData();
                Log.v("gibxin", "onActivityResult : uri -> " + uri);
                Bitmap bitmap = mPhotoUtil.resizeBitmapByWidth(mPhotoUtil.decodeUriAsBitmap(uri), 300);
                FileOutputStream b = null;
                String str = null;
                Date date = null;
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");// 获取当前时间，进一步转化为字符串
                date = new Date(System.currentTimeMillis());
                str = format.format(date);
                mPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "haha" + File.separator +
                        "icon_cache" + File.separator + str + ".jpg";
                File photo = new File(mPhotoPath);
                photo.getParentFile().mkdirs();
                if (!photo.exists()) {
                    try {
                        photo.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    b = new FileOutputStream(mPhotoPath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        b.flush();
                        b.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //mPhotoPath = mPhotoUtil.getPath(getApplicationContext(), uri);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MySettingActivity.this, "取消头像设置", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        pd = ProgressDialog.show(MySettingActivity.this, null, "头像上传中，请稍后……");
        this.msPresenter.uploadAvatar(mStudent.getId(), mSession.getAccess_token(), mPhotoPath, new MSCallbackListener<Student>() {
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
                loadDatas();
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
}
