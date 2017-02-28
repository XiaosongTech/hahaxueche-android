package com.hahaxueche.ui.fragment.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hahaxueche.R;
import com.hahaxueche.ui.activity.myPage.MyInsuranceActivity;
import com.hahaxueche.ui.activity.myPage.PurchaseInsuranceActivity;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.RequestCode;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wangshirui on 2017/2/28.
 */

public class PayInsuranceFragment extends HHBaseFragment {
    public static PayInsuranceFragment newInstance() {
        PayInsuranceFragment fragment = new PayInsuranceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_insurance, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.tv_purchase_insurance})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_purchase_insurance:
                startActivityForResult(new Intent(getContext(), MyInsuranceActivity.class), RequestCode.REQUEST_CODE_MY_INSURANCE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.REQUEST_CODE_MY_INSURANCE) {
            if (resultCode == RESULT_OK && null != data) {
                Intent intent = new Intent(getContext(), PurchaseInsuranceActivity.class);
                intent.putExtra("insuranceType", data.getIntExtra("insuranceType", Common.PURCHASE_INSURANCE_TYPE_150));
                startActivity(intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
