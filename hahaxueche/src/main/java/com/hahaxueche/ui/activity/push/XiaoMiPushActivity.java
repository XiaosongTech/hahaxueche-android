package com.hahaxueche.ui.activity.push;

import android.os.Bundle;

import com.alibaba.sdk.android.push.MiPushSystemNotificationActivity;
import com.hahaxueche.util.HHLog;

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
     * @param title     标题
     * @param summary   内容
     * @param extMap    额外参数
     */
    @Override
    protected void onMiPushSysNoticeOpened(String title, String summary, Map<String, String> extMap) {
        HHLog.d("OnMiPushSysNoticeOpened, title: " + title + ", content: " + summary + ", extMap: " + extMap);
    }
}
