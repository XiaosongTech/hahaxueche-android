package com.hahaxueche.ui.activity.myPage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.base.Bank;
import com.hahaxueche.presenter.myPage.SelectBankPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.myPage.BankAdapter;
import com.hahaxueche.ui.view.myPage.SelectBankView;
import com.hahaxueche.ui.widget.recyclerView.DividerItemDecoration;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/2.
 */

public class SelectBankActivity extends HHBaseActivity implements SelectBankView {
    private SelectBankPresenter mPresenter;
    ImageView mIvBack;
    TextView mTvTitle;
    @BindView(R.id.lv_open_bank)
    RecyclerView mLvOpenBank;
    BankAdapter mAdapter;
    @BindView(R.id.lly_popular_bank)
    LinearLayout mLlyPopularBank;
    @BindView(R.id.et_search_open_bank)
    EditText mEtSearchOpenBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new SelectBankPresenter();
        setContentView(R.layout.activity_select_bank);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        mEtSearchOpenBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.searchBank(mEtSearchOpenBank.getText().toString().trim());
            }
        });
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("选择开户行");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectBankActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showBankList(ArrayList<Bank> banks) {
        mAdapter = new BankAdapter(this, banks, new BankAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ArrayList<Bank> banks = mPresenter.getBanks();
                if (banks != null && banks.size() > 0 && position > -1 && position < banks.size()) {
                    selectBank(banks.get(position));
                }
            }
        });
        mLvOpenBank.setLayoutManager(new LinearLayoutManager(this));
        mLvOpenBank.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mLvOpenBank.setAdapter(mAdapter);
    }

    @Override
    public void showPopularBankList(final ArrayList<Bank> popularBanks) {
        if (popularBanks == null || popularBanks.size() < 1) return;
        int rowCount = popularBanks.size() / 4 + 1;
        for (int i = 0; i < rowCount; i++) {
            LinearLayout honLayout = new LinearLayout(this);
            LinearLayout.LayoutParams honLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin10dp = Utils.instence(this).dip2px(10);
            honLayoutParams.setMargins(0, margin10dp, 0, 0);
            honLayout.setLayoutParams(honLayoutParams);
            honLayout.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < 3; j++) {
                final int index = i * 3 + j;
                TextView tv = new TextView(this);
                LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                if (j != 0) {
                    tvLayoutParams.setMargins(margin10dp, 0, 0, 0);
                } else {
                    tvLayoutParams.setMargins(0, 0, 0, 0);
                }
                tv.setLayoutParams(tvLayoutParams);
                int topPadding = Utils.instence(this).dip2px(5);
                tv.setPadding(0, topPadding, 0, topPadding);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(16);
                if (index < popularBanks.size()) {
                    tv.setText(popularBanks.get(index).name);
                    tv.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_white));
                    tv.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_dark));
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectBank(popularBanks.get(index));
                        }
                    });
                }
                honLayout.addView(tv);
            }
            mLlyPopularBank.addView(honLayout);
        }
    }

    private void selectBank(Bank bank) {
        Intent intent = new Intent();
        intent.putExtra("bank", bank);
        setResult(RESULT_OK, intent);
        SelectBankActivity.this.finish();
    }
}
