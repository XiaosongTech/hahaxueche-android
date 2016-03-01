package com.hahaxueche.ui.activity.signupLogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.signupLogin.CompStuResponse;
import com.hahaxueche.presenter.signupLogin.SLCallbackListener;
import com.hahaxueche.ui.dialog.CityChoseDialog;
import com.hahaxueche.ui.dialog.RegisterInfoPhotoDialog;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.ui.util.PhotoUtil;
import com.hahaxueche.ui.util.PictrueGet;
import com.hahaxueche.utils.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 完善学生资料Activity
 * Created by gibxin on 2016/1/23.
 */
public class SignUpInfoActivity extends SLBaseActivity {
    private LinearLayout mainLayout;
    private ImageView cameraBtn;
    private LinearLayout addressBtn;
    private EditText nameEdit;
    private TextView addressText;
    private Button downBtn;

    private FrameLayout updateLayout;
    private LinearLayout updateCancel;
    private Button updateDown;

    //头像工具类
    private PhotoUtil mPhotoUtil;
    private String mAlbumPicturePath = null;
    private Bitmap curPhoto = null;
    private String mPhotoPath;

    private ProgressDialog pd;//进度框

    private String mCurCityId = "", mCurCityName = "", studentId = "", accessToken = "";

    private CityChoseDialog mCityChoseDialog;
    private String TAG = "SignUpInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_info);
        mPhotoUtil = new PhotoUtil(this);
        initView();
        initEvent();
        SharedPreferences sharedPreferences = getSharedPreferences("session", Activity.MODE_PRIVATE);
        accessToken = sharedPreferences.getString("access_token", "");//sharedPreferences.getString("access_token", "");//"95e7ccf91e7e266c79acc0494b48e184";
        studentId = sharedPreferences.getString("id", "");//sharedPreferences.getString("id", "");//"06812c2b-9dea-4bdc-bbde-b9516627b206";
        mCityChoseDialog = new CityChoseDialog(this,
                new CityChoseDialog.OnBtnClickListener() {
                    @Override
                    public void onCitySelected(String cityName, String cityId) {
                        mCityChoseDialog.dismiss();
                        mCurCityId = cityId;
                        mCurCityName = cityName;
                        addressText.setText(mCurCityName);
                    }
                });
    }

    /**
     * 控件初始化
     */
    private void initView() {
        cameraBtn = Util.instence(this).$(this, R.id.id_camera_btn);
        addressBtn = Util.instence(this).$(this, R.id.id_address_select_btn);
        nameEdit = Util.instence(this).$(this, R.id.id_user_name_edit);
        addressText = Util.instence(this).$(this, R.id.id_user_address_edit);
        downBtn = Util.instence(this).$(this, R.id.id_regist_down_btn);

        if (!mCurCityName.equals("")) {
            addressText.setText(mCurCityName);
        }
    }

    private void initEvent() {
        addressText.setOnClickListener(clickListener);
        downBtn.setOnClickListener(clickListener);
        cameraBtn.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //地址选择
                case R.id.id_user_address_edit:
                    mCityChoseDialog.show();
                    break;
                //完成
                case R.id.id_regist_down_btn:
                    completeStuInfo();
                    break;
                //头像设置
                case R.id.id_camera_btn:
                    RegisterInfoPhotoDialog dialog = new RegisterInfoPhotoDialog(SignUpInfoActivity.this);
                    dialog.show();
                    break;
            }
        }
    };

    /**
     * 完善学生资料
     */
    public void completeStuInfo() {
        String studentName = nameEdit.getText().toString();
        String cityId = mCurCityId;
        if (pd != null) {
            pd.dismiss();
        }
        pd = ProgressDialog.show(SignUpInfoActivity.this, null, "数据提交中，请稍后……");
        this.slPresenter.completeStuInfo(studentId, cityId, studentName, accessToken, mPhotoPath, new SLCallbackListener<CompStuResponse>() {
            @Override
            public void onSuccess(CompStuResponse compStuResponse) {
                if (pd != null) {
                    pd.dismiss();
                }
                SharedPreferences sharedPreferences = getSharedPreferences("session", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("cell_phone",compStuResponse.getCell_phone());
                editor.putString("name",compStuResponse.getName());
                editor.putString("city_id",compStuResponse.getCity());
                editor.putString("avatar",compStuResponse.getAvatar());
                editor.commit();
                Toast.makeText(context, "完善资料成功！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, IndexActivity.class);
                startActivity(intent);
                SignUpInfoActivity.this.finish();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                if (pd != null) {
                    pd.dismiss();
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult : requestCode = " + requestCode + " resultCode = " + resultCode);
        if (requestCode == 100) {//相机
            if (resultCode == RESULT_OK && null != data) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                FileOutputStream b = null;
                String str = null;
                Date date = null;
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");// 获取当前时间，进一步转化为字符串
                date = new Date(System.currentTimeMillis());
                str = format.format(date);
                mPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "haha" + File.separator +
                        "icon_cache" + File.separator + str + ".jpg";
                File photo = new File(mPhotoPath);
                photo.getParentFile().mkdirs();
                if (!photo.exists()) {
                    try {
                        photo.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    b = new FileOutputStream(mPhotoPath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        b.flush();
                        b.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //mPhotoPath = mPhotoUtil.getPath(getApplicationContext(), uri);
                Toast.makeText(this, "照片拍摄成功！", Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(SignUpInfoActivity.this, "取消头像设置", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 200) {//从图库选择
            if (resultCode == RESULT_OK && null != data) {
                Uri uri = data.getData();
                Log.v(TAG, "onActivityResult : uri -> " + uri);
                mPhotoPath = mPhotoUtil.getPath(getApplicationContext(), uri);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(SignUpInfoActivity.this, "取消头像设置", Toast.LENGTH_SHORT).show();
            }
        }

        super.

                onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Bitmap photo) {
        curPhoto = photo;
        int radius = Util.instence(this).dip2px(RegisterInfoPhotoDialog.output_X) / 2;
        if (photo.getWidth() < Util.instence(this).dip2px(RegisterInfoPhotoDialog.output_X)
                || photo.getHeight() < Util.instence(this).dip2px(RegisterInfoPhotoDialog.output_Y)) {
            Drawable res = new BitmapDrawable(getResources(),
                    PictrueGet.createCircleImage(PictrueGet.zoomBitmap(photo, radius, radius), radius));
            cameraBtn.setBackgroundDrawable(res);
            cameraBtn.setImageDrawable(null);
        } else {
            Drawable res = new BitmapDrawable(getResources(),
                    PictrueGet.createCircleImage(PictrueGet.extractMiniThumb(photo, radius * 2, radius * 2, false), radius));
            cameraBtn.setBackgroundDrawable(res);
            cameraBtn.setImageDrawable(null);
        }
    }
}
