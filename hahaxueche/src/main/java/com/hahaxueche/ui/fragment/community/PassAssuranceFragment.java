package com.hahaxueche.ui.fragment.community;

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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.hahaxueche.R;
import com.hahaxueche.presenter.community.PassAssurancePresenter;
import com.hahaxueche.ui.activity.community.ExamLibraryActivity;
import com.hahaxueche.ui.activity.community.StartExamActivity;
import com.hahaxueche.ui.dialog.BaseAlertSimpleDialog;
import com.hahaxueche.ui.dialog.ShareAppDialog;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.community.PassAssuranceView;
import com.hahaxueche.util.ExamLib;
import com.hahaxueche.util.RequestCode;
import com.hahaxueche.util.WebViewUrl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shaohui.shareutil.ShareUtil;
import me.shaohui.shareutil.share.ShareListener;
import me.shaohui.shareutil.share.SharePlatform;

/**
 * Created by wangshirui on 2017/2/28.
 */

public class PassAssuranceFragment extends HHBaseFragment implements PassAssuranceView {
    private ExamLibraryActivity mActivity;
    private PassAssurancePresenter mPresenter;
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
    @BindView(R.id.tv_share_scores)
    TextView mTvShareScores;
    @BindView(R.id.tv_to_exam)
    TextView mTvToExam;
    private ShareAppDialog mShareDialog;
    /*****************
     * 分享
     ******************/
    private ShareDialog shareDialog;

    /*****************
     * end
     ******************/

    public static PassAssuranceFragment newInstance() {
        PassAssuranceFragment fragment = new PassAssuranceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new PassAssurancePresenter();
        mActivity = (ExamLibraryActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pass_assurance, container, false);
        ButterKnife.bind(this, view);
        mPresenter.attachView(this);
        return view;
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
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
                Intent intent = new Intent(getContext(), StartExamActivity.class);
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
        mActivity.showMessage(message);
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
                        (mActivity.checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                                mActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCode.PERMISSIONS_REQUEST_SEND_SMS_FOR_SHARE);
                } else {
                    shareToSms();
                }
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.REQUEST_CODE_EXAM_ACTIVITY) {
            if (resultCode == mActivity.RESULT_OK && data.getBooleanExtra("isShowShare", false)) {
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
        mIvPass.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.protectioncard_noget));
        mLlyNotLogin.setVisibility(View.VISIBLE);
        mLlyScores.setVisibility(View.GONE);
    }

    @Override
    public void showScores(int passCount) {
        mIvPass.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.protectioncard_get));
        mLlyNotLogin.setVisibility(View.GONE);
        mLlyScores.setVisibility(View.VISIBLE);
        mIvScore1.setImageDrawable(ContextCompat.getDrawable(getContext(), passCount > 0 ? R.drawable.ic_hahapass1 : R.drawable.ic_nopass1));
        mIvScore2.setImageDrawable(ContextCompat.getDrawable(getContext(), passCount > 1 ? R.drawable.ic_hahapass2 : R.drawable.ic_nopass2));
        mIvScore3.setImageDrawable(ContextCompat.getDrawable(getContext(), passCount > 2 ? R.drawable.ic_hahapass3 : R.drawable.ic_nopass3));
        mIvScore4.setImageDrawable(ContextCompat.getDrawable(getContext(), passCount > 3 ? R.drawable.ic_hahapass4 : R.drawable.ic_nopass4));
        mIvScore5.setImageDrawable(ContextCompat.getDrawable(getContext(), passCount > 4 ? R.drawable.ic_hahapass5 : R.drawable.ic_nopass5));
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
        ShareUtil.shareImage(getContext(), SharePlatform.QQ, mPresenter.getQrCodeUrl(), new ShareListener() {
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
        ShareUtil.shareImage(getContext(), SharePlatform.QZONE, mPresenter.getQrCodeUrl(), new ShareListener() {
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
        ShareUtil.shareImage(getContext(), SharePlatform.WEIBO, mPresenter.getQrCodeUrl(), new ShareListener() {
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
        ShareUtil.shareImage(getContext(), SharePlatform.WX, mPresenter.getQrCodeUrl(), new ShareListener() {
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
        ShareUtil.shareImage(getContext(), SharePlatform.WX_TIMELINE, mPresenter.getQrCodeUrl(), new ShareListener() {
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
                        MediaStore.Images.Media.insertImage(mActivity.getContentResolver(),
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
                    mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uriToImage));
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
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_SEND_SMS_FOR_SHARE) {
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
