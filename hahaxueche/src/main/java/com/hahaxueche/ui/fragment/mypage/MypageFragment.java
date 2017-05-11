package com.hahaxueche.ui.fragment.myPage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.presenter.myPage.MyPagePresenter;
import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.base.BaseWebViewActivity;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.activity.findCoach.PaySuccessActivity;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.activity.myPage.CourseActivity;
import com.hahaxueche.ui.activity.myPage.FAQActivity;
import com.hahaxueche.ui.activity.myPage.FollowListActivity;
import com.hahaxueche.ui.activity.myPage.MyCoachDetailActivity;
import com.hahaxueche.ui.activity.myPage.MyContractActivity;
import com.hahaxueche.ui.activity.myPage.MyInsuranceActivity;
import com.hahaxueche.ui.activity.myPage.MyVoucherActivity;
import com.hahaxueche.ui.activity.myPage.NoCourseActivity;
import com.hahaxueche.ui.activity.myPage.NotLoginVoucherActivity;
import com.hahaxueche.ui.activity.myPage.PassEnsuranceActivity;
import com.hahaxueche.ui.activity.myPage.PaymentStageActivity;
import com.hahaxueche.ui.activity.myPage.PurchaseInsuranceActivity;
import com.hahaxueche.ui.activity.myPage.ReferFriendsActivity;
import com.hahaxueche.ui.activity.myPage.SoftwareInfoActivity;
import com.hahaxueche.ui.activity.myPage.StudentReferActivity;
import com.hahaxueche.ui.activity.myPage.UploadIdCardActivity;
import com.hahaxueche.ui.dialog.AvatarDialog;
import com.hahaxueche.ui.dialog.BaseConfirmSimpleDialog;
import com.hahaxueche.ui.dialog.community.MyAdviserDialog;
import com.hahaxueche.ui.dialog.myPage.EditUsernameDialog;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.myPage.MyPageView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.PhotoUtil;
import com.hahaxueche.util.RequestCode;
import com.hahaxueche.util.Utils;
import com.hahaxueche.util.WebViewUrl;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.grantland.widget.AutofitTextView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by wangshirui on 16/9/13.
 */
public class MypageFragment extends HHBaseFragment implements MyPageView {
    @BindView(R.id.iv_my_avatar)
    SimpleDraweeView mIvMyAvatar;
    @BindView(R.id.tv_student_name)
    TextView mTvStudentName;
    @BindView(R.id.iv_edit_username)
    ImageView mIvEditUsername;
    @BindView(R.id.tv_login)
    TextView mTvLogin;
    @BindView(R.id.tv_account_balance)
    TextView mTvAccountBalance;
    @BindView(R.id.tv_payment_stage)
    TextView mTvPaymentStage;
    @BindView(R.id.tv_student_phase)
    TextView mTvStudentPhase;
    @BindView(R.id.iv_payment_stage)
    ImageView mIvPaymentArrow;
    @BindView(R.id.view_badge)
    View mViewBadge;
    @BindView(R.id.iv_more_voucher)
    ImageView mIvMoreVoucher;
    @BindView(R.id.view_badge_contract)
    View mViewBadgeContract;
    @BindView(R.id.iv_more_contract)
    ImageView mIvMoreContract;
    @BindView(R.id.tv_logout)
    TextView mTvLogout;
    @BindView(R.id.tv_to_login)
    TextView mTvToLogin;
    @BindView(R.id.view_badge_pass_ensurance)
    View mViewBadgePassEnsurance;
    @BindView(R.id.iv_more_pass_ensurance)
    ImageView mIvMorePassEnsurance;
    @BindView(R.id.crl_main)
    CoordinatorLayout mCrlMain;
    @BindView(R.id.tv_refer_friends)
    AutofitTextView mTvReferFriends;

