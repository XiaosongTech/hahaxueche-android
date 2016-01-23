package com.hahaxueche.ui.activity.signupLogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.hahaxueche.utils.Util;

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

    private ProgressDialog pd;//进度框

    private String mCurCityId = "", mCurCityName = "", studentId = "", accessToken = "";

    private CityChoseDialog mCityChoseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_info);
        initView();
        initEvent();
        SharedPreferences sharedPreferences = getSharedPreferences("session", Activity.MODE_PRIVATE);
        accessToken = "95e7ccf91e7e266c79acc0494b48e184";//sharedPreferences.getString("access_token", "");
        studentId = "06812c2b-9dea-4bdc-bbde-b9516627b206";//sharedPreferences.getString("student_id", "");
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
    private void initEvent(){
        addressText.setOnClickListener(clickListener);
        downBtn.setOnClickListener(clickListener);
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
        this.slPresenter.completeStuInfo(studentId, cityId, studentName, accessToken, new SLCallbackListener<CompStuResponse>() {
            @Override
            public void onSuccess(CompStuResponse compStuResponse) {
                if (pd != null) {
                    pd.dismiss();
                }
                Toast.makeText(context, "完善资料成功！！！", Toast.LENGTH_SHORT).show();
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
