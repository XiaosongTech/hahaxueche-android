package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.hahaxueche.R;
import com.hahaxueche.ui.util.PhotoUtil;
import com.hahaxueche.utils.Util;


/**
 * Created by gibxin on 2016/2/10.
 */
public class RegisterInfoPhotoDialog extends FullScreenDialog {
    private Activity activity;
    //版本比较：是否是4.4及以上版本
    private final boolean mIsKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    public static int output_X = 85;
    public static int output_Y = 85;

    //头像相关工具类
    private PhotoUtil mPhotoUtil;

    public RegisterInfoPhotoDialog(Context context)
    {
        super(context);
        activity = (Activity) context;
        mPhotoUtil = new PhotoUtil(activity);
        mPhotoUtil.creatFile();

        buttonLayout.setButtonTxt(new String[]{context.getResources().getString(R.string.user_registe_start_camera),
                context.getResources().getString(R.string.user_registe_start_album)});
        buttonLayout.setOnButtonClickListener(phoneClickListener);
    }

    ButtonLayout.OnButtonClickListener phoneClickListener = new ButtonLayout.OnButtonClickListener()
    {
        @Override
        public void onClick(View v, int index)
        {
            switch (index)
            {
                case 0://启动相机拍照设置
                    mPhotoUtil.choseHeadImageFromCameraCapture();
                    break;

                case 1://从相册选择设置
                    if (mIsKitKat)
                    {
                        mPhotoUtil.selectImageUriAfterKikat();
                    } else
                    {
                        mPhotoUtil.cropImageUri(Util.instence(activity).dip2px(output_X),
                                Util.instence(activity).dip2px(output_Y));
                    }
                    break;

                case 2:
                    dismiss();
                    break;
            }
            dismiss();
        }
    };

    @Override
    public void show()
    {
        super.show();
        buttonLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                buttonLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.in_downup));
            }
        });
    }

    @Override
    public void dismiss()
    {
        super.dismiss();
        buttonLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.out_updown));
    }
}
