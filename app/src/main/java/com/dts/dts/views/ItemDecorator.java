package com.dts.dts.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ItemDecorator extends RecyclerView.ItemDecoration {
    private final int mSpace;

    public ItemDecorator(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position != 0)
            outRect.bottom = mSpace;
    }
}