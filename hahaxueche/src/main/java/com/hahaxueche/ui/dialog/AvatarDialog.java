package com.hahaxueche.ui.dialog;

import android.os.Build;
import android.view.View;

import com.hahaxueche.ui.dialog.login.ButtonLayout;
import com.hahaxueche.ui.fragment.myPage.MyPageFragment;
import com.hahaxueche.util.PhotoUtil;
import com.hahaxueche.util.Utils;


/**
 * Created by gibxin on 2016/2/10.
 */
public class AvatarDialog extends FullScreenDialog {
    private MyPageFragment myPageFragment;
    //版本比较：是否是4.4及以上版本
    private final boolean mIsKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    public static int output_X = 85;
    public static int output_Y = 85;

    //头像相关工具类
    private PhotoUtil mPhotoUtil;

    public AvatarDialog(MyPageFragment myPageFragment) {
        super(myPageFragment.getContext());
        this.myPageFragment = myPageFragment;
        mPhotoUtil = new PhotoUtil(myPageFragment);
        mPhotoUtil.creatFile();

        buttonLayout.setButtonTxt(new String[]{"拍照", "从相册选择"});
        buttonLayout.setOnButtonClickListener(phoneClickListener);
    }

    ButtonLayout.OnButtonClickListener phoneClickListener = new ButtonLayout.OnButtonClickListener() {
        @Override
        public void onClick(View v, int index) {
            switch (index) {
                case 0://启动相机拍照设置
                    mPhotoUtil.choseHeadImageFromCameraCapture();
                    break;

                case 1://从相册选择设置
                    if (mIsKitKat) {
                        mPhotoUtil.selectImageUriAfterKikat();
                    } else {
                        mPhotoUtil.cropImageUri(Utils.instence(myPageFragment.getContext()).dip2px(output_X),
                                Utils.instence(myPageFragment.getContext()).dip2px(output_Y));
                    }
                    break;

                case 2:
                    dismiss();
                    break;
            }
            dismiss();
        }
    };
}
