package com.hahaxueche.ui.activity.myPage;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.presenter.myPage.UploadIdCardPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.dialog.BaseAlertDialog;
import com.hahaxueche.ui.dialog.BaseConfirmSimpleDialog;
import com.hahaxueche.ui.dialog.ShareAppDialog;
import com.hahaxueche.ui.dialog.myPage.ManualUploadDialog;
import com.hahaxueche.ui.dialog.myPage.UploadIdCardDialog;
import com.hahaxueche.ui.view.myPage.UploadIdCardView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/11/25.
 */

public class UploadIdCardActivity extends HHBaseActivity implements UploadIdCardView {
    private UploadIdCardPresenter mPresenter;
    //保存图片本地路径
    public static final String IMGPATH = Environment.getExternalStorageDirectory().getPath() + "/hahaxueche/id_card_pic/";

    public static final String IMAGE_FILE_A_NAME = "faceAImage.jpeg";
    private File fileFaceA = null;
    private Uri uriFaceA = null;
    private String imageUrlA = null;

    private boolean isFromPaySuccess;//是否从付款成功页面跳转来的
    //是否用户赔付宝页面
    private boolean isInsurance;
    @BindView(R.id.sv_main)
    ScrollView mSvMain;
    @BindView(R.id.iv_id_card_face)
    SimpleDraweeView mIvIdCardFace;
    @BindView(R.id.tv_customer_service)
    TextView mTvCustomerService;
    @BindView(R.id.tv_upload_hints)
    TextView mTvUploadHints;
    //是否已显示过分享弹窗
    private boolean isShownShare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new UploadIdCardPresenter();
        setContentView(R.layout.activity_upload_id_card);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent != null) {
            isFromPaySuccess = intent.getBooleanExtra("isFromPaySuccess", false);
            isInsurance = intent.getBooleanExtra("isInsurance", false);
            mPresenter.setIsInsurance(isInsurance);
        }
        mPresenter.attachView(this);
        initActionBar();
        changeCustomerService();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_upload_id_card);
        TextView mTvManual = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_manual);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        TextView mTvTemplate = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_template);
        if (isInsurance) {
            mTvTemplate.setVisibility(View.GONE);
        }
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("上传身份信息");
        mTvManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showManualUploadDialog();
            }
        });
        mTvTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TemplateContractActivity.class));
            }
        });
    }

    /**
     * 手动填写
     */
    private void showManualUploadDialog() {
        ManualUploadDialog dialog = new ManualUploadDialog(getContext(), new ManualUploadDialog.OnButtonClickListener() {
            @Override
            public void upload(String name, String idCardNumber) {
                mPresenter.manualUpload(name, idCardNumber);
            }
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @OnClick({R.id.rly_id_card_face,
            R.id.tv_submit,
            R.id.tv_later_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rly_id_card_face:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, RequestCode.PERMISSIONS_REQUEST_SDCARD_FOR_SAVE_IMG);
                } else {
                    showPhotoDialog();
                }
                break;
            case R.id.tv_submit:
                mPresenter.addDataTrack("upload_id_page_confirm_tapped", getContext());
                if (TextUtils.isEmpty(imageUrlA)) {
                    showMessage("请上传身份证正面");
                    return;
                }
                if (isInsurance) {
                    mPresenter.claimInsurance();
                } else {
                    mPresenter.generateAgreement();
                }
                break;
            case R.id.tv_later_submit:
                mPresenter.addDataTrack("upload_id_page_cancel_tapped", getContext());
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
    public void navigateToUserContract(String pdfUrl, String studentId) {
        Intent intent = new Intent(getContext(), MyContractActivity.class);
        intent.putExtra("pdfUrl", pdfUrl);
        intent.putExtra("studentId", studentId);
        startActivityForResult(intent, RequestCode.REQUEST_CODE_MY_CONTRACT);
    }

    @Override
    public void setFaceImage(String imageUrl) {
        mIvIdCardFace.setImageURI(imageUrl);
        imageUrlA = imageUrl;

    }

    @Override
    public void setUploadHints(String text) {
        mTvUploadHints.setText(text);
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
        fileFaceA = new File(IMGPATH, IMAGE_FILE_A_NAME);
        try {
            if (fileFaceA.exists()) {
                fileFaceA.delete();
            }
            fileFaceA.createNewFile();
        } catch (Exception e) {
            HHLog.e(e.getMessage());
            e.printStackTrace();
        }
    }

    //启动手机相机拍摄照片
    public void choseImageFromCameraCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri;
        File imageFile = new File(IMGPATH, IMAGE_FILE_A_NAME);
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(getContext(),
                    "com.hahaxueche.provider.fileProvider", imageFile);
        } else {
            imageUri = Uri.fromFile(imageFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, RequestCode.REQUEST_CODE_TAKE_A_PICTURE);
    }

    //从相册中选择
    public void choseImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, RequestCode.REQUEST_CODE_SELECT_A_PICTURE);
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
                        mPresenter.addDataTrack("upload_id_page_popup_confirm_tapped", getContext());
                    }

                    @Override
                    public void clickCancel() {
                        mPresenter.addDataTrack("upload_id_page_popup_cancel_tapped", getContext());
                        showShareDialog();
                    }
                });
        dialog.show();
    }

    /**
     * 分享得现金提示
     */
    @Override
    public void showShareDialog() {
        String shareText = mPresenter.getShareText();
        if (isFromPaySuccess) {
            shareText = "恭喜您！报名成功，" + shareText;
        }
        ShareAppDialog shareDialog = new ShareAppDialog(getContext(), shareText, false,
                new ShareAppDialog.onShareClickListener() {
                    @Override
                    public void share() {
                        setResult(RESULT_OK, null);
                        finish();
                    }
                });
        shareDialog.show();
        isShownShare = true;
    }

    @Override
    public void confirmToSubmit(String name, String num) {
        BaseAlertDialog dialog = new BaseAlertDialog(getContext(), "提示", "您已上传过身份信息，请确认\n姓名：" + name + "\n身份证号：" + num,
                "确认", new BaseAlertDialog.onButtonClickListener() {
            @Override
            public void sure() {
                mPresenter.clickSureToSubmit();
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
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CUSTOMER_SERVICE);
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShownShare) {
                showShareDialog();
            } else {
                showLaterSubmitDialog();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
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
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_SDCARD_FOR_SAVE_IMG) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showPhotoDialog();
            } else {
                showMessage("请允许读写sdcard权限，不然我们无法完成图像采集操作");
            }
        } else if (requestCode == RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CUSTOMER_SERVICE) {
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
        if (requestCode == RequestCode.REQUEST_CODE_TAKE_A_PICTURE) {
            if (resultCode == RESULT_OK) {
                File imageFile = new File(IMGPATH, IMAGE_FILE_A_NAME);
                if (Build.VERSION.SDK_INT >= 24) {
                    uriFaceA = FileProvider.getUriForFile(getContext(),
                            "com.hahaxueche.provider.fileProvider", imageFile);
                } else {
                    uriFaceA = Uri.fromFile(imageFile);
                }
                compressImage(imageFile.getAbsolutePath());
                mPresenter.uploadIdCard(imageFile.getPath());
            } else {
                showMessage("取消拍照");
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_SELECT_A_PICTURE) {
            if (resultCode == RESULT_OK && null != data) {
                String filePath;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    filePath = getPathAfterKitKat(data.getData());
                } else {
                    filePath = getPathBeforeKitKat(data.getData());
                }
                if (TextUtils.isEmpty(filePath)) {
                    showMessage("选择图片出错，请重试!");
                    return;
                }
                File imageFile = new File(filePath);
                if (Build.VERSION.SDK_INT >= 24) {
                    uriFaceA = FileProvider.getUriForFile(getContext(),
                            "com.hahaxueche.provider.fileProvider", imageFile);
                } else {
                    uriFaceA = Uri.fromFile(imageFile);
                }
                compressImage(imageFile.getAbsolutePath());
                mPresenter.uploadIdCard(imageFile.getPath());
            } else if (resultCode == RESULT_CANCELED) {
                showMessage("取消相册选择");
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_MY_CONTRACT) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, null);
                finish();
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

    private void compressImage(String imagePath) {
        int MAX_IMAGE_SIZE = 200 * 1024; // max final file size
        Bitmap bmpPic = BitmapFactory.decodeFile(imagePath);
        if ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
            BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
            bmpOptions.inSampleSize = 1;
            while ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
                bmpOptions.inSampleSize++;
                bmpPic = BitmapFactory.decodeFile(imagePath, bmpOptions);
            }
            HHLog.v("Resize: " + bmpOptions.inSampleSize);
        }
        int compressQuality = 104; // quality decreasing by 5 every loop. (start from 99)
        int streamLength = MAX_IMAGE_SIZE;
        while (streamLength >= MAX_IMAGE_SIZE) {
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            compressQuality -= 5;
            HHLog.v("Quality: " + compressQuality);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            HHLog.v("Size: " + streamLength);
        }
        try {
            FileOutputStream bmpFile = new FileOutputStream(imagePath);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
            HHLog.e("Error on saving file");
        }
    }
}
