package com.hahaxueche.ui.widget.recyclerView;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by wangshirui on 2017/1/10.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpace = 0;

    public SpaceItemDecoration(int space) {
        mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if(parent.getChildPosition(view) != 0)
            outRect.top = mSpace;
    }

}
