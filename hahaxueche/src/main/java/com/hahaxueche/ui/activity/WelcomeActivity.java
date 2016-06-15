package com.hahaxueche.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;

import com.hahaxueche.R;
import com.hahaxueche.model.user.Session;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.ui.activity.mySetting.MSBaseActivity;
import com.hahaxueche.ui.activity.signupLogin.SignUpInfoActivity;
import com.hahaxueche.ui.activity.signupLogin.StartActivity;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by gibxin on 2016/3/6.
 */
public class WelcomeActivity extends MSBaseActivity {
    private SharedPreferencesUtil spUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 声明使用自定义标题
        setContentView(R.layout.activity_welcome);
        spUtil = new SharedPreferencesUtil(this);
        Handler x = new Handler();
        x.postDelayed(new splashhandler(), 2000);
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

    class splashhandler implements Runnable {

        public void run() {
            User user = spUtil.getUser();
            if (null != user && null != user.getStudent() && null != user.getSession()) {//内存中有用户信息，自动登录
                doAutoLogin(spUtil.getUser());
            } else {
                startActivity(new Intent(getApplication(), StartActivity.class));
                WelcomeActivity.this.finish();
            }
        }

    }

    /**
     * 自动登录
     *
     * @param user
     */
    private void doAutoLogin(final User user) {
        this.msPresenter.getStudent(user.getStudent().getId(), user.getSession().getAccess_token(), new MSCallbackListener<Student>() {
            @Override
            public void onSuccess(Student student) {
                user.setStudent(student);
                spUtil.setUser(user);
                Intent intent;
                if (TextUtils.isEmpty(user.getStudent().getCity_id()) || TextUtils.isEmpty(user.getStudent().getName())) {
                    //补全资料
                    intent = new Intent(WelcomeActivity.this, SignUpInfoActivity.class);
                } else {
                    intent = new Intent(WelcomeActivity.this, IndexActivity.class);
                }
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
    }
}
