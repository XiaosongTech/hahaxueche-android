package com.hahaxueche.ui.activity.myPage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
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
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.presenter.myPage.ReferFriendsPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.view.myPage.ReferFriendsView;
import com.hahaxueche.util.HHLog;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by wangshirui on 16/9/21.
 */

public class ReferFriendsActivity extends HHBaseActivity implements ReferFriendsView, IWeiboHandler.Response {
    @BindView(R.id.tv_refer_rules)
    TextView mTvReferRules;
    @BindView(R.id.tv_withdraw_money)
    TextView mTvWithdrawMoney;
    @BindView(R.id.iv_refer)
    SimpleDraweeView mIvRefer;
    @BindView(R.id.iv_qr_code)
    SimpleDraweeView mIvQrCode;
    @BindView(R.id.iv_dash)
    ImageView mIvDash;
    ImageView mIvBack;
    TextView mTvTitle;
    @BindView(R.id.sv_main)
    ScrollView mSvMain;
    private ReferFriendsPresenter mPresenter;
    /*****************
     * 分享
     ******************/
    private ShareDialog shareDialog;
    private IWXAPI wxApi; //微信api
    private Tencent mTencent;//QQ
    private IWeiboShareAPI mWeiboShareAPI;//新浪微博
    private HHBaseApplication myApplication;
    private ShareQQListener shareQQListener;
    private ShareQZoneListener shareQZoneListener;
    /*****************
     * end
     ******************/
    private static final int THUMB_SIZE = 150;
    private static final int PERMISSIONS_REQUEST_SAVE_LOCAL = 60;
    private static final int PERMISSIONS_REQUEST_SHARE_QQ = 601;
    private static final int PERMISSIONS_REQUEST_SHARE_WX = 602;
    private static final int PERMISSIONS_REQUEST_SHARE_CIRCLE_FRIEND = 603;
    private static final int REQUEST_CODE_WITHDRAW = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ReferFriendsPresenter();
        setContentView(R.layout.activity_refer_friends);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        mIvDash.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        initActionBar();
        regShareApi();
    }

    @Override
    public void setReferRules(String rules) {
        mTvReferRules.setText(rules);
    }

    @Override
    public void setMyCityReferImage(String url) {
        mIvRefer.setImageURI(url);
    }

    @Override
    public void setQrCodeImage(String url) {
        mIvQrCode.setImageURI(url);
    }

    @Override
    public void setWithdrawMoney(String money) {
        mTvWithdrawMoney.setText(money);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("我为哈哈代言");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReferFriendsActivity.this.finish();
            }
        });
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

    @OnLongClick(R.id.iv_qr_code)
    public boolean longClickImage() {
        showSaveImageDialog();
        return true;
    }

    @OnClick({R.id.tv_share_qr_code,
            R.id.tv_withdraw,
            R.id.tv_withdraw_money})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_share_qr_code:
                mPresenter.clickShareCount();
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
                                    break;
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
                                default:
                                    break;
                            }
                        }
                    });
                }
                shareDialog.show();
                break;
            case R.id.tv_withdraw:
                mPresenter.clickWithdraw();
                break;
            case R.id.tv_withdraw_money:
                mPresenter.clickReferrerList();
                startActivity(new Intent(getContext(), ReferrerListActivity.class));
                break;
            default:
                break;
        }
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
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/hahaxueche/qrcode.jpg"))));

                    //微信官方api的分享图片方法,图片模式与转发的不一样,二维码无法识别,尼玛!!
                    Uri uriToImage = Uri.fromFile(new File("/sdcard/hahaxueche/qrcode.jpg"));
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
                    mPresenter.clickShareSuccessCount("wechat_friend");
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
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/hahaxueche/qrcode.jpg"))));

                    Uri uriToImage = Uri.fromFile(new File("/sdcard/hahaxueche/qrcode.jpg"));
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
                    mPresenter.clickShareSuccessCount("wechat_friend_zone");
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {

            }
        }, CallerThreadExecutor.getInstance());
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
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/hahaxueche/qrcode.jpg"))));
                    shareQQListener = new ShareQQListener();
                    final Bundle params = new Bundle();
                    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                    params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "哈哈学车");
                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, "/sdcard/hahaxueche/qrcode.jpg");
                    mTencent.shareToQQ(ReferFriendsActivity.this, params, shareQQListener);
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
        mTencent.shareToQzone(ReferFriendsActivity.this, params, shareQZoneListener);
    }

    private void shareToWeibo() {
        ImageRequest imageRequest = ImageRequest.fromUri(mPresenter.getQrCodeUrl());
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchImageFromBitmapCache(imageRequest, null);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                if (bitmap != null) {
                    // 1. 初始化微博的分享消息
                    WeiboMessage weiboMessage = new WeiboMessage();

                    ImageObject imageObject = new ImageObject();
                    //设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
                    Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
                    imageObject.setImageObject(thumbBmp);
                    weiboMessage.mediaObject = imageObject;
                    // 2. 初始化从第三方到微博的消息请求
                    SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
                    // 用transaction唯一标识一个请求
                    request.transaction = String.valueOf(System.currentTimeMillis());
                    request.message = weiboMessage;
                    // 3. 发送请求消息到微博，唤起微博分享界面
                    mWeiboShareAPI.sendRequest(ReferFriendsActivity.this, request);
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {

            }
        }, CallerThreadExecutor.getInstance());
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
            mPresenter.clickShareSuccessCount("QQ_friend");
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
            mPresenter.clickShareSuccessCount("qzone");
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
        if (shareDialog != null) {
            shareDialog.dismiss();
        }
        if (baseResp != null) {
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    mPresenter.clickShareSuccessCount("weibo");
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
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mSvMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToWithdraw() {
        Intent intent = new Intent(getContext(), WithdrawActivity.class);
        startActivityForResult(intent, REQUEST_CODE_WITHDRAW);
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
        } else if (requestCode == PERMISSIONS_REQUEST_SAVE_LOCAL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                saveImg();
            } else {
                showMessage("请允许写入sdcard权限，无法将图片保存到本地");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 提示是否保存到本地
     */
    private void showSaveImageDialog() {
        if (TextUtils.isEmpty(mPresenter.getQrCodeUrl())) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(ReferFriendsActivity.this);
        builder.setTitle("提示");
        builder.setMessage("是否保存到本地?");
        builder.setPositiveButton("保存本地", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_SAVE_LOCAL);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    // Android version is lesser than 6.0 or the permission is already granted.
                    saveImg();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void saveImg() {
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
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/hahaxueche/qrcode.jpg"))));
                    showMessage("图片保存成功");
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {

            }
        }, CallerThreadExecutor.getInstance());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_WITHDRAW) {
            if (resultCode == RESULT_OK && data != null && data.getBooleanExtra("isUpdate", false)) {
                mPresenter.refreshBonus();
            }
        }
        Tencent.onActivityResultData(requestCode, resultCode, data, shareQQListener);
        Tencent.onActivityResultData(requestCode, resultCode, data, shareQZoneListener);
        super.onActivityResult(requestCode, resultCode, data);
    }
}