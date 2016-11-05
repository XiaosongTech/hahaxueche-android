package com.hahaxueche.ui.activity.findCoach;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.findCoach.SearchCoachPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.findCoach.CoachAdapter;
import com.hahaxueche.ui.view.findCoach.SearchCoachView;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

/**
 * Created by wangshirui on 16/10/4.
 */

public class SearchCoachActivity extends HHBaseActivity implements SearchCoachView {
    private EditText mEtCoachName;//教练姓名搜索框
    private ImageView mIvClear;//清除按钮
    private TextView mTvSearch;//搜索/取消
    @BindView(R.id.fly_main)
    FrameLayout mFlyMain;
    @BindView(R.id.lly_history)
    LinearLayout mLlyHistory;
    private SearchCoachPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new SearchCoachPresenter();
        setContentView(R.layout.activity_search_coach);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        initEvents();
        mPresenter.searchTextChange(mEtCoachName.getText().toString());
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_search_coach);
        mEtCoachName = ButterKnife.findById(actionBar.getCustomView(), R.id.et_coach_name);
        mIvClear = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_clear);
        mTvSearch = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_search);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    private void initEvents() {
        mEtCoachName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.searchTextChange(s.toString());
            }
        });
        mIvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtCoachName.setText("");
            }
        });
    }


    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void setRightSearch() {
        mIvClear.setVisibility(View.VISIBLE);
        mTvSearch.setText("搜索");
        mTvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //搜索教练
                mPresenter.searchCoach(mEtCoachName.getText().toString());
            }
        });
    }

    @Override
    public void setRightCancel() {
        mIvClear.setVisibility(View.GONE);
        mTvSearch.setText("取消");
        mTvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchCoachActivity.this.finish();
            }
        });
    }

    @Override
    public void loadSearchHistory(LinkedList searchHistoryList) {
        mLlyHistory.removeAllViews();
        //历史记录行
        LinearLayout.LayoutParams paramTvHistory = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tvHistory = new TextView(this);
        tvHistory.setLayoutParams(paramTvHistory);
        tvHistory.setPadding(Utils.instence(this).dip2px(14), Utils.instence(this).dip2px(12), 0, Utils.instence(this).dip2px(12));
        tvHistory.setTextSize(14);
        tvHistory.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_text));
        tvHistory.setText("历史记录");
        mLlyHistory.addView(tvHistory);
        //分割线
        LinearLayout.LayoutParams paramVwHistory = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.instence(this).dip2px(0.5f));
        View viewHistory = new View(this);
        viewHistory.setLayoutParams(paramVwHistory);
        viewHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
        mLlyHistory.addView(viewHistory);
        //记录信息
        if (searchHistoryList != null && searchHistoryList.size() > 0) {
            for (final Object coachName : searchHistoryList) {
                LinearLayout.LayoutParams paramTv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView textView = new TextView(this);
                textView.setLayoutParams(paramTv);
                textView.setText(coachName.toString());
                textView.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_dark));
                textView.setPadding(Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15), 0, Utils.instence(this).dip2px(15));
                textView.setTextSize(14);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mEtCoachName.setText(coachName.toString());
                        mPresenter.searchCoach(mEtCoachName.getText().toString());
                    }
                });
                mLlyHistory.addView(textView);
                //分割线
                LinearLayout.LayoutParams paramVw = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.instence(this).dip2px(0.5f));
                View view = new View(this);
                view.setLayoutParams(paramVw);
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
                mLlyHistory.addView(view);
            }
            //清除历史记录
            LinearLayout.LayoutParams paramTvClear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramTvClear.setMargins(0, Utils.instence(this).dip2px(12), 0, 0);
            TextView textView = new TextView(this);
            textView.setText("清空历史记录");
            textView.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
            textView.setTextSize(14);
            textView.setPadding(Utils.instence(this).dip2px(36), Utils.instence(this).dip2px(12), Utils.instence(this).dip2px(36), Utils.instence(this).dip2px(12));
            textView.setBackgroundResource(R.drawable.rect_bg_trans_bd_appcolor_ssm);
            textView.setLayoutParams(paramTvClear);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.clearHistory();
                }
            });
            mLlyHistory.addView(textView);
        }
    }

    @Override
    public void loadCoachList(ArrayList<Coach> coachList) {
        mFlyMain.removeAllViews();
        if (coachList != null && coachList.size() > 0) {
            FrameLayout.LayoutParams paramMain = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            ListView listView = new ListView(this);
            listView.setLayoutParams(paramMain);
            CoachAdapter coachItemAdapter = new CoachAdapter(this, coachList);
            listView.setAdapter(coachItemAdapter);
            listView.setDivider(ContextCompat.getDrawable(this, R.drawable.divider_left_20dp));
            listView.setDividerHeight(Utils.instence(this).dip2px(0.5f));
            //listView.setOnItemClickListener(mAdapterClickListener);
            ListView.LayoutParams paramsTvFooter = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT);
            /***********底部显示***********/
            TextView tvFooter = new TextView(this);
            tvFooter.setText("没有更多教练");
            tvFooter.setGravity(Gravity.CENTER);
            tvFooter.setPadding(0, Utils.instence(this).dip2px(10), 0, Utils.instence(this).dip2px(10));
            tvFooter.setTextSize(14);
            tvFooter.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
            tvFooter.setLayoutParams(paramsTvFooter);
            listView.addFooterView(tvFooter);
            listView.setFooterDividersEnabled(false);
            mFlyMain.addView(listView);
        } else {
            //未搜索到教练
            FrameLayout.LayoutParams paramMain = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            TextView tv = new TextView(this);
            tv.setGravity(Gravity.CENTER);
            tv.setText("O(≧口≦)O没有找到教练啊!");
            tv.setLayoutParams(paramMain);
            tv.setTextSize(14);
            tv.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
            mFlyMain.addView(tv);
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mFlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void disableButton() {
        mIvClear.setClickable(false);
        mTvSearch.setClickable(false);
    }

    @Override
    public void enableButton() {
        mIvClear.setClickable(true);
        mTvSearch.setClickable(true);
    }
}
