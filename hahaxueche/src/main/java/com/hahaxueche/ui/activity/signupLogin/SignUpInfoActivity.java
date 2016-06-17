package com.hahaxueche.ui.activity.signupLogin;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.hahaxueche.model.user.Session;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.signupLogin.SLCallbackListener;
import com.hahaxueche.ui.activity.collector.ActivityCollector;
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

    private TextView mTvCoupon;
    private EditText mEtCoupon;

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
        nameEdit = Util.instence(this).$(this, R.id.id_user_name_edit);
        addressText = Util.instence(this).$(this, R.id.id_user_address_edit);
        downBtn = Util.instence(this).$(this, R.id.id_regist_down_btn);
        mTvCoupon = (TextView) findViewById(R.id.tv_coupon);
        mEtCoupon = (EditText) findViewById(R.id.et_coupon);
        if (!mCurCityName.equals("")) {
            addressText.setText(mCurCityName);
        }
    }

    private void initEvent() {
        addressText.setOnClickListener(clickListener);
        downBtn.setOnClickListener(clickListener);
        mTvCoupon.setOnClickListener(clickListener);
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
                //优惠码
                case R.id.tv_coupon:
                    if (mEtCoupon.getVisibility() == View.VISIBLE) {
                        mEtCoupon.setVisibility(View.GONE);
                    } else {
                        mEtCoupon.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
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
        this.slPresenter.completeStuInfo(mStudent.getId(), cityId, studentName, mSession.getAccess_token(), mEtCoupon.getText().toString(), new SLCallbackListener<Student>() {
            @Override
            public void onSuccess(Student student) {
                if (pd != null) {
                    pd.dismiss();
                }
                User user = spUtil.getUser();
                user.setStudent(student);
                spUtil.setUser(user);
                Toast.makeText(context, "完善资料成功！", Toast.LENGTH_SHORT).show();
                ActivityCollector.finishAll();
                Intent intent = new Intent(context, IndexActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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

}
