package com.hahaxueche.presenter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.base.CityConstants;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.payment.PaymentMethod;
import com.hahaxueche.model.user.User;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.UpdateManager;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;
import com.umeng.analytics.MobclickAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangshirui on 2017/3/15.
 */

public class HHBasePresenter {

    /**
     * 在线咨询
     */
    protected void onlineAsk(User user, Context context) {
        String title = "聊天窗口的标题";
        // 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入三个参数分别为来源页面的url，来源页面标题，来源页面额外信息（可自由定义）
        // 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
        ConsultSource source = new ConsultSource("", "android", "");
        //登录用户添加用户信息
        if (user != null && user.isLogin()) {
            YSFUserInfo userInfo = new YSFUserInfo();
            userInfo.userId = user.id;
            userInfo.data = "[{\"key\":\"real_name\", \"value\":\"" + user.student.name + "\"},{\"key\":\"mobile_phone\", \"value\":\"" + user.student.cell_phone + "\"}]";
            Unicorn.setUserInfo(userInfo);
        }
        // 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable(), 如果返回为false，该接口不会有任何动作
        Unicorn.openServiceActivity(context, // 上下文
                title, // 聊天窗口的标题
                source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
        );
    }

    protected String getShortenUrlAddress(String url) {
        String urlAddress = null;
        try {
            urlAddress = "https://api.t.sina.com.cn/short_url/shorten.json?source=4186780524&url_long=" +
                    URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return urlAddress;
    }

    /**
     * 支付方式，目前支持支付宝，银行卡，分期乐
     *
     * @return
     */
    protected ArrayList<PaymentMethod> getPaymentMethod() {
        ArrayList<PaymentMethod> paymentMethods = new ArrayList<>();
        PaymentMethod aliPay = new PaymentMethod(0, R.drawable.ic_alipay_icon, "支付宝", "推荐拥有支付宝账号的用户使用");
        PaymentMethod wxlPay = new PaymentMethod(5, R.drawable.ic_wx_icon, "微信支付", "推荐拥有微信账号的用户使用");
        PaymentMethod cardPay = new PaymentMethod(4, R.drawable.ic_cardpay_icon, "银行卡", "一网通支付，支持所有主流借记卡/信用卡");
        PaymentMethod fqlPay = new PaymentMethod(1, R.drawable.logo_fenqile, "分期乐", "推荐分期使用");
        paymentMethods.add(aliPay);
        paymentMethods.add(wxlPay);
        paymentMethods.add(cardPay);
        paymentMethods.add(fqlPay);
        return paymentMethods;
    }

    protected String validatePhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return "手机号不能为空";
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber chNumberProto = phoneUtil.parse(phoneNumber, "CN");
            if (!phoneUtil.isValidNumber(chNumberProto)) {
                return "您的手机号码格式有误";
            }
        } catch (NumberParseException e) {
            return "您的手机号码格式有误";
        }
        return "";
    }

    /**
     * 版本检测，是否需要更新
     *
     * @param context
     * @param versionCode
     * @return
     */
    public boolean isNeedUpdate(Context context, int versionCode) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            int versioncode = pi.versionCode;
            if (versionCode > versioncode) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 提示更新
     *
     * @param context
     */
    public void alertToUpdate(Context context) {
        UpdateManager updateManager = new UpdateManager(context);
        updateManager.alertToUpdate();
    }

    public void addDataTrack(String event, Context context) {
        this.addDataTrack(event, context, null);
    }

    public void addDataTrack(String event, Context context, HashMap<String, String> map) {
        HHBaseApplication application = HHBaseApplication.get(context);
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            if (map == null) {
                map = new HashMap<>();
            }
            map.put("student_id", user.student.id);
        }
        if (map == null) {
            MobclickAgent.onEvent(context, event);
        } else {
            MobclickAgent.onEvent(context, event, map);
        }
    }

    public List<DrivingSchool> getHotDrivingSchools(Context context) {
        HHBaseApplication application = HHBaseApplication.get(context);
        return application.getCityConstants().driving_schools.subList(0, Common.MAX_DRIVING_SCHOOL_COUNT);
    }

    public int[][] getPriceRanges(Context context) {
        HHBaseApplication application = HHBaseApplication.get(context);
        CityConstants cityConstants = application.getCityConstants();
        return cityConstants.filters.prices;
    }

    public String[] getZones(Context context) {
        HHBaseApplication application = HHBaseApplication.get(context);
        CityConstants cityConstants = application.getCityConstants();
        return cityConstants.zones;
    }

    public int[] getRadius(Context context) {
        HHBaseApplication application = HHBaseApplication.get(context);
        CityConstants cityConstants = application.getCityConstants();
        return cityConstants.filters.radius;
    }

    public List<DrivingSchool> getDrivingSchools(Context context) {
        HHBaseApplication application = HHBaseApplication.get(context);
        return application.getCityConstants().driving_schools;
    }
}
