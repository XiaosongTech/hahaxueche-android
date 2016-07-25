package com.hahaxueche.ui.activity.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.hahaxueche.model.user.User;
import com.hahaxueche.ui.activity.collector.ActivityCollector;
import com.hahaxueche.utils.JsonUtils;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;

/**
 * Created by Administrator on 2016/6/16.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        Log.v("gibxin", getClass().getSimpleName());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 打开webview
     *
     * @param url
     */
    public void openWebView(String url) {
        Intent intent = new Intent(getApplication(), BaseWebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 在线咨询
     */
    public void onlineAsk(Context context) {
        String title = "聊天窗口的标题";
        // 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入三个参数分别为来源页面的url，来源页面标题，来源页面额外信息（可自由定义）
        // 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
        ConsultSource source = new ConsultSource("", "android", "");
        //登录用户添加用户信息
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(context);
        User user = spUtil.getUser();
        if (user != null && user.getSession() != null && !TextUtils.isEmpty(user.getSession().getId())
                && user.getStudent() != null) {
            YSFUserInfo userInfo = new YSFUserInfo();
            userInfo.userId = user.getId();
            userInfo.data = "[{\"key\":\"real_name\", \"value\":\"" + user.getStudent().getName() + "\"},{\"key\":\"mobile_phone\", \"value\":\"" + user.getStudent().getCell_phone() + "\"}]";
            Unicorn.setUserInfo(userInfo);
        }
        // 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable(), 如果返回为false，该接口不会有任何动作
        Unicorn.openServiceActivity(context, // 上下文
                title, // 聊天窗口的标题
                source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
        );
    }
}
