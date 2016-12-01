package com.hahaxueche.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by wangshirui on 2016/11/29.
 */

public class DownloadContractManager {
    private Context mContext;
    private String pdfUrl;
    private static final String savePath = Environment.getExternalStorageDirectory().getPath() + "/hahaxueche/pdf/";

    private static final String saveFileName = "contract.pdf";
    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private int progress;

    private Thread downLoadThread;

    private boolean interceptFlag = false;
    private onDownloadListener onDownloadListener;

    public interface onDownloadListener {
        void finish(Uri uri);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    break;
                case DOWN_OVER:
                    onDownloadListener.finish(Uri.fromFile(new File(savePath, saveFileName)));
                    break;
                default:
                    break;
            }
        }
    };

    public DownloadContractManager(Context context, String url, onDownloadListener listener) {
        this.mContext = context;
        this.pdfUrl = url;
        this.onDownloadListener = listener;
    }

    /**
     * 下载pdf
     */
    public void downloadPdf() {
        downLoadThread = new Thread(mdownPdfRunnable);
        downLoadThread.start();
    }

    private Runnable mdownPdfRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(pdfUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File pdfPath = new File(savePath);
                if (!pdfPath.exists()) {
                    pdfPath.mkdir();
                }
                File pdfFile = new File(savePath, saveFileName);
                if (pdfFile.exists()) {
                    pdfFile.delete();
                }
                HHLog.v("path -> " + pdfFile.getPath());
                pdfFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(pdfFile);
                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    //更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        //下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);//点击取消就停止下载.

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };
}
