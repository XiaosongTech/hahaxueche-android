package com.hahaxueche.ui.activity.mySetting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.utils.Util;

/**
 * Created by wangshirui on 16/8/1.
 */
public class AddBankActivity extends MSBaseActivity {
    private EditText mEtAccountName;
    private EditText mEtAccount;
    private TextView mTvConfirm;
    private ImageButton mIbtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank);
        initViews();
        initEvents();
    }

    private void initViews() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mEtAccount = Util.instence(this).$(this, R.id.et_account);
        mEtAccountName = Util.instence(this).$(this, R.id.et_account_name);
        mTvConfirm = Util.instence(this).$(this, R.id.tv_confirm);

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
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }
}
