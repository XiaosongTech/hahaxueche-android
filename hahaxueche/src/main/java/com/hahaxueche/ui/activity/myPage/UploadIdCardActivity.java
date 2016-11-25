package com.hahaxueche.ui.activity.myPage;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.presenter.myPage.UploadIdCardPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.dialog.myPage.UploadIdCardDialog;
import com.hahaxueche.ui.view.myPage.UploadIdCardView;
import com.hahaxueche.util.HHLog;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/11/25.
 */

public class UploadIdCardActivity extends HHBaseActivity implements UploadIdCardView {
    private UploadIdCardPresenter mPresenter;
    private int choseImageFace = 0;//0：正面；1：反面
    private static final int PERMISSIONS_REQUEST_SDCARD = 600;
    //保存图片本地路径
    public static final String IMGPATH = Environment.getExternalStorageDirectory().getPath() + "/hahaxueche/id_card_pic/";

    public static final String IMAGE_FILE_A_NAME = "faceAImage.jpeg";
    public static final String IMAGE_FILE_B_NAME = "faceBImage.jpeg";
    private File fileFaceA = null;
    private File fileFaceB = null;

    public static final int TAKE_A_PICTURE = 10;
    public static final int SELECET_A_PICTURE = 50;
    @BindView(R.id.sv_main)
    ScrollView mSvMain;
    @BindView(R.id.iv_id_card_face)
    SimpleDraweeView mIvIdCardFace;
    @BindView(R.id.iv_id_card_face_back)
    SimpleDraweeView mIvIdCardFaceBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new UploadIdCardPresenter();
        setContentView(R.layout.activity_upload_id_card);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("上传身份信息");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadIdCardActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @OnClick({R.id.rly_id_card_face,
            R.id.rly_id_card_face_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rly_id_card_face:
                choseImageFace = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_SDCARD);
                } else {
                    showPhotoDialog();
                }
                break;
            case R.id.rly_id_card_face_back:
                choseImageFace = 1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_SDCARD);
                } else {
                    showPhotoDialog();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mSvMain, message, Snackbar.LENGTH_SHORT).show();
    }

    private void showPhotoDialog() {
        UploadIdCardDialog dialog = new UploadIdCardDialog(this);
        dialog.show();
    }

    public void createFile() {
        File imagepath = new File(IMGPATH);
        if (!imagepath.exists()) {
            imagepath.mkdir();
        }
        if (choseImageFace == 0) {//正面
            fileFaceA = new File(IMGPATH, IMAGE_FILE_A_NAME);
            try {
                if (fileFaceA.exists()) {
                    fileFaceA.delete();
                }
                fileFaceA.createNewFile();
            } catch (Exception e) {
                HHLog.e(e.getMessage());
            }
        } else {
            fileFaceB = new File(IMGPATH, IMAGE_FILE_B_NAME);
            try {
                if (!fileFaceB.exists()) {
                    fileFaceB.createNewFile();
                }
            } catch (Exception e) {
                HHLog.e(e.getMessage());
            }
        }
    }

    //启动手机相机拍摄照片
    public void choseImageFromCameraCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(IMGPATH, choseImageFace == 0 ? IMAGE_FILE_A_NAME : IMAGE_FILE_B_NAME)));
        startActivityForResult(intent, TAKE_A_PICTURE);
    }

    //从相册中选择
    public void choseImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, SELECET_A_PICTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_SDCARD) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showPhotoDialog();
            } else {
                showMessage("请允许读写sdcard权限，不然我们无法完成图像采集操作");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_A_PICTURE) {
            if (resultCode == RESULT_OK) {
                if (choseImageFace == 0) {
                    mIvIdCardFace.setImageURI(Uri.fromFile(new File(IMGPATH, IMAGE_FILE_A_NAME)));
                } else {
                    mIvIdCardFaceBack.setImageURI(Uri.fromFile(new File(IMGPATH, IMAGE_FILE_B_NAME)));
                }
            } else {
                showMessage("取消拍照");
            }
        } else if (requestCode == SELECET_A_PICTURE) {
            if (resultCode == RESULT_OK && null != data) {
                String filePath = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    filePath = getPathAfterKitKat(data.getData());
                } else {
                    filePath = getPathBeforeKitKat(data.getData());
                }
                if (TextUtils.isEmpty(filePath)) {
                    showMessage("选择图片出错，请重试!");
                    return;
                }
                if (choseImageFace == 0) {
                    mIvIdCardFace.setImageURI(Uri.fromFile(new File(filePath)));
                } else {
                    mIvIdCardFaceBack.setImageURI(Uri.fromFile(new File(filePath)));
                }
            } else if (resultCode == RESULT_CANCELED) {
                showMessage("取消相册选择");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPathAfterKitKat(Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(this, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(this, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(this, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private String getPathBeforeKitKat(Uri uri) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
