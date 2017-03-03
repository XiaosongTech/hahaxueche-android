package com.hahaxueche.ui.fragment.community;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hahaxueche.R;
import com.hahaxueche.presenter.community.PayInsurancePresenter;
import com.hahaxueche.ui.activity.community.ExamLibraryActivity;
import com.hahaxueche.ui.activity.findCoach.PaySuccessActivity;
import com.hahaxueche.ui.activity.myPage.MyInsuranceActivity;
import com.hahaxueche.ui.activity.myPage.PurchaseInsuranceActivity;
import com.hahaxueche.ui.activity.myPage.ReferFriendsActivity;
import com.hahaxueche.ui.activity.myPage.StudentReferActivity;
import com.hahaxueche.ui.activity.myPage.UploadIdCardActivity;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.community.PayInsuranceView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.RequestCode;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wangshirui on 2017/2/28.
 */

public class PayInsuranceFragment extends HHBaseFragment implements PayInsuranceView {
    private PayInsurancePresenter mPresenter;
    private ExamLibraryActivity mActivity;

    public static PayInsuranceFragment newInstance() {
        PayInsuranceFragment fragment = new PayInsuranceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new PayInsurancePresenter();
        mActivity = (ExamLibraryActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_insurance, container, false);
        ButterKnife.bind(this, view);
        mPresenter.attachView(this);
        return view;
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
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
                if (data.getBooleanExtra("toUploadInfo", false)) {
                    Intent intent = new Intent(getContext(), UploadIdCardActivity.class);
                    intent.putExtra("isFromPaySuccess", false);
                    intent.putExtra("isInsurance", true);
                    startActivityForResult(intent, RequestCode.REQUEST_CODE_UPLOAD_ID_CARD);
                } else if (data.getBooleanExtra("toFindCoach", false)) {
                    mActivity.finishToFindCoach();
                } else {
                    Intent intent = new Intent(getContext(), PurchaseInsuranceActivity.class);
                    intent.putExtra("insuranceType", data.getIntExtra("insuranceType", Common.PURCHASE_INSURANCE_TYPE_169));
                    startActivityForResult(intent, RequestCode.REQUEST_CODE_PURCHASE_INSURANCE);
                }
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_PURCHASE_INSURANCE) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(getContext(), PaySuccessActivity.class);
                intent.putExtra("isPurchasedInsurance", true);
                intent.putExtra("isFromPurchaseInsurance", true);
                startActivityForResult(intent, RequestCode.REQUEST_CODE_PAY_SUCCESS);
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_PAY_SUCCESS) {
            Intent intent = new Intent(getContext(), UploadIdCardActivity.class);
            intent.putExtra("isFromPaySuccess", false);
            intent.putExtra("isInsurance", true);
            startActivityForResult(intent, RequestCode.REQUEST_CODE_UPLOAD_ID_CARD);
        } else if (requestCode == RequestCode.REQUEST_CODE_UPLOAD_ID_CARD) {
            mPresenter.toReferFriends();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void navigateToReferFriends() {
        startActivity(new Intent(getContext(), ReferFriendsActivity.class));
    }

    @Override
    public void navigateToStudentRefer() {
        startActivity(new Intent(getContext(), StudentReferActivity.class));
    }
}
