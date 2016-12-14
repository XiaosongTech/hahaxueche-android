package com.hahaxueche.ui.dialog.myPage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hahaxueche.BuildConfig;
import com.hahaxueche.R;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.util.HHLog;

import java.util.Hashtable;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/12/13.
 */

public class ShareReferDialog extends Dialog {
    private Context mContext;
    private TextView tvShareQQ;
    private TextView tvShareWeibo;
    private TextView tvShareWeixin;
    private TextView tvShareFriendCircle;
    private TextView tvShareQzone;
    private TextView tvShareSms;
    private ImageView ivClose;
    private ImageView mIvQrCode;
    private static int QR_WIDTH = 300;
    private static int QR_HEIGHT = 300;
    private static final String WEB_URL_DALIBAO = BuildConfig.MOBILE_URL + "/share/xin-ren-da-li-bao?promo_code=553353";
    private String mQrCodeUrl;

    public interface OnShareListener {
        void onShare(int shareType);
    }

    private ShareDialog.OnShareListener mOnShareListener;

    public ShareReferDialog(Context context, String student_id, ShareDialog.OnShareListener onShareListener) {
        super(context);
        mContext = context;
        mOnShareListener = onShareListener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_share_refer, null);
        setContentView(view);
        initView(view);
        createQrCodeUrl(student_id);
        generateQrCode();
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }

    private void createQrCodeUrl(String studentId) {
        mQrCodeUrl = WEB_URL_DALIBAO;
        if (!TextUtils.isEmpty(studentId)) {
            mQrCodeUrl += "&referrer_id=" + studentId;
        }
        HHLog.v("mQrCodeUrl -> " + mQrCodeUrl);
    }

    private void generateQrCode() {
        try {
            //判断URL合法性
            if (mQrCodeUrl == null || "".equals(mQrCodeUrl) || mQrCodeUrl.length() < 1) {
                return;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(mQrCodeUrl, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            //显示到一个ImageView上面
            mIvQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    /**
     * 控件初始化
     *
     * @param view
     */
    private void initView(View view) {
        tvShareWeixin = ButterKnife.findById(view, R.id.tv_share_weixin);
        tvShareWeixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnShareListener.onShare(0);
                dismiss();
            }
        });
        tvShareFriendCircle = ButterKnife.findById(view, R.id.tv_share_friend_circle);
        tvShareFriendCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnShareListener.onShare(1);
                dismiss();
            }
        });
        tvShareQQ = ButterKnife.findById(view, R.id.tv_share_qq);
        tvShareQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnShareListener.onShare(2);
            }
        });
        tvShareWeibo = ButterKnife.findById(view, R.id.tv_share_weibo);
        tvShareWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnShareListener.onShare(3);
                dismiss();
            }
        });
        tvShareQzone = ButterKnife.findById(view, R.id.tv_share_qzone);
        tvShareQzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnShareListener.onShare(4);
                dismiss();
            }
        });
        tvShareSms = ButterKnife.findById(view, R.id.tv_share_sms);
        tvShareSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnShareListener.onShare(5);
                dismiss();
            }
        });
        ivClose = ButterKnife.findById(view, R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        mIvQrCode = ButterKnife.findById(view, R.id.iv_qrcode);
    }
}
