package com.dts.dts.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dts.dts.R;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;

/**
 * Created by silver on 2/24/17.
 */

public class SatelliteFragment extends Fragment {
    public SatelliteFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_satellite, container, false);
        SupportStreetViewPanoramaFragment satelliteFragment = (SupportStreetViewPanoramaFragment)getChildFragmentManager().findFragmentById(R.id.mv_satelite);
        mListener.getSatelliteFragment(satelliteFragment);
        return view;
    }

    private SatelliteListener mListener;
    public void setOnGetSatelliteFragmentListener(SatelliteListener listener)
    {
        mListener = listener;
    }

    public interface SatelliteListener{
        void getSatelliteFragment(SupportStreetViewPanoramaFragment satelliteFragment);
    }
}
