package com.hahaxueche.ui.activity.mySetting;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.model.city.City;
import com.hahaxueche.model.user.User;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by gibxin on 2016/4/26.
 */
public class ReferFriendsActivity extends MSBaseActivity {
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
    private String mQrCodeUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_friends);
        initView();
        initEvent();
        loadDatas();
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
    }

    private void initEvent() {
        mTvWithdraw.setOnClickListener(mClickListener);
        mIbtnBack.setOnClickListener(mClickListener);
        mTvSaveQrCode.setOnClickListener(mClickListener);
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
                    startActivity(intent);
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
}
