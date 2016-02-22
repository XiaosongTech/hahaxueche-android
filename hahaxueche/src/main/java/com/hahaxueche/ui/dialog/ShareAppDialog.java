package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.share.ShareConstants;
import com.hahaxueche.ui.activity.findCoach.CoachDetailActivity;
import com.hahaxueche.utils.Util;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * 分享dialog页面
 * Created by gibxin on 2016/2/21.
 */
public class ShareAppDialog extends Dialog implements IWXAPIEventHandler {
    private IWXAPI wxApi; //微信api

    private Context mContext;
    private TextView tvShareQQ;
    private TextView tvShareQzone;
    private TextView tvShareWeixin;
    private TextView tvShareFriendCircle;
    private Tencent mTencent;//QQ

    public ShareAppDialog(Context context) {
        super(context);
        mContext = context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_share_app, null);
        setContentView(view);
        initView(view);
        regToWx();
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        //lp.x = 100; // 新位置X坐标
        //lp.y = 100; // 新位置Y坐标
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
        mTencent = Tencent.createInstance(ShareConstants.APP_ID_QQ, mContext);
    }

    /**
     * 控件初始化
     *
     * @param view
     */
    private void initView(View view) {
        tvShareWeixin = (TextView) view.findViewById(R.id.tv_share_weixin);
        tvShareWeixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = "http://a.app.qq.com/o/simple.jsp?pkgname=com.hahaxueche";
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = "哈哈学车";
                msg.description = "开启快乐学车之旅吧～";
                Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
                msg.thumbData = Util.bmpToByteArray(thumb, true);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                //SendMessageToWX.Req.WXSceneTimeline
                req.scene = SendMessageToWX.Req.WXSceneSession;
                wxApi.sendReq(req);
            }
        });
        tvShareFriendCircle = (TextView) view.findViewById(R.id.tv_share_friend_circle);
        tvShareFriendCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = "http://a.app.qq.com/o/simple.jsp?pkgname=com.hahaxueche";
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = "哈哈学车";
                msg.description = "开启快乐学车之旅吧～";
                Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
                msg.thumbData = Util.bmpToByteArray(thumb, true);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                wxApi.sendReq(req);
            }
        });
        tvShareQQ = (TextView) view.findViewById(R.id.tv_share_qq);
        tvShareQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareListener myListener = new ShareListener();

                final Bundle params = new Bundle();
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, "哈哈学车");
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "哈哈学车");
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "开启快乐学车之旅吧～");
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://a.app.qq.com/o/simple.jsp?pkgname=com.hahaxueche");
                mTencent.shareToQQ((CoachDetailActivity) mContext, params, myListener);
            }
        });
        tvShareQzone = (TextView) view.findViewById(R.id.tv_share_qzone);
        tvShareQzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareListener myListener = new ShareListener();

                final Bundle params = new Bundle();
                //分享类型
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
                params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "哈哈学车");//必填
                params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "开启快乐学车之旅吧～");//选填
                params.putString(QzoneShare.SHARE_TO_QQ_AUDIO_URL, "http://a.app.qq.com/o/simple.jsp?pkgname=com.hahaxueche");//必填
                //params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, "图片链接ArrayList");
                mTencent.shareToQzone((CoachDetailActivity) mContext, params, myListener);

            }
        });
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private void regToWx() {
        wxApi = WXAPIFactory.createWXAPI(mContext, ShareConstants.APP_ID, true);
        wxApi.registerApp(ShareConstants.APP_ID);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.v("WXEntryActivity", "onReq");
        switch (baseReq.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.v("WXEntryActivity", "onResp");
        int result = 0;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.weixin_errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.weixin_errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.weixin_errcode_deny;
                break;
            default:
                result = R.string.weixin_errcode_unknown;
                break;
        }
        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        cancel();
    }

    private class ShareListener implements IUiListener {

        @Override
        public void onCancel() {
        }

        @Override
        public void onComplete(Object arg0) {
        }

        @Override
        public void onError(UiError arg0) {
        }

    }
}
