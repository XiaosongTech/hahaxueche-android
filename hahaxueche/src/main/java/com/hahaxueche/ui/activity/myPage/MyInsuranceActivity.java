package com.hahaxueche.ui.activity.myPage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.presenter.myPage.MyInsurancePresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.myPage.MyInsuranceView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.RequestCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2017/2/25.
 */

public class MyInsuranceActivity extends HHBaseActivity implements MyInsuranceView {
    private MyInsurancePresenter mPresenter;
    private TextView mTvRight;
    @BindView(R.id.sv_main)
    ScrollView mSvMain;
    @BindView(R.id.lly_no_purchase)
    LinearLayout mLlyNoPurchase;
    @BindView(R.id.lly_no_upload_info)
    LinearLayout mLlyNoUploadInfo;
    @BindView(R.id.lly_success)
    LinearLayout mLlySuccess;
    @BindView(R.id.tv_with_new_coach_name)
    TextView mTvWithNewCoachName;
    @BindView(R.id.tv_with_new_coach_price)
    TextView mTvWithNewCoachPrice;
    @BindView(R.id.tv_with_paid_coach_name)
    TextView mTvWithPaidCoachName;
    @BindView(R.id.tv_with_paid_coach_price)
    TextView mTvWithPaidCoachPrice;
    @BindView(R.id.tv_without_coach_name)
    TextView mTvWithoutCoachName;
    @BindView(R.id.tv_without_coach_price)
    TextView mTvWithoutCoachPrice;
    @BindView(R.id.lly_with_new_coach)
    LinearLayout mLlyWithNewCoach;
    @BindView(R.id.lly_with_paid_coach)
    LinearLayout mLlyWithPaidCoach;
    @BindView(R.id.lly_without_coach)
    LinearLayout mLlyWithoutCoach;
    @BindView(R.id.tv_customer_service)
    TextView mTvCustomerService;
    @BindView(R.id.tv_insurance_abstract)
    TextView mTvInsuranceAbstract;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MyInsurancePresenter();
        setContentView(R.layout.activity_my_insurance);
        ButterKnife.bind(this);
        initActionBar();
        mPresenter.attachView(this);
        changeCustomerService();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_my_insurance);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("我的赔付宝");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyInsuranceActivity.this.finish();
            }
        });
        mTvRight = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_right);
        mTvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.clickRightButton();
            }
        });
    }

    @OnClick({R.id.lly_with_new_coach,
            R.id.lly_with_paid_coach,
            R.id.lly_without_coach,
            R.id.tv_insurance_qrcode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lly_with_new_coach:
                mPresenter.purchaseWithNewCoach();
                break;
            case R.id.lly_with_paid_coach:
                mPresenter.purchaseWithPaidCoach();
                break;
            case R.id.lly_without_coach:
                mPresenter.purchaseWithoutCoach();
                break;
            case R.id.tv_insurance_qrcode:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCode.PERMISSIONS_REQUEST_SDCARD);
                } else {
                    saveQrcodeImage();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 把哈哈学车公众号二维码保存到本地
     */
    private void saveQrcodeImage() {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.hhxc_qrcode);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "hahaxueche");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "hhxc_qrcode.png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            bitmap.recycle();
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
                "/hahaxueche/hhxc_qrcode.png");
        Uri uriToImage;
        if (Build.VERSION.SDK_INT >= 24) {
            uriToImage = FileProvider.getUriForFile(getContext(),
                    "com.hahaxueche.provider.fileProvider", imageFile);
        } else {
            uriToImage = Uri.fromFile(imageFile);
        }
        // 最后通知图库更新
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uriToImage));
        showMessage("已保存至系统相册！");
    }


    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void setViewNoPurchase() {
        mLlyNoPurchase.setVisibility(View.VISIBLE);
        mLlyNoUploadInfo.setVisibility(View.GONE);
        mLlySuccess.setVisibility(View.GONE);
        mTvRight.setText("了解详情");
    }

    @Override
    public void setViewNoUploadInfo() {
        mLlyNoPurchase.setVisibility(View.GONE);
        mLlyNoUploadInfo.setVisibility(View.VISIBLE);
        mLlySuccess.setVisibility(View.GONE);
        mTvRight.setText("上传投保信息");
    }

    @Override
    public void setViewSuccess() {
        mLlyNoPurchase.setVisibility(View.GONE);
        mLlyNoUploadInfo.setVisibility(View.GONE);
        mLlySuccess.setVisibility(View.VISIBLE);
        mTvRight.setText("保险信息");
    }

    @Override
    public void setWithNewCoachPayEnable(boolean enable) {
        if (enable) {
            mTvWithNewCoachName.setBackgroundResource(R.drawable.button_insurance_top_half);
            mTvWithNewCoachPrice.setBackgroundResource(R.drawable.button_insurance_bottom_half);
        } else {
            mTvWithNewCoachName.setBackgroundResource(R.drawable.button_insurance_cant_top);
            mTvWithNewCoachPrice.setBackgroundResource(R.drawable.button_insurance_cant_bottom);
        }
        mLlyWithNewCoach.setClickable(enable);
    }

    @Override
    public void setWithPaidCoachPayEnable(boolean enable) {
        if (enable) {
            mTvWithPaidCoachName.setBackgroundResource(R.drawable.button_insurance_top_half);
            mTvWithPaidCoachPrice.setBackgroundResource(R.drawable.button_insurance_bottom_half);
        } else {
            mTvWithPaidCoachName.setBackgroundResource(R.drawable.button_insurance_cant_top);
            mTvWithPaidCoachPrice.setBackgroundResource(R.drawable.button_insurance_cant_bottom);
        }
        mLlyWithPaidCoach.setClickable(enable);
    }

    @Override
    public void setWithoutCoachPayEnable(boolean enable) {
        if (enable) {
            mTvWithoutCoachName.setBackgroundResource(R.drawable.button_insurance_top_half);
            mTvWithoutCoachPrice.setBackgroundResource(R.drawable.button_insurance_bottom_half);
        } else {
            mTvWithoutCoachName.setBackgroundResource(R.drawable.button_insurance_cant_top);
            mTvWithoutCoachPrice.setBackgroundResource(R.drawable.button_insurance_cant_bottom);
        }
        mLlyWithoutCoach.setClickable(enable);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mSvMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void finishToPurchaseInsuranceWithPaidCoach() {
        Intent intent = new Intent();
        intent.putExtra("insuranceType", Common.PURCHASE_INSURANCE_TYPE_WITH_PAID_COACH);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void finishToPurchaseInsuranceWithoutCoach() {
        Intent intent = new Intent();
        intent.putExtra("insuranceType", Common.PURCHASE_INSURANCE_TYPE_WITHOUT_COACH);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void finishToUploadInfo() {
        Intent intent = new Intent();
        intent.putExtra("toUploadInfo", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void finishToFindCoach() {
        Intent intent = new Intent();
        intent.putExtra("toFindCoach", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void setAbstract(String text) {
        mTvInsuranceAbstract.setText(text);
    }

    @Override
    public void navigateToInsuranceInfo() {
        startActivity(new Intent(getContext(), InsuranceInfoActivity.class));
    }

    @Override
    public void setWithNewCoachPrice(String s) {
        mTvWithNewCoachPrice.setText(s);
    }

    @Override
    public void setWithPaidCoachPrice(String s) {
        mTvWithPaidCoachPrice.setText(s);
    }

    @Override
    public void setWithoutCoachPrice(String s) {
        mTvWithoutCoachPrice.setText(s);
    }

    public void changeCustomerService() {
        String customerService = mTvCustomerService.getText().toString();
        SpannableString spCustomerServiceStr = new SpannableString(customerService);
        spCustomerServiceStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RequestCode.PERMISSIONS_REQUEST_CELL_PHONE);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    // Android version is lesser than 6.0 or the permission is already granted.
                    contactService();
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getContext(), R.color.app_theme_color));
                ds.setUnderlineText(true);
                ds.clearShadowLayer();
            }
        }, customerService.indexOf("400"), customerService.indexOf("6006") + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spCustomerServiceStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.app_theme_color)),
                customerService.indexOf("400"), customerService.indexOf("6006") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spCustomerServiceStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                mPresenter.onlineAsk();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getContext(), R.color.app_theme_color));
                ds.setUnderlineText(true);
                ds.clearShadowLayer();
            }
        }, customerService.indexOf("在线客服"), customerService.indexOf("在线客服") + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spCustomerServiceStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.app_theme_color)),
                customerService.indexOf("在线客服"), customerService.indexOf("在线客服") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvCustomerService.setText(spCustomerServiceStr);
        mTvCustomerService.setHighlightColor(ContextCompat.getColor(getContext(), R.color.app_theme_color));
        mTvCustomerService.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 联系客服
     */
    private void contactService() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4000016006"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_CELL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                contactService();
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系客服");
            }
        } else if (requestCode == RequestCode.PERMISSIONS_REQUEST_SDCARD) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveQrcodeImage();
            } else {
                showMessage("请允许操作sdcard权限，不然无法将二维码图片保存至系统相册");
            }
        }
    }
}
