package com.hahaxueche.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;

import com.hahaxueche.R;
import com.hahaxueche.model.user.SessionModel;
import com.hahaxueche.model.student.StudentModel;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.ui.activity.mySetting.MSBaseActivity;
import com.hahaxueche.ui.activity.signupLogin.StartActivity;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by gibxin on 2016/3/6.
 */
public class WelcomeActivity extends MSBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 声明使用自定义标题
        setContentView(R.layout.activity_welcome);
        //根据sp中是否有session和student，判断打开页面
        final SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        SessionModel curSession = spUtil.getSession();
        StudentModel curStudent = spUtil.getStudent();
        if (curSession != null && curStudent != null &&
                !TextUtils.isEmpty(curStudent.getId()) &&
                !TextUtils.isEmpty(curSession.getAccess_token())) {
            this.msPresenter.getStudent(curStudent.getId(), curSession.getAccess_token(), new MSCallbackListener<StudentModel>() {
                @Override
                public void onSuccess(StudentModel student) {
                    spUtil.setStudent(student);
                    Intent intent = new Intent(WelcomeActivity.this, IndexActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }

                @Override
                public void onFailure(String errorEvent, String message) {
                    Intent intent = new Intent(WelcomeActivity.this, StartActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }
            });
        } else {
            Intent intent = new Intent(WelcomeActivity.this, StartActivity.class);
            startActivity(intent);
            WelcomeActivity.this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
