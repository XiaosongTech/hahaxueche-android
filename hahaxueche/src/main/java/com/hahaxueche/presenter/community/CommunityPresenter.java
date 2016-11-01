package com.hahaxueche.presenter.community;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.community.News;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.community.CommunityView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Subscription;

/**
 * Created by wangshirui on 16/9/22.
 */

public class CommunityPresenter implements Presenter<CommunityView> {
    private CommunityView mCommunityView;
    private Subscription subscription;
    private HHBaseApplication application;
    private ArrayList<News> mNewsArrayList;

    public void attachView(CommunityView view) {
        this.mCommunityView = view;
        application = HHBaseApplication.get(mCommunityView.getContext());
    }

    public void detachView() {
        this.mCommunityView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void fetchNews() {
        mNewsArrayList = new ArrayList<>();
        News news = new News();
        news.date = "2016.03.05";
        news.title = "我的学车初体验";
        news.sub_title = "行业咨询";
        news.pic_url = "https://striker.teambition.net/storage/110ke9b940d70217cc5b5e4c52379ef441ed?download=%E5%9B%BE%E7%89%87%EF%BC%8D%E5%AD%A6%E8%BD%A6%E5%88%9D%E4%BD%93%E9%AA%8C.png&Signature=eyJhbGciOiJIUzI1NiJ9.eyJyZXNvdXJjZSI6Ii9zdG9yYWdlLzExMGtlOWI5NDBkNzAyMTdjYzViNWU0YzUyMzc5ZWY0NDFlZCIsImV4cCI6MTQ3NDY3NTIwMH0.AkttKI7HMwAN-MSaDL-RXfRQDIcvLLxQ4rCIQq-49a8";
        news.content = "我叫于小赞，是一名大二在校生，方向感极差的我决定考驾照，然后各种心酸的去驾校咨询，还是没能找到合适的，要么距离太远，要么价位太贵......最后闺蜜给我推荐了一个叫哈哈学车的App，据说是学车神器！接着下载后，神奇的学车之旅就开始啦！";
        news.comment_count = 5;
        news.like_count = 11;
        news.read_count = 3406;
        mNewsArrayList.add(news);
        News news2 = new News();
        news2.date = "2016.03.05";
        news2.title = "如何讨好十二星座教练";
        news2.sub_title = "瞎jb扯";
        news2.pic_url = "https://striker.teambition.net/storage/110kffb43b087d6d4a1a7436e4aa0e48f11b?download=1.png&Signature=eyJhbGciOiJIUzI1NiJ9.eyJyZXNvdXJjZSI6Ii9zdG9yYWdlLzExMGtmZmI0M2IwODdkNmQ0YTFhNzQzNmU0YWEwZTQ4ZjExYiIsImV4cCI6MTQ3NDY3NTIwMH0.ORADwITsP1yVzgeTYxasboEZyb_kcYlhWBwA4XIChpM";
        news2.content = "在学车的过程中，很多学员觉得不是车难学，而是教练难相处。小伙伴们这时不要急着发脾气，或者闹小情绪，想要不翻友谊的小船，下面和哈哈学车一起，掌握与十二星座教练相处的方法吧。";
        news2.comment_count = 15;
        news2.like_count = 340;
        news2.read_count = 17204;
        mNewsArrayList.add(news2);
        mCommunityView.refreshNewsList(mNewsArrayList);
        mCommunityView.setPullLoadEnable(true);
    }

    public void loadMoreNews() {
        News news = new News();
        news.date = "2016.03.05";
        news.title = "我的学车初体验";
        news.sub_title = "行业咨询";
        news.pic_url = "https://striker.teambition.net/storage/110ke9b940d70217cc5b5e4c52379ef441ed?download=%E5%9B%BE%E7%89%87%EF%BC%8D%E5%AD%A6%E8%BD%A6%E5%88%9D%E4%BD%93%E9%AA%8C.png&Signature=eyJhbGciOiJIUzI1NiJ9.eyJyZXNvdXJjZSI6Ii9zdG9yYWdlLzExMGtlOWI5NDBkNzAyMTdjYzViNWU0YzUyMzc5ZWY0NDFlZCIsImV4cCI6MTQ3NDY3NTIwMH0.AkttKI7HMwAN-MSaDL-RXfRQDIcvLLxQ4rCIQq-49a8";
        news.content = "我叫于小赞，是一名大二在校生，方向感极差的我决定考驾照，然后各种心酸的去驾校咨询，还是没能找到合适的，要么距离太远，要么价位太贵......最后闺蜜给我推荐了一个叫哈哈学车的App，据说是学车神器！接着下载后，神奇的学车之旅就开始啦！";
        news.comment_count = 5;
        news.like_count = 11;
        news.read_count = 3406;
        mNewsArrayList.add(news);
        News news2 = new News();
        news2.date = "2016.03.05";
        news2.title = "如何讨好十二星座教练";
        news2.sub_title = "瞎jb扯";
        news2.pic_url = "https://striker.teambition.net/storage/110kffb43b087d6d4a1a7436e4aa0e48f11b?download=1.png&Signature=eyJhbGciOiJIUzI1NiJ9.eyJyZXNvdXJjZSI6Ii9zdG9yYWdlLzExMGtmZmI0M2IwODdkNmQ0YTFhNzQzNmU0YWEwZTQ4ZjExYiIsImV4cCI6MTQ3NDY3NTIwMH0.ORADwITsP1yVzgeTYxasboEZyb_kcYlhWBwA4XIChpM";
        news2.content = "在学车的过程中，很多学员觉得不是车难学，而是教练难相处。小伙伴们这时不要急着发脾气，或者闹小情绪，想要不翻友谊的小船，下面和哈哈学车一起，掌握与十二星座教练相处的方法吧。";
        news2.comment_count = 15;
        news2.like_count = 340;
        news2.read_count = 17204;
        mNewsArrayList.add(news2);
        mCommunityView.addMoreNewsList(mNewsArrayList);
    }

    public void clickGroupBuyCount() {
        //团购点击
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            HashMap<String, String> map = new HashMap();
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mCommunityView.getContext(), "club_page_group_purchase_tapped", map);
        } else {
            MobclickAgent.onEvent(mCommunityView.getContext(), "club_page_group_purchase_tapped");
        }

    }

    public void clickTestLibCount() {
        //在线题库点击
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            HashMap<String, String> map = new HashMap();
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mCommunityView.getContext(), "club_page_online_test_tapped", map);
        } else {
            MobclickAgent.onEvent(mCommunityView.getContext(), "club_page_online_test_tapped");
        }

    }
}
