package com.hahaxueche.ui.view.homepage;

import android.text.SpannableString;

import com.hahaxueche.model.base.Banner;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/9/17.
 */
public interface HomepageView extends HHBaseView {
    /**
     * 打开webview
     *
     * @param url
     */
    void openWebView(String url);

    /**
     * 显示信息
     *
     * @param message
     */
    void showMessage(String message);

    /**
     * 设置驾校数量显示
     *
     * @param ss
     */
    void setDrivingSchoolCountDisplay(SpannableString ss);

    /**
     * 设置教练数量显示
     *
     * @param ss
     */
    void setCoachCountDisplay(SpannableString ss);

    /**
     * 设置付费学员数量显示
     *
     * @param ss
     */
    void setPaidStudentCountDisplay(SpannableString ss);

    /**
     * 显示城市选择对话框
     */
    void showCityChoseDialog();

    /**
     * banner init
     *
     * @param bannerArrayList
     */
    void initBanners(ArrayList<Banner> bannerArrayList);

    void navigateToReferFriends();

    void alertToRegister();
}
