package com.dts.dts.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Android Dev E5550 on 11/29/2016.
 */

public class LinearLayoutManager extends android.support.v7.widget.LinearLayoutManager {
    public LinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
