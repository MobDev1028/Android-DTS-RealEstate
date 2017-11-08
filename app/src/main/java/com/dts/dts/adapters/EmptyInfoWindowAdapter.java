package com.dts.dts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.dts.dts.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Android Dev E5550 on 11/28/2016.
 */

public class EmptyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context mContext;

    public EmptyInfoWindowAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        //return LayoutInflater.from(mContext).inflate(R.layout.layout_empty_info_window, null);
        return new View(mContext);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

}
