package com.hahaxueche.ui.activity.community;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.community.ExamLibraryPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.community.ExamLibraryPageAdapter;
import com.hahaxueche.ui.dialog.BaseAlertSimpleDialog;
import com.hahaxueche.ui.dialog.ShareAppDialog;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.view.community.ExamLibraryView;
import com.hahaxueche.util.ExamLib;
import com.hahaxueche.util.RequestCode;
import com.hahaxueche.util.WebViewUrl;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shaohui.shareutil.ShareUtil;
import me.shaohui.shareutil.share.ShareListener;
import me.shaohui.shareutil.share.SharePlatform;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class ExamLibraryActivity extends HHBaseActivity implements ExamLibraryView {
    ImageView mIvBack;
    TextView mTvTitle;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.iv_pass)
    ImageView mIvPass;
    @BindView(R.id.lly_not_login)
    LinearLayout mLlyNotLogin;
    @BindView(R.id.lly_scores)
    LinearLayout mLlyScores;
    @BindView(R.id.tv_insurance_count)
    TextView mTvInsuranceCount;
    @BindView(R.id.iv_score1)
    ImageView mIvScore1;
    @BindView(R.id.iv_score2)
    ImageView mIvScore2;
    @BindView(R.id.iv_score3)
    ImageView mIvScore3;
    @BindView(R.id.iv_score4)
    ImageView mIvScore4;
    @BindView(R.id.iv_score5)
    ImageView mIvScore5;
    @BindView(R.id.tv_pass_score_text)
    TextView mTvPassScoreText;
    @BindView(R.id.sv_main)
    ScrollView mSvMain;
    @BindView(R.id.tv_share_scores)
    TextView mTvShareScores;
    @BindView(R.id.tv_to_exam)
    TextView mTvToExam;
    private ShareAppDialog mShareDialog;
    private ExamLibraryPresenter mPresenter;
    /*****************
     * 分享
     ******************/
    private ShareDialog shareDialog;

    /*****************
     * end
     ******************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ExamLibraryPresenter();
        setContentView(R.layout.activity_exam_library);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        ExamLibraryPageAdapter adapter = new ExamLibraryPageAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        HHBaseApplication application = HHBaseApplication.get(getContext());
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(getContext(), "online_test_page_viewed", map);
        } else {
            MobclickAgent.onEvent(getContext(), "online_test_page_viewed");
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        mShareDialog = null;
        super.onDestroy();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExamLibraryActivity.this.finish();
            }
        });
        mTvTitle.setText("科一挂科险");
    }

    @OnClick({R.id.tv_share_scores,
            R.id.tv_to_exam,
            R.id.iv_pass,
            R.id.tv_get_pass_ensurance})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_share_scores:
                if (shareDialog == null) {
                    shareDialog = new ShareDialog(getContext(), new ShareDialog.OnShareListener() {
                        @Override
                        public void onShare(int shareType) {
                            mPresenter.generateQrCodeUrl(shareType);
                        }
                    });
                }
                shareDialog.show();
                break;
            case R.id.tv_to_exam:
                Intent intent = new Intent(this, StartExamActivity.class);
                intent.putExtra("examType", ExamLib.EXAM_TYPE_1);
                startActivityForResult(intent, RequestCode.REQUEST_CODE_START_EXAM);
                break;
            case R.id.iv_pass:
                BaseAlertSimpleDialog dialog = new BaseAlertSimpleDialog(getContext(), "什么是挂科险？",
                        "在哈哈学车平台上注册登录，即可获得挂科险。\n学员在哈哈学车平台报名后，通过哈哈学车APP模拟科目一考试5次成绩均在90分以上，" +
                                "并分享至第三方平台即可发起理赔，当科目一考试未通过可凭借成绩单获得全额赔付120元。");
                dialog.show();
                break;
            case R.id.tv_get_pass_ensurance:
                openWebView(WebViewUrl.WEB_URL_BAOGUOKA);
                break;
            default:
                break;
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mSvMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void share(int shareType) {
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
            case 5:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCode.PERMISSIONS_REQUEST_SEND_SMS);
                } else {
                    shareToSms();
                }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.REQUEST_CODE_EXAM_ACTIVITY) {
            if (resultCode == RESULT_OK && data.getBooleanExtra("isShowShare", false)) {
                if (mShareDialog == null) {
                    String shareText = getResources().getString(R.string.upload_share_dialog_text);
                    mShareDialog = new ShareAppDialog(getContext(), shareText, true, null);
                }
                mShareDialog.show();
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_START_EXAM) {
            mPresenter.fetchScores();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showNotLogin() {
        mIvPass.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.protectioncard_noget));
        mLlyNotLogin.setVisibility(View.VISIBLE);
        mLlyScores.setVisibility(View.GONE);
    }

    @Override
    public void showScores(int passCount) {
        mIvPass.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.protectioncard_get));
        mLlyNotLogin.setVisibility(View.GONE);
        mLlyScores.setVisibility(View.VISIBLE);
        mIvScore1.setImageDrawable(ContextCompat.getDrawable(this, passCount > 0 ? R.drawable.ic_hahapass1 : R.drawable.ic_nopass1));
        mIvScore2.setImageDrawable(ContextCompat.getDrawable(this, passCount > 1 ? R.drawable.ic_hahapass2 : R.drawable.ic_nopass2));
        mIvScore3.setImageDrawable(ContextCompat.getDrawable(this, passCount > 2 ? R.drawable.ic_hahapass3 : R.drawable.ic_nopass3));
        mIvScore4.setImageDrawable(ContextCompat.getDrawable(this, passCount > 3 ? R.drawable.ic_hahapass4 : R.drawable.ic_nopass4));
        mIvScore5.setImageDrawable(ContextCompat.getDrawable(this, passCount > 4 ? R.drawable.ic_hahapass5 : R.drawable.ic_nopass5));
        if (passCount > 0) {
            mTvPassScoreText.setText("您已在" + (passCount > 5 ? 5 : passCount) + "次模拟考试中获得90分以上的成绩。");
            mTvShareScores.setVisibility(View.VISIBLE);
            mTvToExam.setVisibility(View.GONE);
        } else {
            mTvShareScores.setVisibility(View.GONE);
            mTvToExam.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setInsuranceCount(SpannableString ss) {
        mTvInsuranceCount.setText(ss);
    }

    private void shareToQQ() {
        ShareUtil.shareImage(this, SharePlatform.QQ, mPresenter.getQrCodeUrl(), new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                showMessage("分享成功");
            }

            @Override
            public void shareFailure(Exception e) {
                showMessage("分享失败");
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                showMessage("取消分享");
            }
        });
    }

    private void shareToQZone() {
        ShareUtil.shareImage(this, SharePlatform.QZONE, mPresenter.getQrCodeUrl(), new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                showMessage("分享成功");
            }

            @Override
            public void shareFailure(Exception e) {
                showMessage("分享失败");
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                showMessage("取消分享");
            }
        });
    }

    private void shareToWeibo() {
        ShareUtil.shareImage(this, SharePlatform.WEIBO, mPresenter.getQrCodeUrl(), new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                showMessage("分享成功");
            }

            @Override
            public void shareFailure(Exception e) {
                showMessage("分享失败");
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                showMessage("取消分享");
            }
        });
    }

    private void shareToWeixin() {
        ShareUtil.shareImage(this, SharePlatform.WX, mPresenter.getQrCodeUrl(), new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                showMessage("分享成功");
            }

            @Override
            public void shareFailure(Exception e) {
                showMessage("分享失败");
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                showMessage("取消分享");
            }
        });
    }

    private void shareToFriendCircle() {
        ShareUtil.shareImage(this, SharePlatform.WX_TIMELINE, mPresenter.getQrCodeUrl(), new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                showMessage("分享成功");
            }

            @Override
            public void shareFailure(Exception e) {
                showMessage("分享失败");
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                showMessage("取消分享");
            }
        });
    }

    private void shareToSms() {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(mPresenter.getQrCodeUrl()))
                .setProgressiveRenderingEnabled(true)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchDecodedImage(imageRequest, null);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                if (bitmap != null) {
                    // 首先保存图片
                    File appDir = new File(Environment.getExternalStorageDirectory(), "hahaxueche");
                    if (!appDir.exists()) {
                        appDir.mkdir();
                    }
                    String fileName = "qrcode.jpg";
                    File file = new File(appDir, fileName);
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 其次把文件插入到系统图库
                    try {
                        MediaStore.Images.Media.insertImage(getContentResolver(),
                                file.getAbsolutePath(), fileName, null);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    File imageFile = new File(Environment.getExternalStorageDirectory() +
                            "/hahaxueche/qrcode.jpg");
                    Uri uriToImage;
                    if (Build.VERSION.SDK_INT >= 24) {
                        uriToImage = FileProvider.getUriForFile(getContext(),
                                "com.hahaxueche.provider.fileProvider", imageFile);
                    } else {
                        uriToImage = Uri.fromFile(imageFile);
                    }
                    // 最后通知图库更新
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uriToImage));
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                    intent.setType("image/jpg");
                    startActivity(intent);
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {

            }
        }, CallerThreadExecutor.getInstance());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                shareToSms();
            } else {
                showMessage("请允许发送短信权限，不然无法分享到短信");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
