package com.hahaxueche.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return intent != null;
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
        String ret = "";
        DecimalFormat dfInt = new DecimalFormat("#####.##");
        try {
            ret = "￥" + dfInt.format(price / 100.0);
        } catch (Exception e) {
            HHLog.e(e.getMessage());
        }
        return ret;
    }

    public static String getMoneyYuan(double price) {
        String ret = "";
        DecimalFormat dfInt = new DecimalFormat("#####.##");
        try {
            ret = dfInt.format(price / 100.0) + "元";
        } catch (Exception e) {
            HHLog.e(e.getMessage());
        }
        return ret;
    }

    public static String getCount(int count) {
        String ret = "";
        DecimalFormat dfInt = new DecimalFormat("#####.#");
        try {
            ret = count > 9999 ? (dfInt.format(count / 10000.0) + "万") : String.valueOf(count);
        } catch (Exception e) {
            HHLog.e(e.getMessage());
        }
        return ret;
    }

    public static String getRate(String rate) {
        String ret = "";
        DecimalFormat dfInt = new DecimalFormat("#####.#");
        try {
            ret = dfInt.format(Double.parseDouble(rate)) + "%";
        } catch (Exception e) {
            HHLog.e(e.getMessage());
        }
        return ret;
    }

    public static String getDateFromUTC(String UTCTime) {
        String localTimeStr = "";
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

    public static String getDateDotFromUTC(String UTCTime) {
        String localTimeStr = "";
        DateFormat UTCformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
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
        String localTimeStr = "";
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

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * API<17时，生成viewid
     *
     * @return
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    //判断email格式是否正确
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 获取url 指定name的value;
     *
     * @param url
     * @param name
     * @return
     */
    public static String getUrlValueByName(String url, String name) {
        String result = "";
        int index = url.indexOf("?");
        String temp = url.substring(index + 1);
        String[] keyValue = temp.split("&");
        for (String str : keyValue) {
            if (str.contains(name)) {
                result = str.replace(name + "=", "");
                break;
            }
        }
        return result;
    }

    public static String replaceUrlParam(String url, String key, String value) {
        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(key)) {
            int index = url.indexOf(key + "=");
            if (index != -1) {
                StringBuilder sb = new StringBuilder();
                sb.append(url.substring(0, index)).append(key + "=")
                        .append(value);
                int idx = url.indexOf("&", index);
                if (idx != -1) {
                    sb.append(url.substring(idx));
                }
                url = sb.toString();
            }
        }
        return url;
    }
}
