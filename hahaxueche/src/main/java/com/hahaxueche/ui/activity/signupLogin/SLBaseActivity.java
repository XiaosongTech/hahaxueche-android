package com.hahaxueche.ui.activity.signupLogin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.hahaxueche.MyApplication;
import com.hahaxueche.presenter.signupLogin.SLPresenter;

/**
 * ע���¼Activity����
 * Created by gibxin on 2016/1/19.
 */
public class SLBaseActivity extends Activity {
    // ������ʵ��
    public Context context;
    // Ӧ��ȫ�ֵ�ʵ��
    public MyApplication application;
    // ���Ĳ��Actionʵ��
    public SLPresenter slPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
    }
}
