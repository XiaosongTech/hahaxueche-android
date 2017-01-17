package com.hahaxueche.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.hahaxueche.ui.fragment.myPage.MypageFragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by yellowlgx on 2015/7/23.
 * 用于获取android4.4版本以上的相册选取照片后的路径
 * 以及设置头像，启动裁剪等功能
 */
public class PhotoUtil {
    private MypageFragment mMypageFragment;

    //保存图片本地路径
    public static final String ACCOUNT_DIR = Environment.getExternalStorageDirectory().getPath()
            + "/hahaxueche/";
    public static final String IMGPATH = ACCOUNT_DIR;

    /* 头像文件 */
    public static final String IMAGE_FILE_NAME = "faceImage.jpeg";
    public static final String TMP_IMAGE_FILE_NAME = "tmp_faceImage.jpeg";

    private File fileone = null;
    private File filetwo = null;

    public Uri uritempFile = null;

    public PhotoUtil(MypageFragment mypageFragment) {
        mMypageFragment = mypageFragment;
    }

    public void creatFile() {
        File directory = new File(ACCOUNT_DIR);
        File imagepath = new File(IMGPATH);
        if (!directory.exists()) {
            directory.mkdir();
        }
        if (!imagepath.exists()) {
            imagepath.mkdir();
        }

        fileone = new File(IMGPATH, IMAGE_FILE_NAME);
        filetwo = new File(IMGPATH, TMP_IMAGE_FILE_NAME);

        try {
            if (!fileone.exists() && !filetwo.exists()) {
                fileone.createNewFile();
                filetwo.createNewFile();
            }
        } catch (Exception e) {
            HHLog.e(e.getMessage());
        }
    }

    //bitmap转为二进制Byte
    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public InputStream Bitmap2IS(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(mMypageFragment.getActivity().getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    // 启动手机相机拍摄照片作为头像
    public void choseHeadImageFromCameraCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = new File(IMGPATH, IMAGE_FILE_NAME);
        Uri uriToImage;
        if (Build.VERSION.SDK_INT >= 24) {
            uriToImage = FileProvider.getUriForFile(mMypageFragment.getContext(),
                    "com.hahaxueche.provider.fileProvider", imageFile);
        } else {
            uriToImage = Uri.fromFile(imageFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriToImage);
        mMypageFragment.startActivityForResult(intent, RequestCode.REQUEST_CODE_TAKE_A_PICTURE);
    }

    /**
     * <br>功能简述:裁剪图片方法实现---------------------- 相册
     */
    public void cropImageUri(int width, int height) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", Util.instence(this).dip2px(output_X));
//        intent.putExtra("outputY", Util.instence(this).dip2px(output_X));
        intent.putExtra("outputX", width * 2);
        intent.putExtra("outputY", height * 2);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        File imageFile = new File(IMGPATH, IMAGE_FILE_NAME);
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(mMypageFragment.getContext(),
                    "com.hahaxueche.provider.fileProvider", imageFile);
            HHLog.v(uritempFile.toString());
        } else {
            imageUri = Uri.fromFile(imageFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        mMypageFragment.startActivityForResult(intent, RequestCode.REQUEST_CODE_SELECT_A_PICTURE);
    }


    /**
     * <br>功能简述:4.4以上裁剪图片方法实现---------------------- 相册
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void selectImageUriAfterKikat() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        mMypageFragment.startActivityForResult(intent, RequestCode.REQUEST_CODE_SELECET_A_PICTURE_AFTER_KIKAT);
    }

    /**
     * <br>功能简述:裁剪图片方法实现----------------------相机
     *
     * @param uri
     */
    public void cameraCropImageUri(Uri uri, int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX , outputY : 裁剪图片宽高
//        intent.putExtra("outputX", Util.instence(this).dip2px(output_X));
//        intent.putExtra("outputY", Util.instence(this).dip2px(output_Y));
        intent.putExtra("outputX", width * 2);
        intent.putExtra("outputY", height * 2);
//        intent.putExtra("return-data", true);
        File imageFile = new File(IMGPATH, IMAGE_FILE_NAME);
        if (Build.VERSION.SDK_INT >= 24) {
            uritempFile = FileProvider.getUriForFile(mMypageFragment.getContext(),
                    "com.hahaxueche.provider.fileProvider", imageFile);
        } else {
            uritempFile = Uri.fromFile(imageFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        mMypageFragment.startActivityForResult(intent, RequestCode.REQUEST_CODE_SET_PICTURE);
    }

    /**
     * <br>功能简述: 4.4及以上改动版裁剪图片方法实现 --------------------相机
     *
     * @param uri
     */
    public void cropImageUriAfterKikat(Uri uri, int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/jpeg");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", Util.instence(this).dip2px(output_X));
//        intent.putExtra("outputY", Util.instence(this).dip2px(output_Y));
        intent.putExtra("outputX", width * 2);
        intent.putExtra("outputY", height * 2);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        File imageFile = new File(IMGPATH, IMAGE_FILE_NAME);
        Uri uriToImage;
        if (Build.VERSION.SDK_INT >= 24) {
            uriToImage = FileProvider.getUriForFile(mMypageFragment.getContext(),
                    "com.hahaxueche.provider.fileProvider", imageFile);
        } else {
            uriToImage = Uri.fromFile(imageFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uriToImage);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        mMypageFragment.startActivityForResult(intent, RequestCode.REQUEST_CODE_SET_ALBUM_PICTURE_KITKAT);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
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

                return getDataColumn(context, contentUri, null, null);
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

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
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

    /**
     * 根据宽度压缩bitmap
     *
     * @param bitmap
     * @param newWitdh
     * @return
     */
    public Bitmap resizeBitmapByWidth(Bitmap bitmap, int newWitdh) {
        if (bitmap.getWidth() > newWitdh) {
            int height = Math.round(((float) newWitdh / bitmap.getWidth()) * bitmap.getHeight());
            Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, newWitdh,
                    height, true);
            return newBitmap;
        } else {
            return bitmap;
        }
    }

}
