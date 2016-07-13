package com.hahaxueche.ui.activity.mySetting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.share.ShareConstants;
import com.hahaxueche.ui.activity.signupLogin.AgreementActivity;
import com.hahaxueche.utils.Util;
import com.tencent.tauth.Tencent;

/**
 * Created by wangshirui on 16/7/13.
 */
public class MyCouponActivity extends MSBaseActivity {
    private TextView mTvHowGetCoupon;//如何获取礼金券文字说明
    private TextView mTvFenqileUsage;//分期乐使用说明
    private TextView mTvContactTel;
    private TextView mTvContactQQ;
    private Tencent mTencent;//QQ
    private static final int PERMISSIONS_REQUEST_CELL_PHONE = 601;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupon);
        mTencent = Tencent.createInstance(ShareConstants.APP_ID_QQ, MyCouponActivity.this);
        initViews();
        loadTextViews();
    }

    private void initViews() {
        mTvHowGetCoupon = Util.instence(this).$(this, R.id.tv_how_get_coupon);
        mTvFenqileUsage = Util.instence(this).$(this, R.id.tv_fenqile_usage);
        mTvContactTel = Util.instence(this).$(this, R.id.tv_contact_tel);
        mTvContactQQ = Util.instence(this).$(this, R.id.tv_contact_qq);
    }

    /**
     * 加载部分文字说明
     */
    private void loadTextViews() {
        //礼金券文字说明
        //部分变色 可点击
        CharSequence howToGetCouponStr = getText(R.string.howToGetCoupon);
        SpannableString spHowGetCoupon = new SpannableString(howToGetCouponStr);
        spHowGetCoupon.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(context, AgreementActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(context, R.color.app_theme_color));
                ds.setUnderlineText(false);
                ds.clearShadowLayer();
            }
        }, howToGetCouponStr.length() - 12, howToGetCouponStr.length() - 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spHowGetCoupon.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.haha_blue)), howToGetCouponStr.length() - 12, howToGetCouponStr.length() - 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvHowGetCoupon.setText(spHowGetCoupon);
        mTvHowGetCoupon.setHighlightColor(ContextCompat.getColor(context, R.color.haha_blue));
        mTvHowGetCoupon.setMovementMethod(LinkMovementMethod.getInstance());
        //分期乐文字说明
        CharSequence fenqileUsageStr = getText(R.string.fenqileUsage);
        SpannableString spFenqileUsageStr = new SpannableString(fenqileUsageStr);
        spFenqileUsageStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(context, AgreementActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(context, R.color.app_theme_color));
                ds.setUnderlineText(false);
                ds.clearShadowLayer();
            }
        }, fenqileUsageStr.length() - 12, fenqileUsageStr.length() - 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spFenqileUsageStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.haha_blue)), fenqileUsageStr.length() - 12, fenqileUsageStr.length() - 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvFenqileUsage.setText(spFenqileUsageStr);
        mTvFenqileUsage.setHighlightColor(ContextCompat.getColor(context, R.color.haha_blue));
        mTvFenqileUsage.setMovementMethod(LinkMovementMethod.getInstance());
        //联系客服电话
        mTvContactTel.setText(Html.fromHtml(getResources().getString(R.string.contactTel)));
        mTvContactTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CELL_PHONE);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    // Android version is lesser than 6.0 or the permission is already granted.
                    contactTel();
                }
            }
        });
        mTvContactQQ.setText(Html.fromHtml(getResources().getString(R.string.contactQQ)));
        mTvContactQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactQQ();
            }
        });

    }

    /**
     * 联系客服电话
     */
    private void contactTel() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4000016006"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    private void contactQQ() {
        int ret = mTencent.startWPAConversation(MyCouponActivity.this, ShareConstants.CUSTOMER_SERVICE_QQ, "");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CELL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactTel();
            } else {
                Toast.makeText(this, "请允许拨打电话权限，不然无法直接拨号联系客服", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
