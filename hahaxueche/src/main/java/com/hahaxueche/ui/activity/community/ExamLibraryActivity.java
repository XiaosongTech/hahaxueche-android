package com.hahaxueche.ui.activity.community;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.TextUtils;
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
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.WebViewUrl;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class ExamLibraryActivity extends HHBaseActivity implements ExamLibraryView, IWeiboHandler.Response {
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
    private IWXAPI wxApi; //微信api
    private Tencent mTencent;//QQ
    private IWeiboShareAPI mWeiboShareAPI;//新浪微博
    private HHBaseApplication myApplication;
    private String mTitle;
    private String mDescription;
    private String mImageUrl;
    private String mUrl;
    private ShareQQListener shareQQListener;
    private ShareQZoneListener shareQZoneListener;
    /*****************
     * end
     ******************/
    private static final int PERMISSIONS_REQUEST_SHARE_QQ = 601;
    private static final int PERMISSIONS_REQUEST_SHARE_WX = 602;
    private static final int PERMISSIONS_REQUEST_SHARE_CIRCLE_FRIEND = 603;
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 604;

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
        regShareApi();
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
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
        mTvTitle.setText("科一保过");
    }

    @OnClick({R.id.tv_share_scores,
            R.id.tv_to_exam,
            R.id.iv_pass,
            R.id.tv_get_pass_ensurance})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_share_scores:
                if (TextUtils.isEmpty(mPresenter.getQrCodeUrl())) {
                    return;
                }
                if (shareDialog == null) {
                    shareDialog = new ShareDialog(getContext(), new ShareDialog.OnShareListener() {
                        @Override
                        public void onShare(int shareType) {
                            switch (shareType) {
                                case 0:
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_SHARE_WX);
                                    } else {
                                        shareToWeixin();
                                    }
                                case 1:
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_SHARE_CIRCLE_FRIEND);
                                    } else {
                                        shareToFriendCircle();
                                    }
                                    break;
                                case 2:
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_SHARE_QQ);
                                    } else {
                                        shareToQQ();
                                    }
                                    break;
                                case 3:
                                    shareToWeibo();
                                    break;
                                case 4:
                                    shareToQZone();
                                    break;
                                case 5:
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
                                    } else {
                                        shareToSms();
                                    }
                                default:
                                    break;
                            }
                        }
                    });
                }
                shareDialog.show();
                break;
            case R.id.tv_to_exam:
                Intent intent = new Intent(this, StartExamActivity.class);
                intent.putExtra("examType", ExamLib.EXAM_TYPE_1);
                startActivityForResult(intent, 2);
                break;
            case R.id.iv_pass:
                BaseAlertSimpleDialog dialog = new BaseAlertSimpleDialog(getContext(), "什么是保过卡？",
                        "在哈哈学车平台上注册登录，即可获得保过卡。\n学员在哈哈学车平台报名后，通过哈哈学车APP模拟科目一考试5次成绩均在90分以上，" +
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
    public void initShareData(String desc, String shareUrl) {
        mTitle = "科一不过包陪";
        mDescription = desc;
        mImageUrl = "https://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
        mUrl = shareUrl;
        HHLog.v("mUrl -> " + mUrl);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mSvMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data.getBooleanExtra("isShowShare", false)) {
                if (mShareDialog == null) {
                    mShareDialog = new ShareAppDialog(getContext(), mPresenter.getBonus());
                }
                mShareDialog.show();
            }
        } else if (requestCode == 2) {
            mPresenter.fetchScores();
        }
        Tencent.onActivityResultData(requestCode, resultCode, data, shareQQListener);
        Tencent.onActivityResultData(requestCode, resultCode, data, shareQZoneListener);
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

    /**
     * 获取分享API
     */
    private void regShareApi() {
        myApplication = HHBaseApplication.get(getContext());
        wxApi = myApplication.getIWXAPI();
        mTencent = myApplication.getTencentAPI();
        mWeiboShareAPI = myApplication.getWeiboAPI();
    }

    private void shareToQQ() {
        ImageRequest imageRequest = ImageRequest.fromUri(mPresenter.getQrCodeUrl());
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchImageFromBitmapCache(imageRequest, null);
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
                        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), fileName, null);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    // 最后通知图库更新
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/hahaxueche/qrcode.jpg"))));
                    shareQQListener = new ShareQQListener();
                    final Bundle params = new Bundle();
                    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                    params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "哈哈学车");
                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, Environment.getExternalStorageDirectory() + "/hahaxueche/qrcode.jpg");
                    mTencent.shareToQQ(ExamLibraryActivity.this, params, shareQQListener);
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {

            }
        }, CallerThreadExecutor.getInstance());
    }

    private void shareToQZone() {
        shareQZoneListener = new ShareQZoneListener();
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_APP);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "哈哈学车");
        params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, "哈哈学车");
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "");
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mPresenter.getQrCodeUrl());
        ArrayList<String> imgUrlList = new ArrayList<>();
        imgUrlList.add(mPresenter.getQrCodeUrl());
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrlList);
        mTencent.shareToQzone(ExamLibraryActivity.this, params, shareQZoneListener);
    }

    private void shareToWeibo() {
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        ImageObject imageObject = new ImageObject();
        Bitmap thumbBmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.share_qrcode), 400, 150, true);
        // 设置 Bitmap 类型的图片到视频对象里
        // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        imageObject.setImageObject(thumbBmp);
        TextObject textObject = new TextObject();
        textObject.text = mTitle;
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = "链接";
        mediaObject.description = mDescription;
        mediaObject.setThumbImage(thumbBmp);
        mediaObject.actionUrl = mUrl;

        weiboMessage.imageObject = imageObject;
        weiboMessage.textObject = textObject;
        weiboMessage.mediaObject = mediaObject;

        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求*/
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(this, request);
    }

    private void shareToWeixin() {
        ImageRequest imageRequest = ImageRequest.fromUri(mPresenter.getQrCodeUrl());
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchImageFromBitmapCache(imageRequest, null);
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
                    // 最后通知图库更新
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/hahaxueche/qrcode.jpg"))));

                    //微信官方api的分享图片方法,图片模式与转发的不一样,二维码无法识别,尼玛!!
                    Uri uriToImage = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/hahaxueche/qrcode.jpg"));
                    Intent shareIntent = new Intent();
                    //发送图片到朋友圈
                    //ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                    //发送图片给好友。
                    ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                    shareIntent.setComponent(comp);
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                    shareIntent.setType("image/jpeg");
                    startActivity(Intent.createChooser(shareIntent, "分享图片"));
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {

            }
        }, CallerThreadExecutor.getInstance());
    }

    private void shareToFriendCircle() {
        ImageRequest imageRequest = ImageRequest.fromUri(mPresenter.getQrCodeUrl());
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchImageFromBitmapCache(imageRequest, null);
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
                    // 最后通知图库更新
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/hahaxueche/qrcode.jpg"))));

                    Uri uriToImage = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/hahaxueche/qrcode.jpg"));
                    Intent shareIntent = new Intent();
                    //发送图片到朋友圈
                    ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                    //发送图片给好友。
                    //ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                    shareIntent.setComponent(comp);
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                    shareIntent.setType("image/jpeg");
                    startActivity(Intent.createChooser(shareIntent, "分享图片"));
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {

            }
        }, CallerThreadExecutor.getInstance());
    }

    private void shareToSms() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        //intent.setClassName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity");
        intent.putExtra("sms_body", "哈哈学车");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mPresenter.getQrCodeUrl()));
        intent.setType("image/png");
        startActivity(intent);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private class ShareQQListener implements IUiListener {

        @Override
        public void onCancel() {
            if (shareDialog != null) {
                shareDialog.dismiss();
            }
            showMessage("取消分享");
        }

        @Override
        public void onComplete(Object arg0) {
            if (shareDialog != null) {
                shareDialog.dismiss();
            }
            showMessage("分享成功");
        }

        @Override
        public void onError(UiError arg0) {
            if (shareDialog != null) {
                shareDialog.dismiss();
            }
            showMessage("分享失败");
            HHLog.e("分享失败，原因：" + arg0.errorMessage);
        }

    }

    private class ShareQZoneListener implements IUiListener {

        @Override
        public void onCancel() {
            if (shareDialog != null) {
                shareDialog.dismiss();
            }
            showMessage("取消分享");
        }

        @Override
        public void onComplete(Object arg0) {
            if (shareDialog != null) {
                shareDialog.dismiss();
            }
            showMessage("分享成功");
        }

        @Override
        public void onError(UiError arg0) {
            if (shareDialog != null) {
                shareDialog.dismiss();
            }
            showMessage("分享失败");
            HHLog.e("分享失败，原因：" + arg0.errorMessage);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    @Override
    public void onResponse(BaseResponse baseResp) {
        if (baseResp != null) {
            if (shareDialog != null) {
                shareDialog.dismiss();
            }
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    showMessage("分享成功");
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    showMessage("取消分享");
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    showMessage("分享失败");
                    HHLog.e("分享失败，原因：" + baseResp.errMsg);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_SHARE_QQ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                shareToQQ();
            } else {
                showMessage("请允许写入sdcard权限，不然从本地将图片分享到QQ");
            }
        } else if (requestCode == PERMISSIONS_REQUEST_SHARE_WX) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                shareToWeixin();
            } else {
                showMessage("请允许写入sdcard权限，不然从本地将图片分享到微信");
            }
        } else if (requestCode == PERMISSIONS_REQUEST_SHARE_CIRCLE_FRIEND) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                shareToFriendCircle();
            } else {
                showMessage("请允许写入sdcard权限，不然从本地将图片分享到朋友圈");
            }
        } else if (requestCode == PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                shareToSms();
            } else {
                showMessage("请允许发送短信权限，不然无法分享到短信");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
