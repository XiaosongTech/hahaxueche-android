package com.hahaxueche.ui.dialog.myPage;

import android.view.View;

import com.hahaxueche.ui.activity.myPage.UploadIdCardActivity;
import com.hahaxueche.ui.dialog.FullScreenDialog;
import com.hahaxueche.ui.dialog.login.ButtonLayout;

/**
 * Created by wangshirui on 2016/11/25.
 */

public class UploadIdCardDialog extends FullScreenDialog {
    private UploadIdCardActivity mActivity;

    public UploadIdCardDialog(UploadIdCardActivity uploadIdCardActivity) {
        super(uploadIdCardActivity.getContext());
        mActivity = uploadIdCardActivity;
        buttonLayout.setButtonTxt(new String[]{"拍照", "从相册选择"});
        buttonLayout.setOnButtonClickListener(phoneClickListener);
    }

    ButtonLayout.OnButtonClickListener phoneClickListener = new ButtonLayout.OnButtonClickListener() {
        @Override
        public void onClick(View v, int index) {
            switch (index) {
                case 0://启动相机拍照设置
                    mActivity.createFile();
                    mActivity.choseImageFromCameraCapture();
                    break;
                case 1://从相册选择设置
                    mActivity.createFile();
                    mActivity.choseImageFromAlbum();
                    break;
                case 2:
                    dismiss();
                    break;
            }
            dismiss();
        }
    };

}
