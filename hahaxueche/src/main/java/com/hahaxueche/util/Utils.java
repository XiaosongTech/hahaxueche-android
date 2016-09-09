package com.hahaxueche.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangshirui on 16/9/9.
 */
public class Utils {
    private static Utils mUtil;

    private static DisplayMetrics dm;

    private static Handler sHandler;

    public static Utils instence(Context context) {
        if (mUtil == null)
            mUtil = new Utils();
        if (dm == null)
            dm = context.getResources().getDisplayMetrics();
        return mUtil;
    }

    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public DisplayMetrics getDm() {
        return dm;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = dm.density;
        return (int) (dpValue * scale + 0.5f);//最后的0.5f只是为了四舍五入
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(float pxValue) {
        final float scale = dm.density;
        return (int) (pxValue / scale + 0.5f);//最后的0.5f只是为了四舍五入
    }

    public boolean isApkInstall(Context context, String pkg) {
        PackageManager mPackageManager = context.getApplicationContext().getPackageManager();
        PackageInfo intent;
        try {
            intent = mPackageManager.getPackageInfo(pkg, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        if (intent == null)
            return false;
        else
            return true;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public int sp2px(float spValue) {
        final float fontScale = dm.density;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static String getMoney(double price) {
        DecimalFormat dfInt = new DecimalFormat("#####.##");
        return "￥" + dfInt.format(price / 100);
    }

    public static String getMoneyYuan(double price) {
        DecimalFormat dfInt = new DecimalFormat("#####.##");
        return dfInt.format(price / 100) + "元";
    }

    public static String getDateFromUTC(String UTCTime) {
        String localTimeStr = null;
        DateFormat UTCformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date UTCDate = UTCformat.parse(UTCTime);
            //format.setTimeZone(TimeZone.getTimeZone("GMT-8")) ;
            localTimeStr = format.format(UTCDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return localTimeStr;
    }

    public static String getMonthDayFromUTC(String UTCTime) {
        String localTimeStr = null;
        DateFormat UTCformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat format = new SimpleDateFormat("MM-dd");
        try {
            Date UTCDate = UTCformat.parse(UTCTime);
            //format.setTimeZone(TimeZone.getTimeZone("GMT-8")) ;
            localTimeStr = format.format(UTCDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return localTimeStr;
    }

    public static void runOnUiThread(Runnable runnable) {
        if (sHandler == null) {
            sHandler = new Handler(Looper.getMainLooper());
        }
        sHandler.post(runnable);
    }
}
