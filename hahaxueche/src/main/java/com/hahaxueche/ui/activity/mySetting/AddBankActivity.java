package com.hahaxueche.ui.activity.mySetting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.student.Bank;
import com.hahaxueche.utils.Util;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangshirui on 16/8/1.
 */
public class AddBankActivity extends MSBaseActivity {
    private EditText mEtAccountName;
    private EditText mEtAccount;
    private TextView mTvConfirm;
    private ImageButton mIbtnBack;
    private LinearLayout mLlyBank;//选择开户行布局
    private MaterialSpinner mSpBank;
    private RelativeLayout mRlyCity;//选择开户地布局
    private TextView mTvCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank);
        initViews();
        initEvents();
        loadOpenBanks();
        loadDatas();
    }

    private void initViews() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mEtAccount = Util.instence(this).$(this, R.id.et_account);
        mEtAccountName = Util.instence(this).$(this, R.id.et_account_name);
        mTvConfirm = Util.instence(this).$(this, R.id.tv_confirm);
        mLlyBank = Util.instence(this).$(this, R.id.lly_bank);
        mSpBank = Util.instence(this).$(this, R.id.sp_bank);
        mRlyCity = Util.instence(this).$(this, R.id.rly_city);
        mTvCity = Util.instence(this).$(this, R.id.tv_city);

    }

    private void initEvents() {
        mEtAccountName.clearFocus();
        mTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddBankActivity.this, "account -> " + mEtAccount.getText().toString() + "; name -> " + mEtAccountName.getText().toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("isUpdate", true);
                setResult(RESULT_OK, intent);
                AddBankActivity.this.finish();
            }
        });

        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBankActivity.this.finish();
            }
        });
        mRlyCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), SelectCityActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    private void loadOpenBanks() {
        List<String> dataset = new LinkedList<>(Arrays.asList("工商银行", "交通银行", "招商银行", "建设银行", "广发银行"));
        mSpBank.setItems(dataset);
        mSpBank.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

            }
        });
    }

    private void loadDatas() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            try {
                Bank bank = (Bank) bundle.get("bank");
                mEtAccountName.setText(bank.getAccount_name());
                mEtAccount.setText(bank.getAccount());
            } catch (Exception e) {

            }
        }
    }
}
