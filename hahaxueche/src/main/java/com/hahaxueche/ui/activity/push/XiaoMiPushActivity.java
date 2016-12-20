package com.hahaxueche.ui.activity.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.alibaba.sdk.android.push.MiPushSystemNotificationActivity;
import com.hahaxueche.R;
import com.hahaxueche.ui.activity.base.BaseWebViewActivity;

import java.util.Map;

/**
 * Created by wangshirui on 2016/12/20.
 */

public class XiaoMiPushActivity extends MiPushSystemNotificationActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 实现通知打开回调方法，获取通知相关信息
     *
     * @param title   标题
     * @param summary 内容
     * @param extMap  额外参数
     */
    @Override
    protected void onMiPushSysNoticeOpened(String title, String summary, Map<String, String> extMap) {
        if (extMap == null || TextUtils.isEmpty(extMap.get("url"))) return;
        Intent intent = new Intent(this, BaseWebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString("url", extMap.get("url"));
        intent.putExtras(bundle);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(summary)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        manager.notify(1, notification);//notification id 1
    }
}
