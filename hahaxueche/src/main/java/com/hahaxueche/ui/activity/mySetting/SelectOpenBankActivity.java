package com.hahaxueche.ui.activity.mySetting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.student.Bank;
import com.hahaxueche.ui.adapter.mySetting.OpenBankAdapter;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/8/7.
 */
public class SelectOpenBankActivity extends MSBaseActivity {

    private ImageButton mIbtnBack;
    private EditText mEtSearchOpenBank;//搜索框
    private LinearLayout mLlyPopularBank;
    private ListView mLvOpenBank;

    private SharedPreferencesUtil spUtil;
    private ArrayList<Bank> mBankList;
    private ArrayList<Bank> mPopularBankList;
    private OpenBankAdapter mOpenBankAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_open_bank);
        spUtil = new SharedPreferencesUtil(context);
        initViews();
        initEvents();
        loadDatas();
    }

    private void initViews() {
        mEtSearchOpenBank = Util.instence(this).$(this, R.id.et_search_open_bank);
        mLlyPopularBank = Util.instence(this).$(this, R.id.lly_popular_bank);
        mLvOpenBank = Util.instence(this).$(this, R.id.lv_open_bank);
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
    }

    private void initEvents() {
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectOpenBankActivity.this.finish();
            }
        });
        mLvOpenBank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mBankList != null && mBankList.size() > 0 && position > -1 && position < mBankList.size()) {
                    selectBank(mBankList.get(position));
                }
            }
        });
    }

    /**
     * 加载热门城市
     */
    private void loadDatas() {
        mEtSearchOpenBank.clearFocus();
        if (spUtil.getConstants() == null) return;
        mBankList = spUtil.getConstants().getBanks();
        if (mBankList == null || mBankList.size() < 1) return;
        mPopularBankList = new ArrayList<>();//常用银行
        for (Bank bank : mBankList) {
            if (bank.is_popular()) {
                mPopularBankList.add(bank);
            }
        }

        int rowCount = mPopularBankList.size() / 4 + 1;
        for (int i = 0; i < rowCount; i++) {
            LinearLayout honLayout = new LinearLayout(context);
            LinearLayout.LayoutParams honLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin10dp = Util.instence(context).dip2px(10);
            honLayoutParams.setMargins(0, margin10dp, 0, 0);
            honLayout.setLayoutParams(honLayoutParams);
            honLayout.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < 3; j++) {
                final int index = i * 3 + j;
                TextView tv = new TextView(context);
                LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                if (j != 0) {
                    tvLayoutParams.setMargins(margin10dp, 0, 0, 0);
                } else {
                    tvLayoutParams.setMargins(0, 0, 0, 0);
                }
                tv.setLayoutParams(tvLayoutParams);
                int topPadding = Util.instence(context).dip2px(5);
                tv.setPadding(0, topPadding, 0, topPadding);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(16);
                if (index < mPopularBankList.size()) {
                    tv.setText(mPopularBankList.get(index).getName());
                    tv.setBackgroundColor(ContextCompat.getColor(context, R.color.haha_white));
                    tv.setTextColor(ContextCompat.getColor(context, R.color.haha_black_light));
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectBank(mPopularBankList.get(index));
                        }
                    });
                }
                honLayout.addView(tv);
            }
            mLlyPopularBank.addView(honLayout);
        }
        mOpenBankAdapter = new OpenBankAdapter(SelectOpenBankActivity.this, mBankList, R.layout.adapter_open_bank);
        mLvOpenBank.setAdapter(mOpenBankAdapter);
        //setListViewHeightBasedOnChildren(mLvOpenBank);
    }

    private void selectBank(Bank bank) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("bank", bank);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        SelectOpenBankActivity.this.finish();
    }

}
