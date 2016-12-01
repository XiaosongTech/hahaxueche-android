package com.hahaxueche.ui.dialog.myPage;

import android.view.View;

import com.hahaxueche.ui.activity.myPage.MyContractActivity;
import com.hahaxueche.ui.dialog.FullScreenDialog;
import com.hahaxueche.ui.dialog.login.ButtonLayout;

/**
 * Created by wangshirui on 2016/11/30.
 */

public class MyContractDialog extends FullScreenDialog {
    private MyContractActivity mActivity;

    public MyContractDialog(MyContractActivity myContractActivity) {
        super(myContractActivity.getContext());
        mActivity = myContractActivity;
        buttonLayout.setButtonTxt(new String[]{"下载到本地", "发送邮箱"});
        buttonLayout.setOnButtonClickListener(buttonClickListener);
    }

    ButtonLayout.OnButtonClickListener buttonClickListener = new ButtonLayout.OnButtonClickListener() {
        @Override
        public void onClick(View v, int index) {
            switch (index) {
                case 0:
                    mActivity.openPdf();
                    break;
                case 1:
                    mActivity.sendEmail();
                    break;
                case 2:
                    dismiss();
                    break;
            }
            dismiss();
        }
    };
}
