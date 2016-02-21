package com.hahaxueche.ui.activity.findCoach;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.ui.dialog.ShareAppDialog;
import com.hahaxueche.ui.dialog.ZoomImgDialog;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.ui.widget.imageSwitcher.ImageSwitcher;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 教练详情Activity
 * Created by gibxin on 2016/2/13.
 */
public class CoachDetailActivity extends Activity implements ImageSwitcher.OnSwitchItemClickListener {
    private CircleImageView civCdCoachAvatar;//教练头像
    private CircleImageView civCdCorpCoachAvater1; //合作教练1头像
    private CircleImageView civCdCorpCoachAvater2; //合作教练2头像
    private CircleImageView cirCommentStuAvatar1;//评价学员1头像
    private CircleImageView cirCommentStuAvatar2;//评价学员1头像
    private TextView tvCdCoachName;//教练姓名
    private TextView tvCdCoachDescription;//教练描述
    private ImageSwitcher isCdCoachDetail;//教练照片
    private ZoomImgDialog zoomImgDialog = null;
    private ImageButton  ibtnCoachDetialBack;//回退按钮
    private ImageView ivShare;//分享
    private ShareAppDialog shareAppDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_detail);
        initView();
        initEvent();
    }

    private void initView() {
        civCdCoachAvatar = Util.instence(this).$(this, R.id.cir_cd_coach_avatar);
        civCdCorpCoachAvater1 = Util.instence(this).$(this, R.id.cir_cd_corp_coach1);
        civCdCorpCoachAvater2 = Util.instence(this).$(this, R.id.cir_cd_corp_coach2);
        cirCommentStuAvatar1 = Util.instence(this).$(this, R.id.cir_cd_comment_stu1);
        cirCommentStuAvatar2 = Util.instence(this).$(this, R.id.cir_cd_comment_stu2);

        tvCdCoachName = Util.instence(this).$(this, R.id.tv_cd_coach_name);
        tvCdCoachDescription = Util.instence(this).$(this, R.id.tv_cd_coach_description);
        ibtnCoachDetialBack = Util.instence(this).$(this, R.id.ibtn_coach_detail_back);

        tvCdCoachName.setText("张三");
        String coachDescription = "  在练车这种日复一日年复一年的重复性工作中，难免会有教练会遇到各种各样的问题，一些教练可能会控制不好自己的情绪，" +
                "课时我觉得与学员相处是一件非常快乐的事儿。每个教练都千万种理由解释情绪问题，可是这样肯定会影响到教学，我的学员不会担心这种问题，" +
                "我会耐心的对待每一位学员。";
        tvCdCoachDescription.setText(coachDescription);

        //String url = "http://haha-staging.oss-cn-shanghai.aliyuncs.com/uploads/student/avatar/06812c2b-9dea-4bdc-bbde-b9516627b206/20160213_111453.jpg";
        getCoachAvatar("http://ent.cctv.com/20080514/images/112581_1210766410375_701363666_1.jpg", civCdCoachAvatar);
        getCoachAvatar("http://www.yingkounews.com/wenti/ttkx/tyzx/lq/201409/W020140915540308090003.jpg",civCdCorpCoachAvater1);
        getCoachAvatar("http://www.taiwan.cn/xwzx/Technology/201411/W020141122586458692101.jpg",civCdCorpCoachAvater2);
        getCoachAvatar("http://img001.21cnimg.com/photos/album/20160204/m600/14F9F83CD7AC2266503030C7620299FE.jpeg",cirCommentStuAvatar1);
        getCoachAvatar("http://i3.sinaimg.cn/gm/cr/2013/0226/3279497539.jpg",cirCommentStuAvatar2);
        isCdCoachDetail = Util.instence(this).$(this, R.id.is_cd_coach_switcher);
        ArrayList<String> s = new ArrayList<String>();
        s.add("http://img2.3lian.com/2014/f5/158/d/87.jpg");
        s.add("http://img2.3lian.com/2014/f5/158/d/88.jpg");
        s.add("http://img2.3lian.com/2014/f5/158/d/89.jpg");
        s.add("http://img2.3lian.com/2014/f5/158/d/90.jpg");
        isCdCoachDetail.updateImages(s);

        ivShare = Util.instence(this).$(this, R.id.iv_share);
        shareAppDialog = new ShareAppDialog(this);

    }

    private void initEvent() {
        isCdCoachDetail.setIndicatorRadius(Util.instence(this).dip2px(3));
        isCdCoachDetail.setIndicatorDivide(Util.instence(this).dip2px(15));
        isCdCoachDetail.setOnSwitchItemClickListener(this);
        ibtnCoachDetialBack.setOnClickListener(mClickListener);
        ivShare.setOnClickListener(mClickListener);
    }

    private void getCoachAvatar(String url, CircleImageView civCoachAvatar) {
        final int iconWidth = Util.instence(this).dip2px(60);
        final int iconHeight = iconWidth;
        Picasso.with(this).load(url).resize(iconWidth, iconHeight)
                .into(civCoachAvatar);
    }

    @Override
    public void onSwitchClick(String url, List<String> urls) {
        if (zoomImgDialog == null)
            zoomImgDialog = new ZoomImgDialog(this, R.style.zoom_dialog);
        zoomImgDialog.setZoomImgeRes(url, urls, "");
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ibtn_coach_detail_back:
                    CoachDetailActivity.this.finish();
                    break;
                case R.id.iv_share:
                    shareAppDialog.show();
                    break;
            }
        }
    };
}
