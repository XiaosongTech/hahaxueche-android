package com.hahaxueche.ui.activity.mySetting;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.MyApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.model.city.City;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.ui.dialog.ShareAppDialog;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by gibxin on 2016/4/26.
 */
public class ReferFriendsActivity extends MSBaseActivity implements IWeiboHandler.Response {
    private ImageButton mIbtnBack;
    private TextView mTvWithdrawMoney;//提现金额
    private TextView mTvWithdraw;//提现
    private TextView mTvSaveQrCode;
    private ImageView mIvDash;
    private ImageView mIvQrCode;
    private SharedPreferencesUtil spUtil;

    private ProgressDialog pd;//进度框
    private TextView mTvReferRules;
    private City myCity;
    private User mUser;
    private ImageView mIvRefer;
    private static final int PERMISSIONS_REQUEST = 600;
    private static final int PERMISSIONS_REQUEST_SHARE_QQ = 601;
    private static final int PERMISSIONS_REQUEST_SHARE_WX = 602;
    private static final int PERMISSIONS_REQUEST_SHARE_CIRCLE_FRIEND = 603;
    private String mQrCodeUrl;

    private static final int REQUEST_CODE_WITHDRAW = 0;

    /*****************
     * 分享
     ******************/
    private IWXAPI wxApi; //微信api
    private Tencent mTencent;//QQ
    private IWeiboShareAPI mWeiboShareAPI;//新浪微博
    private MyApplication myApplication;
    private String mTitle = "哈哈学车";
    private String mDescription = "";
    private ShareAppDialog shareAppDialog;
    private ImageView mIvShare;
    private static final int THUMB_SIZE = 150;

