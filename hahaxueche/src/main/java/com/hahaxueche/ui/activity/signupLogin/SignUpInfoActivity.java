package com.hahaxueche.ui.activity.signupLogin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
import com.hahaxueche.model.user.Session;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.signupLogin.SLCallbackListener;
import com.hahaxueche.ui.dialog.CityChoseDialog;
import com.hahaxueche.ui.dialog.RegisterInfoPhotoDialog;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.ui.util.PhotoUtil;
import com.hahaxueche.ui.util.PictrueGet;
import com.hahaxueche.utils.SharedPreferencesUtil;
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

    private ProgressDialog pd;//进度框

    private String mCurCityId = "", mCurCityName = "";

    private CityChoseDialog mCityChoseDialog;
    private String TAG = "SignUpInfoActivity";
    private SharedPreferencesUtil spUtil;
    private Session mSession;
    private Student mStudent;
    private static final int PERMISSIONS_REQUEST = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_info);
        mPhotoUtil = new PhotoUtil(this);
        spUtil = new SharedPreferencesUtil(this);
        mSession = spUtil.getUser().getSession();
        mStudent = spUtil.getUser().getStudent();
        initView();
        initEvent();
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
                    // Check the SDK version and whether the permission is already granted or not.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                    || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_REQUEST);
                        //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                    } else {
                        // Android version is lesser than 6.0 or the permission is already granted.
                        showPhotoDialog();
                    }
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
        this.slPresenter.completeStuInfo(mStudent.getId(), cityId, studentName, mSession.getAccess_token(), PhotoUtil.IMGPATH + "/" + PhotoUtil.IMAGE_FILE_NAME, new SLCallbackListener<Student>() {
            @Override
            public void onSuccess(Student student) {
                if (pd != null) {
                    pd.dismiss();
                }
                User user = spUtil.getUser();
                user.setStudent(student);
                spUtil.setUser(user);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showPhotoDialog();
            } else {
                Toast.makeText(this, "请允许读写sdcard权限，不然我们无法完成头像采集操作", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPhotoDialog() {
        RegisterInfoPhotoDialog dialog = new RegisterInfoPhotoDialog(SignUpInfoActivity.this);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PhotoUtil.SELECT_A_PICTURE) {
            if (resultCode == RESULT_OK && null != data) {
                //4.4以下的;
                Bitmap bitmap = mPhotoUtil.decodeUriAsBitmap(Uri.fromFile(new File(PhotoUtil.IMGPATH,
                        PhotoUtil.TMP_IMAGE_FILE_NAME)));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(SignUpInfoActivity.this, "取消头像设置", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PhotoUtil.SELECET_A_PICTURE_AFTER_KIKAT) {
            if (resultCode == RESULT_OK && null != data) {
                cropImageUriAfterKikat(data);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(SignUpInfoActivity.this, "取消头像设置", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PhotoUtil.SET_ALBUM_PICTURE_KITKAT) {
            Log.i("lgx", "4.4以上上的 RESULT_OK");

            Bitmap bitmap = mPhotoUtil.decodeUriAsBitmap(Uri.fromFile(new File(PhotoUtil.IMGPATH,
                    PhotoUtil.IMAGE_FILE_NAME)));
            setImageToHeadView(bitmap);
        } else if (requestCode == PhotoUtil.TAKE_A_PICTURE) {
            Log.i("lgx", "TAKE_A_PICTURE-resultCode:" + resultCode);
            if (resultCode == RESULT_OK) {
                mPhotoUtil.cameraCropImageUri(Uri.fromFile(new File(PhotoUtil.IMGPATH, PhotoUtil.IMAGE_FILE_NAME)),
                        Util.instence(SignUpInfoActivity.this).dip2px(RegisterInfoPhotoDialog.output_X),
                        Util.instence(SignUpInfoActivity.this).dip2px(RegisterInfoPhotoDialog.output_Y));
            } else {
                Toast.makeText(SignUpInfoActivity.this, "取消头像设置", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PhotoUtil.SET_PICTURE) {
            //拍照的设置头像  不考虑版本
            Bitmap bitmap = null;
            if (resultCode == RESULT_OK && null != data) {
                if (mPhotoUtil.uritempFile != null) {
                    try {
                        bitmap = BitmapFactory
                                .decodeStream(getContentResolver().openInputStream(mPhotoUtil.uritempFile));
                        setImageToHeadView(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(SignUpInfoActivity.this, "取消头像设置", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignUpInfoActivity.this, "设置头像失败", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void cropImageUriAfterKikat(Intent data) {
        mAlbumPicturePath = mPhotoUtil.getPath(getApplicationContext(), data.getData());
        mPhotoUtil.cropImageUriAfterKikat(Uri.fromFile(new File(mAlbumPicturePath)),
                Util.instence(SignUpInfoActivity.this).dip2px(RegisterInfoPhotoDialog.output_X),
                Util.instence(SignUpInfoActivity.this).dip2px(RegisterInfoPhotoDialog.output_Y));
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
