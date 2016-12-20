package com.hahaxueche.receiver.aliyun;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.google.gson.Gson;
import com.hahaxueche.model.base.PushObject;
import com.hahaxueche.ui.activity.base.BaseWebViewActivity;
import com.hahaxueche.util.HHLog;

/**
 * Created by wangshirui on 2016/12/20.
 */

public class HHPushReceiver extends MessageReceiver {

    @Override
    protected void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        try {
            PushObject pushObject = new Gson().fromJson(extraMap, PushObject.class);
            if (!TextUtils.isEmpty(pushObject.url)) {
                Intent intent = new Intent(context, BaseWebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putString("url", pushObject.url);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            HHLog.e(e.getMessage());
        }
        super.onNotificationOpened(context, title, summary, extraMap);
    }
}
