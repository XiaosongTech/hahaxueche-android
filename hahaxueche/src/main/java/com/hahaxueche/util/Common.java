package com.hahaxueche.util;

/**
 * Created by wangshirui on 2017/2/17.
 */

public class Common {
    //轮播图切换时间
    public static final int BANNER_TURNING_TIME = 2500;
    //未报名学车，和教练一起买的类型
    public static final int PURCHASE_INSURANCE_TYPE_WITH_NEW_COACH = 0;
    //已在哈哈平台买教练，再买赔付宝的类型
    public static final int PURCHASE_INSURANCE_TYPE_WITH_PAID_COACH = 1;
    //在其他驾校报名，在哈哈买赔付宝的类型
    public static final int PURCHASE_INSURANCE_TYPE_WITHOUT_COACH = 2;
    //C1超值班
    public static final int CLASS_TYPE_NORMAL_C1 = 0;
    //C1VIP班
    public static final int CLASS_TYPE_VIP_C1 = 1;
    //C1无忧班
    public static final int CLASS_TYPE_WUYOU_C1 = 4;
    //C2超值班
    public static final int CLASS_TYPE_NORMAL_C2 = 2;
    //C2VIP班
    public static final int CLASS_TYPE_VIP_C2 = 3;
    //C2无忧班班
    public static final int CLASS_TYPE_WUYOU_C2 = 5;
    //超值班
    public static final String CLASS_TYPE_NORMAL_NAME = "超值班";
    //VIP班
    public static final String CLASS_TYPE_VIP_NAME = "VIP班";
    //无忧班
    public static final String CLASS_TYPE_WUYOU_NAME = "无忧班";
    //超值班描述
    public static final String CLASS_TYPE_NORMAL_DESC = "四人一车，高性价比";
    //VIP班描述
    public static final String CLASS_TYPE_VIP_DESC = "一人一车，极速拿证";
    //无忧班描述
    public static final String CLASS_TYPE_WUYOU_DESC = "包补考费，不过包赔";
    //C1
    public static final int LICENSE_TYPE_C1 = 1;
    //C2
    public static final int LICENSE_TYPE_C2 = 2;
    //车友无忧班
    public static final int GROUP_TYPE_CHEYOU_WUYOU = 1;
    //分页-启始页
    public static final int START_PAGE = 1;
    //分页-每页显示数量
    public static final int PER_PAGE = 10;
    //登陆方式-密码登陆
    public static final String LOGIN_TYPE_PASSWORD = "password";
    //登陆方式-验证码登陆
    public static final String LOGIN_TYPE_AUTH = "auth";
    //发送验证码类型-重置密码
    public static final String SEND_AUTH_TYPE_RESET = "reset";
    //发送验证码类型-注册
    public static final String SEND_AUTH_TYPE_REGISTER = "register";
    //发送验证码类型-短信登陆
    public static final String SEND_AUTH_TYPE_LOGIN = "login";
    //用户类型-学员
    public static final String USER_TYPE_STUDENT = "student";
    //优秀教练
    public static final int COACH_SKILL_LEVEL_COMMON = 0;
    //普通教练
    public static final int COACH_SKILL_LEVEL_GOLDEN = 1;
    //热门驾校数量
    public static final int MAX_DRIVING_SCHOOL_COUNT = 8;
    //附近驾校数量
    public static final int MAX_NEAR_COACH_COUNT = 8;
    //选择项-不限
    public static final int NO_LIMIT = -1;
    //搜索类型-驾校
    public static final int SEARCH_TYPE_DRIVING_SCHOOL = 1;
    //搜索类型-教练
    public static final int SEARCH_TYPE_COACH = 2;
    //地图不聚合的最大level
    public static final double CLUSTER_MAX_ZOOM_LEVEL = 11;
}
