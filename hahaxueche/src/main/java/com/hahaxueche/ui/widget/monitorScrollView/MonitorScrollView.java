package com.hahaxueche.ui.widget.monitorScrollView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by gibxin on 2016/2/13.
 */
public class MonitorScrollView extends ScrollView {
    private ScrollViewListener listener = null;
    public interface ScrollViewListener {
        void onScrollChanged(MonitorScrollView scrollView, int x, int y, int oldx, int oldy);
    }

    public MonitorScrollView(Context context) {
        super(context);
    }

    public MonitorScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (listener != null)
            listener.onScrollChanged(this,l,t,oldl,oldt);
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
