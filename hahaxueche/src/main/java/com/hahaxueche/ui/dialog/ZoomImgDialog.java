package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.ui.adapter.ZoomImgAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gibxin on 2016/2/13.
 */
public class ZoomImgDialog extends Dialog{
    private Context mContext;

    private ViewPager imgViewPager;
    private TextView titleView;
    private ProgressBar mLoadingView;
    private ZoomImgAdapter mAdapter;

    public ZoomImgDialog(Context context)
    {
        super(context);
    }

    public ZoomImgDialog(Context context, int theme)
    {
        super(context, theme);
        mContext = context;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.zoom_img_layout, null);
        setContentView(view);
        initAtribut();
        imgViewPager = (ViewPager) view.findViewById(R.id.id_zoom_img);
        titleView = (TextView) view.findViewById(R.id.id_zoom_title);
        mLoadingView = (ProgressBar) view.findViewById(R.id.id_zoom_loading);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void initAtribut()
    {
        // 这句话起全屏的作用
        Window win = ZoomImgDialog.this.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        win.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager m = ((Activity) mContext).getWindowManager();
//        Display display = m.getDefaultDisplay(); // 获取屏幕宽、高用
//        Rect rect = new Rect();
//        display.getRectSize(rect);
//        lp.width = rect.width();
//        lp.height = rect.height() - rect.top;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
    }

    public void setZoomImgeRes(String url, String title)
    {
        if (mAdapter == null)
        {
            mAdapter = new ZoomImgAdapter(mContext);
        }
        imgViewPager.removeAllViews();
        mAdapter.setLoadingView(mLoadingView);
        imgViewPager.setAdapter(mAdapter);
        List<String> urls = new ArrayList<String>();
        if (url != null)
            urls.add(url);
        mAdapter.update(urls);
        if (!TextUtils.isEmpty(title))
        {
            titleView.setText(title);
            titleView.setVisibility(View.VISIBLE);
        }
//        ZoomImgDialog.this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ZoomImgDialog.this.show();
    }

    public void setZoomImgeRes(String url, List<String> urls, String title)
    {
        if (mAdapter == null)
        {
            mAdapter = new ZoomImgAdapter(mContext);
        }
        imgViewPager.removeAllViews();
        mAdapter.setLoadingView(mLoadingView);
        imgViewPager.setAdapter(mAdapter);
        if (url != null)
        {
            urls.remove(url);
            urls.add(0, url);
        }
        mAdapter.update(urls);
        if (!TextUtils.isEmpty(title))
        {
            titleView.setText(title);
            titleView.setVisibility(View.VISIBLE);
        }
//        ZoomImgDialog.this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ZoomImgDialog.this.show();
    }

    @Override
    public void dismiss()
    {
        super.dismiss();
    }
}
