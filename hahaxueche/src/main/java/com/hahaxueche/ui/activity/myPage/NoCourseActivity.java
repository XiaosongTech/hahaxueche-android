package com.hahaxueche.ui.activity.myPage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.base.BannerHighlight;
import com.hahaxueche.presenter.myPage.NoCoursePresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.myPage.LoopStudentAdapter;
import com.hahaxueche.ui.dialog.BaseAlertSimpleDialog;
import com.hahaxueche.ui.view.myPage.NoCourseView;
import com.hahaxueche.ui.widget.recyclerView.DividerItemDecoration;
import com.hahaxueche.util.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/11/5.
 */

public class NoCourseActivity extends HHBaseActivity implements NoCourseView {
    ImageView mIvBack;
    TextView mTvTitle;
    @BindView(R.id.lv_loop_student)
    RecyclerView mLvLoopStudent;
    private NoCoursePresenter mPresenter;
    private LoopStudentAdapter mLoopStudentAdapter;
    private ArrayList<BannerHighlight> mBannerHightList;
    private ArrayList<BannerHighlight> mLoopBannerHightList = new ArrayList<>();
    private int loopIndex = 0;
    private final MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new NoCoursePresenter();
        setContentView(R.layout.activity_no_course);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        BaseAlertSimpleDialog dialog = new BaseAlertSimpleDialog(this, "您还没有选择教练哦~", "快去寻找教练，开启快乐学车之旅吧！");
        dialog.show();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("我的课程");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoCourseActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void initBannerHighlights(ArrayList<BannerHighlight> bannerHighlights) {
        mBannerHightList = bannerHighlights;
        for (int i = 0; i < 5; i++) {
            mLoopBannerHightList.add(mBannerHightList.get(i));
            loopIndex++;
        }
        mLoopStudentAdapter = new LoopStudentAdapter(this, mLoopBannerHightList);
        mLvLoopStudent.setLayoutManager(new LinearLayoutManager(this));
        mLvLoopStudent.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST,
                Utils.instence(this).dip2px(20)));
        mLvLoopStudent.setAdapter(mLoopStudentAdapter);
        mHandler.sendEmptyMessage(1);
    }

    @OnClick({R.id.tv_chose_coach})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_chose_coach:
                Intent intent = new Intent();
                intent.putExtra("showTab", 1);
                setResult(RESULT_OK, intent);
                NoCourseActivity.this.finish();
                break;
            default:
                break;
        }
    }

    static class MyHandler extends Handler {
        private final WeakReference<NoCourseActivity> mActivity;

        public MyHandler(NoCourseActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final NoCourseActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == 1) {
                    activity.mLoopBannerHightList.remove(activity.mLoopBannerHightList.size() - 1);
                    if (++activity.loopIndex == activity.mBannerHightList.size()) {
                        activity.loopIndex = 0;
                    }
                    activity.mLoopBannerHightList.add(0, activity.mBannerHightList.get(activity.loopIndex));
                    activity.mLoopStudentAdapter.notifyDataSetChanged();
                    activity.mHandler.sendEmptyMessageDelayed(1, 3000);
                }
            }
        }
    }
}
