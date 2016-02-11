package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.hahaxueche.R;
import com.hahaxueche.ui.util.PhotoUtil;
import com.hahaxueche.utils.Util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private Uri fileUri;
    private File mediaFile;
    private String filePath;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 200;

    public RegisterInfoPhotoDialog(Context context) {
        super(context);
        activity = (Activity) context;

        buttonLayout.setButtonTxt(new String[]{context.getResources().getString(R.string.user_registe_start_camera),
                context.getResources().getString(R.string.user_registe_start_album)});
        buttonLayout.setOnButtonClickListener(phoneClickListener);
    }

    ButtonLayout.OnButtonClickListener phoneClickListener = new ButtonLayout.OnButtonClickListener() {
        @Override
        public void onClick(View v, int index) {
            switch (index) {
                case 0://启动相机拍照设置
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    /*fileUri = getOutputMediaFileUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);*/
                    activity.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    break;

                case 1://从相册选择设置
                    intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    //回调
                    activity.startActivityForResult(intent, SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
                    break;

                case 2:
                    dismiss();
                    break;
            }
            dismiss();
        }
    };

    @Override
    public void show() {
        super.show();
        buttonLayout.post(new Runnable() {
            @Override
            public void run() {
                buttonLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.in_downup));
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        buttonLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.out_updown));
    }

    private Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = null;
        try {
            String path = Environment.getExternalStorageDirectory() + File.separator + "haha" + File.separator +
                    "icon_cache";


            mediaStorageDir = new File(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        filePath = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
        mediaFile = new File(filePath);
        return mediaFile;
    }
}
