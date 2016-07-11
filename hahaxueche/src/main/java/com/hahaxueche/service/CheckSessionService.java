package com.hahaxueche.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.receiver.AlarmReceiver;
import com.hahaxueche.ui.activity.WelcomeActivity;
import com.hahaxueche.ui.activity.collector.ActivityCollector;
import com.hahaxueche.ui.activity.signupLogin.LoginActivity;
import com.hahaxueche.ui.activity.signupLogin.StartActivity;
import com.hahaxueche.utils.JsonUtils;
import com.hahaxueche.utils.SharedPreferencesUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

/**
 * 检测session有效Service
 * Created by wangshirui on 16/7/11.
 */
public class CheckSessionService extends Service {
    private SharedPreferencesUtil spUtil;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        spUtil = new SharedPreferencesUtil(getApplicationContext());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (spUtil.getUser() != null && !TextUtils.isEmpty(spUtil.getUser().getCell_phone())
                && spUtil.getUser().getSession() != null && !TextUtils.isEmpty(spUtil.getUser().getSession().getAccess_token())) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    HttpEngine httpEngine = HttpEngine.getInstance();
                    Type type = new TypeToken<BaseValid>() {
                    }.getType();
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("cell_phone", spUtil.getUser().getCell_phone());
                    try {
                        Response response = httpEngine.postHandle(paramMap, "sessions/access_token/valid", spUtil.getUser().getSession().getAccess_token());
                        String body = response.body().string();
                        Log.v("gibxin", "body -> " + body);
                        if (response.isSuccessful()) {
                            BaseValid valid = JsonUtils.deserialize(body, type);
                            if (!valid.isValid()) {
                                //session过期,强制下线
                                ActivityCollector.finishAll();
                                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                                intent.putExtra("forceOffline", true);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60;//1min
        long triggerAtTime = SystemClock.elapsedRealtime() + interval;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        } else {
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
