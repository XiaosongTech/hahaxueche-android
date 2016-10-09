package com.hahaxueche.ui.fragment.myPage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.user.Consultant;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.presenter.myPage.MyPagePresenter;
import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.activity.myPage.FAQActivity;
import com.hahaxueche.ui.activity.myPage.FollowListActivity;
import com.hahaxueche.ui.activity.myPage.ReferFriendsActivity;
import com.hahaxueche.ui.activity.myPage.SoftwareInfoActivity;
import com.hahaxueche.ui.dialog.AvatarDialog;
import com.hahaxueche.ui.dialog.community.MyConsultantDialog;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.myPage.MyPageView;
import com.hahaxueche.util.PhotoUtil;
import com.hahaxueche.util.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by wangshirui on 16/9/13.
 */
public class MyPageFragment extends HHBaseFragment implements MyPageView, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.lly_not_login)
    LinearLayout mLlyNotLogin;
    @BindView(R.id.srl_my_page)
    SwipeRefreshLayout mSrlMyPage;
    @BindView(R.id.iv_my_avatar)
    SimpleDraweeView mIvMyAvatar;
    @BindView(R.id.tv_student_name)
    TextView mTvStudentName;
    @BindView(R.id.tv_account_balance)
    TextView mTvAccountBalance;
    @BindView(R.id.tv_payment_stage)
    TextView mTvPaymentStage;
    @BindView(R.id.tv_student_phase)
    TextView mTvStudentPhase;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;

    private MyPagePresenter mPresenter;
    private MainActivity mActivity;
    private static final int PERMISSIONS_REQUEST_SDCARD = 600;
    private static final int PERMISSIONS_REQUEST_CELL_PHONE = 601;
    private static final String WEB_URL_ABOUT_HAHA = "http://staging.hahaxueche.net/#/student";
    private static final String URL_APP_STORE = "http://a.app.qq.com/o/simple.jsp?pkgname=com.hahaxueche";
    private PhotoUtil mPhotoUtil;
    private String mAlbumPicturePath = null;
    private MyConsultantDialog mConsultantDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mPresenter = new MyPagePresenter();
        mPhotoUtil = new PhotoUtil(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);
        ButterKnife.bind(this, view);
        mPresenter.attachView(this);
        mSrlMyPage.setOnRefreshListener(this);
        mSrlMyPage.setColorSchemeResources(R.color.app_theme_color);
        return view;
    }

    @OnClick(R.id.tv_logout)
    public void logOut() {
        mPresenter.logOut();
    }

    @Override
    public void showNotLoginView() {
        mLlyNotLogin.setVisibility(View.VISIBLE);
        mSrlMyPage.setVisibility(View.GONE);
    }

    @Override
    public void showLoggedInView() {
        mLlyNotLogin.setVisibility(View.GONE);
        mSrlMyPage.setVisibility(View.VISIBLE);
    }

    @Override
    public void loadStudentInfo(Student student) {
        mIvMyAvatar.setImageURI(student.avatar);
        mTvStudentName.setText(student.name);
        mTvAccountBalance.setText(Utils.getMoney(student.getAccountBalance()));
        mTvPaymentStage.setText(student.getPaymentStageLabel());
        mTvStudentPhase.setText(student.getStudentPhaseLabel());
    }

    @Override
    @OnClick(R.id.tv_back_login)
    public void finishToStartLogin() {
        ActivityCollector.finishAll();
        Intent intent = new Intent(getContext(), StartLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void startRefresh() {
        mSrlMyPage.setRefreshing(true);
    }

    @Override
    public void stopRefresh() {
        mSrlMyPage.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        mPresenter.fetchStudent();
    }

    @OnClick(R.id.rly_online_service)
    public void onlineAsk() {
        mPresenter.onlineAsk();
    }

    @OnClick(R.id.rly_my_follow_coach)
    public void navigateToFollwList() {
        startActivity(new Intent(getContext(), FollowListActivity.class));
    }

    @OnClick(R.id.rly_my_consultant)
    public void clickMyConsultant() {
        if (mConsultantDialog == null) {
            Consultant consultant = new Consultant();
            consultant.name = "余寒";
            consultant.description = "\"自打入宫以来,就独得学员恩宠\"";
            consultant.avatar = "http://haha-production.oss-cn-hangzhou.aliyuncs.com/uploads/coach/avatar/c4aa92c9-5e2b-41ca-9ed8-b21d752338a3/15902721430ava.JPG";
            mConsultantDialog = new MyConsultantDialog(getContext(), consultant, new MyConsultantDialog.onMyConsultantListener() {
                @Override
                public boolean call() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mActivity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CELL_PHONE);
                        //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                    } else {
                        // Android version is lesser than 6.0 or the permission is already granted.
                        callMyConsultant();
                    }
                    return true;
                }
            });
        }
        mConsultantDialog.show();
    }

    @OnClick(R.id.rly_FAQ)
    public void navigateToFAQ() {
        startActivity(new Intent(getContext(), FAQActivity.class));
    }


    @OnClick(R.id.rly_support_haha)
    public void supportHaha() {
        Uri uri = Uri.parse(URL_APP_STORE);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.rly_software_info)
    public void navigateToSoftwareInfo() {
        startActivity(new Intent(getContext(), SoftwareInfoActivity.class));
    }

    @OnClick(R.id.rly_referer_friends)
    public void navigateToReferFriends() {
        startActivity(new Intent(getContext(), ReferFriendsActivity.class));
    }

    @OnClick(R.id.iv_my_avatar)
    public void clickAvatar() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (mActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || mActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || mActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_SDCARD);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            showAvatarDialog();
        }
    }

    /**
     * 联系客服
     */
    private void callMyConsultant() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4000016006"));
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CELL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                callMyConsultant();
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系客服");
            }
        } else if (requestCode == PERMISSIONS_REQUEST_SDCARD) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showAvatarDialog();
            } else {
                showMessage("请允许读写sdcard权限，不然我们无法完成头像采集操作");
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PhotoUtil.SELECT_A_PICTURE) {
            if (resultCode == RESULT_OK && null != data) {
                //4.4以下的;
                Bitmap bitmap = mPhotoUtil.decodeUriAsBitmap(Uri.fromFile(new File(PhotoUtil.IMGPATH,
                        PhotoUtil.TMP_IMAGE_FILE_NAME)));
            } else if (resultCode == RESULT_CANCELED) {
                showMessage("取消头像设置");
            }
        } else if (requestCode == PhotoUtil.SELECET_A_PICTURE_AFTER_KIKAT) {
            if (resultCode == RESULT_OK && null != data) {
                cropImageUriAfterKikat(data);
            } else if (resultCode == RESULT_CANCELED) {
                showMessage("取消头像设置");
            }
        } else if (requestCode == PhotoUtil.SET_ALBUM_PICTURE_KITKAT) {
            if (resultCode == RESULT_OK && null != data) {
                //Bitmap bitmap = mPhotoUtil.decodeUriAsBitmap(Uri.fromFile(new File(PhotoUtil.IMGPATH,
                //        PhotoUtil.TMP_IMAGE_FILE_NAME)));
                mPresenter.uploadAvatar();
            } else if (resultCode == RESULT_CANCELED) {
                showMessage("取消头像设置");
            }
        } else if (requestCode == PhotoUtil.TAKE_A_PICTURE) {
            if (resultCode == RESULT_OK) {
                mPhotoUtil.cameraCropImageUri(Uri.fromFile(new File(PhotoUtil.IMGPATH, PhotoUtil.IMAGE_FILE_NAME)),
                        Utils.instence(getContext()).dip2px(AvatarDialog.output_X),
                        Utils.instence(getContext()).dip2px(AvatarDialog.output_Y));
            } else {
                showMessage("取消头像设置");
            }
        } else if (requestCode == PhotoUtil.SET_PICTURE) {
            //拍照的设置头像  不考虑版本
            Bitmap bitmap = null;
            if (resultCode == RESULT_OK && null != data) {
                if (mPhotoUtil.uritempFile != null) {
                    mPresenter.uploadAvatar();
                }
            } else if (resultCode == RESULT_CANCELED) {
                showMessage("取消头像设置");
            } else {
                showMessage("设置头像失败");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void cropImageUriAfterKikat(Intent data) {
        mAlbumPicturePath = mPhotoUtil.getPath(getContext(), data.getData());
        mPhotoUtil.cropImageUriAfterKikat(Uri.fromFile(new File(mAlbumPicturePath)),
                Utils.instence(getContext()).dip2px(AvatarDialog.output_X),
                Utils.instence(getContext()).dip2px(AvatarDialog.output_Y));
    }

    private void showAvatarDialog() {
        AvatarDialog dialog = new AvatarDialog(this);
        dialog.show();
    }
}
