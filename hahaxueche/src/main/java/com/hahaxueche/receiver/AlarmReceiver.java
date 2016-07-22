package com.hahaxueche.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hahaxueche.service.CheckSessionService;

/**
 * 定时任务receiver
 * Created by wangshirui on 16/7/11.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, CheckSessionService.class);
        context.startService(i);
    }
}
