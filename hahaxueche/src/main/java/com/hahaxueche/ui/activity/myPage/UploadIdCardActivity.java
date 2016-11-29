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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.presenter.myPage.UploadIdCardPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.dialog.BaseAlertDialog;
import com.hahaxueche.ui.dialog.BaseConfirmSimpleDialog;
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
    private Uri uriFaceA = null;
    private Uri uriFaceB = null;
    private String imageUrlA = null;
    private String imageUrlB = null;

    public static final int TAKE_A_PICTURE = 10;
    public static final int SELECET_A_PICTURE = 50;
    private boolean isFromPaySuccess;//是否从付款成功页面跳转来的
    @BindView(R.id.sv_main)
    ScrollView mSvMain;
    @BindView(R.id.iv_id_card_face)
    SimpleDraweeView mIvIdCardFace;
    @BindView(R.id.iv_id_card_face_back)
    SimpleDraweeView mIvIdCardFaceBack;
    @BindView(R.id.tv_customer_service)
    TextView mTvCustomerService;

    private static final int PERMISSIONS_REQUEST_CELL_PHONE = 601;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            isFromPaySuccess = intent.getBooleanExtra("isFromPaySuccess", false);
        }
        mPresenter = new UploadIdCardPresenter();
        setContentView(R.layout.activity_upload_id_card);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        changeCustomerService();
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
                showLaterSubmitDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @OnClick({R.id.rly_id_card_face,
            R.id.rly_id_card_face_back,
            R.id.tv_submit,
            R.id.tv_later_submit})
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
            case R.id.tv_submit:
                if (TextUtils.isEmpty(imageUrlA)) {
                    showMessage("请上传身份证正面");
                    return;
                }
                if (TextUtils.isEmpty(imageUrlB)) {
                    showMessage("请上传身份证反面");
                    return;
                }
                mPresenter.uploadInfo();
                break;
            case R.id.tv_later_submit:
                showLaterSubmitDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mSvMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToUserContract(String pdfUrl) {
        Intent intent = new Intent(getContext(), MyContractActivity.class);
        intent.putExtra("pdfUrl", pdfUrl);
        startActivity(intent);
    }

    @Override
    public void setFaceImage(String imageUrl) {
        mIvIdCardFace.setImageURI(imageUrl);
        imageUrlA = imageUrl;

    }

    @Override
    public void setFaceBackImage(String imageUrl) {
        mIvIdCardFaceBack.setImageURI(imageUrl);
        imageUrlB = imageUrl;
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

    /**
     * 稍后上传，提示
     */
    private void showLaterSubmitDialog() {
        BaseConfirmSimpleDialog dialog = new BaseConfirmSimpleDialog(getContext(), "友情提醒", "如果不上传您的信息，\n我们无法保障您的合法权益！",
                "继续上传", "稍后上传",
                new BaseConfirmSimpleDialog.onClickListener() {
                    @Override
                    public void clickConfirm() {
                        //do nothing
                    }

                    @Override
                    public void clickCancel() {
                        showShareDialog();
                    }
                });
        dialog.show();
    }

    /**
     * 分享得现金提示
     */
    private void showShareDialog() {
        String shareText = mPresenter.getShareText();
        if (isFromPaySuccess) {
            shareText = "恭喜您！报名成功，" + shareText;
        }
        BaseAlertDialog dialog = new BaseAlertDialog(getContext(), "推荐好友", shareText, "分享得现金",
                new BaseAlertDialog.onButtonClickListener() {
                    @Override
                    public void sure() {
                        setResult(RESULT_OK, null);
                        finish();
                    }
                });
        dialog.show();
    }

    public void changeCustomerService() {
        String customerService = mTvCustomerService.getText().toString();
        SpannableString spCustomerServiceStr = new SpannableString(customerService);
        spCustomerServiceStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CELL_PHONE);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    // Android version is lesser than 6.0 or the permission is already granted.
                    contactService();
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getContext(), R.color.app_theme_color));
                ds.setUnderlineText(true);
                ds.clearShadowLayer();
            }
        }, customerService.indexOf("400"), customerService.indexOf("6006") + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spCustomerServiceStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.app_theme_color)),
                customerService.indexOf("400"), customerService.indexOf("6006") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spCustomerServiceStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                mPresenter.onlineAsk();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getContext(), R.color.app_theme_color));
                ds.setUnderlineText(true);
                ds.clearShadowLayer();
            }
        }, customerService.indexOf("在线客服"), customerService.indexOf("在线客服") + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spCustomerServiceStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.app_theme_color)),
                customerService.indexOf("在线客服"), customerService.indexOf("在线客服") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvCustomerService.setText(spCustomerServiceStr);
        mTvCustomerService.setHighlightColor(ContextCompat.getColor(getContext(), R.color.app_theme_color));
        mTvCustomerService.setMovementMethod(LinkMovementMethod.getInstance());
    }


    /**
     * 禁止back键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    /**
     * 联系客服
     */
    private void contactService() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4000016006"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
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
        } else if (requestCode == PERMISSIONS_REQUEST_CELL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactService();
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系客服");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_A_PICTURE) {
            if (resultCode == RESULT_OK) {
                if (choseImageFace == 0) {
                    uriFaceA = Uri.fromFile(new File(IMGPATH, IMAGE_FILE_A_NAME));
                    mPresenter.uploadIdCard(uriFaceA.getPath(), 0);
                } else {
                    uriFaceB = Uri.fromFile(new File(IMGPATH, IMAGE_FILE_B_NAME));
                    mPresenter.uploadIdCard(uriFaceB.getPath(), 1);
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
                    uriFaceA = Uri.fromFile(new File(filePath));
                    mPresenter.uploadIdCard(uriFaceA.getPath(), 0);
                } else {
                    uriFaceB = Uri.fromFile(new File(filePath));
                    mPresenter.uploadIdCard(uriFaceB.getPath(), 1);
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

    /*private void compressImage(Uri uri) {
        int MAX_IMAGE_SIZE = 200 * 1024; // max final file size
        Bitmap bmpPic = BitmapFactory.decodeFile(uri.getPath());
        if ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
            BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
            bmpOptions.inSampleSize = 1;
            while ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
                bmpOptions.inSampleSize++;
                bmpPic = BitmapFactory.decodeFile(uri.getPath(), bmpOptions);
            }
            HHLog.d("Resize: " + bmpOptions.inSampleSize);
        }
        int compressQuality = 104; // quality decreasing by 5 every loop. (start from 99)
        int streamLength = MAX_IMAGE_SIZE;
        while (streamLength >= MAX_IMAGE_SIZE) {
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            compressQuality -= 5;
            HHLog.d("Quality: " + compressQuality);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            HHLog.d("Size: " + streamLength);
        }
        try {
            FileOutputStream bmpFile = new FileOutputStream(uri.getPath());
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
            HHLog.e("Error on saving file: " + e.getMessage());
        }
    }*/
}