    /*****************
     * end
     ******************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_friends);
        initView();
        initEvent();
        loadDatas();
        loadShare();
    }

    private void initView() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mIvDash = Util.instence(this).$(this, R.id.iv_dash);
        mTvReferRules = Util.instence(this).$(this, R.id.tv_refer_rules);
        mTvWithdrawMoney = Util.instence(this).$(this, R.id.tv_withdraw_money);
        mTvWithdraw = Util.instence(this).$(this, R.id.tv_withdraw);
        mIvDash.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        spUtil = new SharedPreferencesUtil(this);
        mIvRefer = Util.instence(this).$(this, R.id.iv_refer);
        mIvQrCode = Util.instence(this).$(this, R.id.iv_qr_code);
        mTvSaveQrCode = Util.instence(this).$(this, R.id.tv_save_qr_code);
        mIvShare = Util.instence(this).$(this, R.id.iv_share);
    }

    private void initEvent() {
        mTvWithdraw.setOnClickListener(mClickListener);
        mIbtnBack.setOnClickListener(mClickListener);
        mTvSaveQrCode.setOnClickListener(mClickListener);
        mTvWithdrawMoney.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        String eventDetailTips = getResources().getString(R.string.eventDetailsTips);
        myCity = spUtil.getMyCity();
        mUser = spUtil.getUser();
        if (myCity != null && !TextUtils.isEmpty(myCity.getReferral_banner())) {
            mTvReferRules.setText(String.format(eventDetailTips, Util.getMoney(String.valueOf(myCity.getReferer_bonus()))));
            int width = Util.instence(context).getDm().widthPixels;
            int height = Math.round(((float) 8 / 9) * width);
            Picasso.with(context).load(myCity.getReferral_banner()).resize(width, height).centerCrop().into(mIvRefer);
        }
        if (mUser != null && mUser.getStudent() != null) {
            mTvWithdrawMoney.setText(Util.getMoney(mUser.getStudent().getBonus_balance()));
            mQrCodeUrl = HttpEngine.BASE_SERVER_IP + "/share/students/" + mUser.getStudent().getId() + "/image";
            Log.v("gibxin", "mQrCodeUrl -> " + mQrCodeUrl);
            Picasso.with(context).load(mQrCodeUrl).into(mIvQrCode);
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ibtn_back:
                    ReferFriendsActivity.this.finish();
                    break;
                case R.id.tv_withdraw:
                    Intent intent = new Intent(ReferFriendsActivity.this, WithdrawActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_WITHDRAW);
                    break;
                case R.id.tv_save_qr_code:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
                        //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                    } else {
                        // Android version is lesser than 6.0 or the permission is already granted.
                        saveImg();
                    }
                    break;
                case R.id.tv_withdraw_money:
                    //推荐记录
                    intent = new Intent(ReferFriendsActivity.this, MakeMoneyInfoActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                saveImg();
            } else {
                Toast.makeText(this, "请允许写入sdcard权限，不然无法将图片保存到本地", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_SHARE_QQ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                shareToQQ();
            } else {
                Toast.makeText(this, "请允许写入sdcard权限，不然从本地将图片分享到QQ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_SHARE_WX) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                shareToWeixin();
            } else {
                Toast.makeText(this, "请允许写入sdcard权限，不然从本地将图片分享到微信", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_SHARE_CIRCLE_FRIEND) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                shareToFriendCircle();
            } else {
                Toast.makeText(this, "请允许写入sdcard权限，不然从本地将图片分享到朋友圈", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void saveImg() {
        Picasso.with(context).load(mQrCodeUrl).into(localImgTarget);
    }

    Target localImgTarget = new Target() {

        @Override
        public void onPrepareLoad(Drawable arg0) {
            pd = ProgressDialog.show(ReferFriendsActivity.this, null, "图片保存传中，请稍后……");
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
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
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/hahaxueche/qrcode.jpg"))));
            if (pd != null) {
                pd.dismiss();
            }
            Toast.makeText(context, "图片保存成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBitmapFailed(Drawable arg0) {
            if (pd != null) {
                pd.dismiss();
            }
            Toast.makeText(context, "图片保存失败", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * qq分享必须是本地图片
     */
    Target QQTarget = new Target() {

        @Override
        public void onPrepareLoad(Drawable arg0) {
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
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
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/hahaxueche/qrcode.jpg"))));
            ShareListener myListener = new ShareListener();
            final Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "哈哈学车");
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, "/sdcard/hahaxueche/qrcode.jpg");
            //params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
            mTencent.shareToQQ(ReferFriendsActivity.this, params, myListener);
        }

        @Override
        public void onBitmapFailed(Drawable arg0) {
        }
    };

    Target wxTarget = new Target() {

        @Override
        public void onPrepareLoad(Drawable arg0) {
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {

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
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/hahaxueche/qrcode.jpg"))));

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

//            WXImageObject imgObj = new WXImageObject(bitmap);
//
//            WXMediaMessage msg = new WXMediaMessage();
//            msg.mediaObject = imgObj;
//
//            Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
//            //bitmap.recycle();
//            msg.thumbData = Util.bmpToByteArray(thumbBmp, true);  // ÉèÖÃËõÂÔÍ¼
//
//            SendMessageToWX.Req req = new SendMessageToWX.Req();
//            req.transaction = buildTransaction("img");
//            req.message = msg;
//            req.scene = SendMessageToWX.Req.WXSceneSession;
//            wxApi.sendReq(req);
        }

        @Override
        public void onBitmapFailed(Drawable arg0) {
        }
    };

    Target circleFriendsTarget = new Target() {

        @Override
        public void onPrepareLoad(Drawable arg0) {
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
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
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/hahaxueche/qrcode.jpg"))));

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
//            WXImageObject imgObj = new WXImageObject(bitmap);
//
//            WXMediaMessage msg = new WXMediaMessage();
//            msg.mediaObject = imgObj;
//
//            Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
//            //bitmap.recycle();
//            msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
//
//            SendMessageToWX.Req req = new SendMessageToWX.Req();
//            req.transaction = buildTransaction("img");
//            req.message = msg;
//            req.scene = SendMessageToWX.Req.WXSceneTimeline;
//            wxApi.sendReq(req);
        }

        @Override
        public void onBitmapFailed(Drawable arg0) {
        }
    };

    Target weiboTarget = new Target() {

        @Override
        public void onPrepareLoad(Drawable arg0) {
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {

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

        @Override
        public void onBitmapFailed(Drawable arg0) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_WITHDRAW) {
            if (resultCode == RESULT_OK && data != null && data.getBooleanExtra("isUpdate", false)) {
                this.msPresenter.getStudent(spUtil.getUser().getStudent().getId(), spUtil.getUser().getSession().getAccess_token(),
                        new MSCallbackListener<Student>() {
                            @Override
                            public void onSuccess(Student student) {
                                User user = spUtil.getUser();
                                user.setStudent(student);
                                spUtil.setUser(user);
                                loadDatas();
                            }

                            @Override
                            public void onFailure(String errorEvent, String message) {
                            }
                        });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadShare() {
        regShareApi();
        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShareAppDialog();
            }
        });
    }

    private void share(int shareType) {
        switch (shareType) {
            case 0:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_SHARE_WX);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    // Android version is lesser than 6.0 or the permission is already granted.
                    shareToWeixin();
                }
                break;
            case 1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_SHARE_CIRCLE_FRIEND);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    // Android version is lesser than 6.0 or the permission is already granted.
                    shareToFriendCircle();
                }
                break;
            case 2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_SHARE_QQ);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    // Android version is lesser than 6.0 or the permission is already granted.
                    shareToQQ();
                }
                break;
            case 3:
                shareToWeibo();
                break;
            default:
                break;
        }
    }

    private void shareToQQ() {
        Picasso.with(context).load(mQrCodeUrl).into(QQTarget);
    }

    private void shareToWeibo() {
        Picasso.with(context).load(mQrCodeUrl).into(weiboTarget);
    }

    private void shareToWeixin() {
        Picasso.with(context).load(mQrCodeUrl).into(wxTarget);
    }

    private void shareToFriendCircle() {
        Picasso.with(context).load(mQrCodeUrl).into(circleFriendsTarget);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private class ShareListener implements IUiListener {

        @Override
        public void onCancel() {
            Log.v("gibxin", "onCancel");
        }

        @Override
        public void onComplete(Object arg0) {
            Log.v("gibxin", "onComplete");
        }

        @Override
        public void onError(UiError arg0) {
            Log.v("gibxin", "onError");
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
            Log.v("gibxin", "baseResp.errCode" + baseResp.errCode);
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    Toast.makeText(ReferFriendsActivity.this, "分享成功", Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    Toast.makeText(ReferFriendsActivity.this, "取消分享", Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    Toast.makeText(ReferFriendsActivity.this, "分享失败，原因：" + baseResp.errMsg, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    /**
     * 获取分享API
     */
    private void regShareApi() {
        myApplication = (MyApplication) this.getApplication();
        wxApi = myApplication.getIWXAPI();
        mTencent = myApplication.getTencentAPI();
        mWeiboShareAPI = myApplication.getWeiboAPI();
    }

    private void showShareAppDialog() {
        if (shareAppDialog == null) {
            shareAppDialog = new ShareAppDialog(ReferFriendsActivity.this, new ShareAppDialog.OnShareListener() {
                @Override
                public void onShare(int shareType) {
                    share(shareType);
                }
            });
        }
        shareAppDialog.show();
    }
}