    private MyPagePresenter mPresenter;
    private MainActivity mActivity;
    private PhotoUtil mPhotoUtil;
    private MyAdviserDialog mConsultantDialog;
    private EditUsernameDialog mEditUsernameDialog;

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
        return view;
    }

    @Override
    public void showNotLogin() {
        mTvStudentName.setVisibility(View.GONE);
        mIvEditUsername.setVisibility(View.GONE);
        mTvLogin.setVisibility(View.VISIBLE);
        mTvLogout.setVisibility(View.GONE);
        mTvToLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLogin() {
        mTvStudentName.setVisibility(View.VISIBLE);
        mIvEditUsername.setVisibility(View.VISIBLE);
        mTvLogin.setVisibility(View.GONE);
        mTvLogout.setVisibility(View.VISIBLE);
        mTvToLogin.setVisibility(View.GONE);
    }

    @Override
    public void loadStudentInfo(Student student) {
        mIvMyAvatar.setImageURI(student.avatar);
        mTvStudentName.setText(student.name);
        mTvAccountBalance.setText(Utils.getMoney(student.getAccountBalance()));
        mTvPaymentStage.setText(student.getPaymentStageLabel());
        if (student.isPurchasedService()) {
            mIvPaymentArrow.setVisibility(View.VISIBLE);
        } else {
            mIvPaymentArrow.setVisibility(View.GONE);
        }
        mTvStudentPhase.setText(student.getStudentPhaseLabel());
        mActivity.controlMyPageBadge();
    }

    @Override
    public void finishToStartLogin() {
        ActivityCollector.finishAll();
        Intent intent = new Intent(getContext(), StartLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @OnClick({R.id.rly_online_service,
            R.id.rly_my_follow_coach,
            R.id.rly_my_consultant,
            R.id.rly_FAQ,
            R.id.rly_support_haha,
            R.id.rly_software_info,
            R.id.rly_referer_friends,
            R.id.iv_my_avatar,
            R.id.tv_logout,
            R.id.rly_my_coach,
            R.id.rly_payment_stage,
            R.id.rly_my_course,
            R.id.iv_edit_username,
            R.id.rly_my_voucher,
            R.id.rly_my_contract,
            R.id.tv_login,
            R.id.tv_to_login,
            R.id.rly_pass_ensurance,
            R.id.rly_my_insurance})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rly_online_service:
                mPresenter.onlineAsk();
                break;
            case R.id.rly_my_follow_coach:
                mPresenter.addDataTrack("my_page_my_followed_coach_tapped", getContext());
                if (!mPresenter.isLogin()) {
                    alertToLogin();
                    return;
                }
                startActivity(new Intent(getContext(), FollowListActivity.class));
                break;
            case R.id.rly_my_consultant:
                mPresenter.addDataTrack("my_page_my_advisor_tapped", getContext());
                if (!mPresenter.isLogin()) {
                    mPresenter.openFindAdviser();
                    return;
                }
                if (mConsultantDialog == null) {
                    mConsultantDialog = new MyAdviserDialog(getContext(), mPresenter.getAdviser(), new MyAdviserDialog.onMyConsultantListener() {
                        @Override
                        public boolean call() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mActivity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CUSTOMER_SERVICE);
                                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                            } else {
                                // Android version is lesser than 6.0 or the permission is already granted.
                                callMyConsultant(mPresenter.getAdviser().phone);
                            }
                            return true;
                        }
                    });
                }
                mConsultantDialog.show();
                break;
            case R.id.rly_FAQ:
                mPresenter.addDataTrack("my_page_FAQ_tapped", getContext());
                openWebView(WebViewUrl.WEB_URL_PROCEDURE);
                break;
            case R.id.rly_support_haha:
                mPresenter.addDataTrack("my_page_rate_us_tapped", getContext());
                Uri uri = Uri.parse(WebViewUrl.URL_APP_STORE);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.rly_software_info:
                mPresenter.addDataTrack("my_page_version_check_tapped", getContext());
                startActivity(new Intent(getContext(), SoftwareInfoActivity.class));
                break;
            case R.id.rly_referer_friends:
                mPresenter.clickReferCount();
                break;
            case R.id.iv_my_avatar:
                if (!mPresenter.isLogin()) {
                    alertToLogin();
                    return;
                }
                // Check the SDK version and whether the permission is already granted or not.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        (mActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                || mActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                || mActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, RequestCode.PERMISSIONS_REQUEST_SDCARD_FOR_SAVE_AVATAR);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    // Android version is lesser than 6.0 or the permission is already granted.
                    showAvatarDialog();
                }
                break;
            case R.id.tv_logout:
                BaseConfirmSimpleDialog baseConfirmSimpleDialog = new BaseConfirmSimpleDialog(getContext(),
                        "哈哈学车", "是否退出登录？", "确定", "取消", new BaseConfirmSimpleDialog.onClickListener() {
                    @Override
                    public void clickConfirm() {
                        mPresenter.logOut();
                    }

                    @Override
                    public void clickCancel() {
                    }
                });
                baseConfirmSimpleDialog.show();
                break;
            case R.id.rly_my_coach:
                if (!mPresenter.isLogin()) {
                    alertToLogin();
                    return;
                }
                mPresenter.toMyCoach();
                break;
            case R.id.rly_payment_stage:
                mPresenter.clickPaymentStage();
                break;
            case R.id.rly_my_course:
                mPresenter.clickMyCourse();
                break;
            case R.id.iv_edit_username:
                if (mEditUsernameDialog == null) {
                    mEditUsernameDialog = new EditUsernameDialog(getContext(), new EditUsernameDialog.OnButtonClickListener() {
                        @Override
                        public void save(String username) {
                            mPresenter.editUsername(username);
                        }
                    });
                }
                mEditUsernameDialog.show();
                break;
            case R.id.rly_my_voucher:
                mPresenter.clickMyVoucher();
                break;
            case R.id.rly_my_contract:
                mPresenter.clickMyContract();
                break;
            case R.id.tv_login:
                alertToLogin();
                break;
            case R.id.tv_to_login:
                ActivityCollector.finishAll();
                intent = new Intent(getContext(), StartLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.rly_pass_ensurance:
                mPresenter.clickPassEnsurance();
                break;
            case R.id.rly_my_insurance:
                mPresenter.clickMyInsurance();
                break;
            default:
                break;
        }
    }

    /**
     * 联系客服
     */
    private void callMyConsultant(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mCrlMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void toMyCoach(String coachId) {
        Intent intent = new Intent(getContext(), MyCoachDetailActivity.class);
        intent.putExtra("coachId", coachId);
        startActivity(intent);
    }

    @Override
    public void navigateToPaymentStage() {
        startActivityForResult(new Intent(getContext(), PaymentStageActivity.class), RequestCode.REQUEST_CODE_PAYMENT_STAGE);
    }

    @Override
    public void navigateToNoCourse() {
        startActivityForResult(new Intent(getContext(), NoCourseActivity.class), RequestCode.REQUEST_CODE_NO_COURSE);
    }

    @Override
    public void navigateToMyCourse() {
        startActivity(new Intent(getContext(), CourseActivity.class));
    }

    @Override
    public void navigateToMyVoucher() {
        startActivity(new Intent(getContext(), MyVoucherActivity.class));
    }

    @Override
    public void editUsername(String name) {
        mTvStudentName.setText(name);
    }

    @Override
    public void setVoucherBadge(boolean hasBadge) {
        if (hasBadge) {
            mViewBadge.setVisibility(View.VISIBLE);
            mIvMoreVoucher.setVisibility(View.GONE);
        } else {
            mViewBadge.setVisibility(View.GONE);
            mIvMoreVoucher.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setContractBadge(boolean hasBadge) {
        if (hasBadge) {
            mViewBadgeContract.setVisibility(View.VISIBLE);
            mIvMoreContract.setVisibility(View.GONE);
        } else {
            mViewBadgeContract.setVisibility(View.GONE);
            mIvMoreContract.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void alertToFindCoach() {
        BaseConfirmSimpleDialog dialog = new BaseConfirmSimpleDialog(getContext(), "哈哈学车",
                "您还没有报名哟~\n快去选选心仪的教练报名学车吧~", "去逛逛", "取消", new BaseConfirmSimpleDialog.onClickListener() {
            @Override
            public void clickConfirm() {
                mActivity.selectTab(1);
            }

            @Override
            public void clickCancel() {

            }
        });
        dialog.show();
    }

    @Override
    public void navigateToUploadIdCard() {
        startActivityForResult(new Intent(getContext(), UploadIdCardActivity.class), RequestCode.REQUEST_CODE_UPLOAD_ID_CARD);
    }

    @Override
    public void navigateToSignContract() {
        startActivityForResult(new Intent(getContext(), MyContractActivity.class), RequestCode.REQUEST_CODE_MY_CONTRACT);
    }

    @Override
    public void navigateToMyContract() {
        startActivityForResult(new Intent(getContext(), MyContractActivity.class), RequestCode.REQUEST_CODE_MY_CONTRACT);
    }

    @Override
    public void navigateToNotLoginVoucher() {
        startActivity(new Intent(getContext(), NotLoginVoucherActivity.class));
    }

    @Override
    public void navigateToPassEnsurance() {
        startActivity(new Intent(getContext(), PassEnsuranceActivity.class));
    }

    @Override
    public void setPassEnsuranceBadge(boolean hasBadge) {
        if (hasBadge) {
            mViewBadgePassEnsurance.setVisibility(View.VISIBLE);
            mIvMorePassEnsurance.setVisibility(View.GONE);
        } else {
            mViewBadgePassEnsurance.setVisibility(View.GONE);
            mIvMorePassEnsurance.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setReferText(String text) {
        mTvReferFriends.setText(text);
    }

    @Override
    public void navigateToReferFriends() {
        startActivity(new Intent(getContext(), ReferFriendsActivity.class));
    }

    @Override
    public void navigateToStudentRefer() {
        startActivity(new Intent(getContext(), StudentReferActivity.class));
    }

    @Override
    public void navigateToMyInsurance() {
        startActivityForResult(new Intent(getContext(), MyInsuranceActivity.class), RequestCode.REQUEST_CODE_MY_INSURANCE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CUSTOMER_SERVICE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                callMyConsultant(mPresenter.getAdviser().phone);
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系客服");
            }
        } else if (requestCode == RequestCode.PERMISSIONS_REQUEST_SDCARD_FOR_SAVE_AVATAR) {
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
        if (requestCode == RequestCode.REQUEST_CODE_SELECT_A_PICTURE) {
            if (resultCode == RESULT_OK && null != data) {
                //4.4以下的;
            } else if (resultCode == RESULT_CANCELED) {
                showMessage("取消头像设置");
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_SELECET_A_PICTURE_AFTER_KIKAT) {
            if (resultCode == RESULT_OK && null != data) {
                cropImageUriAfterKikat(data);
            } else if (resultCode == RESULT_CANCELED) {
                showMessage("取消头像设置");
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_SET_ALBUM_PICTURE_KITKAT) {
            if (resultCode == RESULT_OK && null != data) {
                mPresenter.uploadAvatar();
            } else if (resultCode == RESULT_CANCELED) {
                showMessage("取消头像设置");
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_TAKE_A_PICTURE) {
            if (resultCode == RESULT_OK) {
                File imageFile = new File(PhotoUtil.IMGPATH, PhotoUtil.IMAGE_FILE_NAME);
                Uri imageUri;
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(getContext(),
                            "com.hahaxueche.provider.fileProvider", imageFile);
                } else {
                    imageUri = Uri.fromFile(imageFile);
                }
                mPhotoUtil.cameraCropImageUri(imageUri, Utils.instence(getContext()).dip2px(AvatarDialog.output_X),
                        Utils.instence(getContext()).dip2px(AvatarDialog.output_Y));
            } else {
                showMessage("取消头像设置");
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_SET_PICTURE) {
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
        } else if (requestCode == RequestCode.REQUEST_CODE_NO_COURSE) {
            if (resultCode == RESULT_OK && null != data) {
                int tab = data.getIntExtra("showTab", 1);
                mActivity.selectTab(tab);
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_PAYMENT_STAGE) {
            mPresenter.fetchStudent();
        } else if (requestCode == RequestCode.REQUEST_CODE_UPLOAD_ID_CARD) {
            if (resultCode == RESULT_OK) {
                mActivity.controlMyPageBadge();
                mPresenter.toReferFriends();
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_MY_CONTRACT) {
            if (resultCode == RESULT_OK) {//已签订协议
                mActivity.controlMyPageBadge();
                mPresenter.toReferFriends();
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_WEBVIEW) {
            if (resultCode == RESULT_OK && null != data) {
                if (data.getBooleanExtra("peifubao", false)) {
                    navigateToMyInsurance();
                } else {
                    int tab = data.getIntExtra("showTab", 1);
                    mActivity.selectTab(tab);
                }
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_MY_INSURANCE) {
            if (resultCode == RESULT_OK && null != data) {
                if (data.getBooleanExtra("toUploadInfo", false)) {
                    Intent intent = new Intent(getContext(), UploadIdCardActivity.class);
                    intent.putExtra("isFromPaySuccess", false);
                    intent.putExtra("isInsurance", true);
                    startActivityForResult(intent, RequestCode.REQUEST_CODE_UPLOAD_ID_CARD);
                } else if (data.getBooleanExtra("toFindCoach", false)) {
                    mActivity.selectTab(1);
                } else {
                    Intent intent = new Intent(getContext(), PurchaseInsuranceActivity.class);
                    intent.putExtra("insuranceType", data.getIntExtra("insuranceType", Common.PURCHASE_INSURANCE_TYPE_WITHOUT_COACH));
                    startActivityForResult(intent, RequestCode.REQUEST_CODE_PURCHASE_INSURANCE);
                }
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_PURCHASE_INSURANCE) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(getContext(), PaySuccessActivity.class);
                intent.putExtra("isPurchasedInsurance", true);
                intent.putExtra("isFromPurchaseInsurance", true);
                startActivityForResult(intent, RequestCode.REQUEST_CODE_PAY_SUCCESS);
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_PAY_SUCCESS) {
            Intent intent = new Intent(getContext(), UploadIdCardActivity.class);
            intent.putExtra("isFromPaySuccess", false);
            intent.putExtra("isInsurance", true);
            startActivityForResult(intent, RequestCode.REQUEST_CODE_UPLOAD_ID_CARD);
        }
        mPresenter.setContractBadge();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void cropImageUriAfterKikat(Intent data) {
        String mAlbumPicturePath = mPhotoUtil.getPath(getContext(), data.getData());
        File imageFile = new File(mAlbumPicturePath);
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(getContext(),
                    "com.hahaxueche.provider.fileProvider", imageFile);
        } else {
            imageUri = Uri.fromFile(imageFile);
        }
        mPhotoUtil.cropImageUriAfterKikat(imageUri,
                Utils.instence(getContext()).dip2px(AvatarDialog.output_X),
                Utils.instence(getContext()).dip2px(AvatarDialog.output_Y));
    }

    private void showAvatarDialog() {
        AvatarDialog dialog = new AvatarDialog(this);
        dialog.show();
    }

    @Override
    public void alertToLogin() {
        BaseConfirmSimpleDialog dialog = new BaseConfirmSimpleDialog(getContext(), "提示", "请先登录或者注册", "去登录", "知道了",
                new BaseConfirmSimpleDialog.onClickListener() {
                    @Override
                    public void clickConfirm() {
                        ActivityCollector.finishAll();
                        Intent intent = new Intent(getContext(), StartLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void clickCancel() {

                    }
                });
        dialog.show();
    }

    @Override
    public void openWebView(String url) {
        Intent intent = new Intent(getContext(), BaseWebViewActivity.class);
        Bundle bundle = new Bundle();
        HHLog.v("webview url -> " + url);
        bundle.putString("url", url);
        intent.putExtras(bundle);
        startActivityForResult(intent, RequestCode.REQUEST_CODE_WEBVIEW);
    }
}
