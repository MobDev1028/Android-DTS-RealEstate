package com.dts.dts.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.models.Property;
import com.dts.dts.remote.APIRequestManager;
import com.dts.dts.utils.LocalPreferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by silver on 12/20/16.
 */

public class StreetViewActivity extends AppCompatActivity implements StreetViewPanorama.OnStreetViewPanoramaChangeListener, StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener,
        StreetViewPanorama.OnStreetViewPanoramaClickListener, StreetViewPanorama.OnStreetViewPanoramaLongClickListener, OnStreetViewPanoramaReadyCallback, SensorEventListener {
    private boolean isFlick;
    private boolean isStreetView;
    StreetViewPanorama mStreetViewPanorama;
    GoogleMap mGoogleMap;
    private Property mProperty;
    private String origin;
    private String gJson;
    private SensorManager sensorMgr;
    Sensor mSensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.street_view);
        getDataFromIntent();

        StreetViewPanoramaFragment satelliteMap = (StreetViewPanoramaFragment) getFragmentManager()
                .findFragmentById(R.id.mv_satelite);
        final SupportMapFragment generalMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mv_map);

        setupSatelliteMap(satelliteMap);
        setupCommonMap(generalMap);
        findViewById(R.id.changeMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMap();
            }
        });

        findViewById(R.id.detailHand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFlick = !isFlick;
                LocalPreferences.saveFlick(StreetViewActivity.this, isFlick);
                setFlickState();
            }
        });

        findViewById(R.id.fullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalPreferences.saveFlick(StreetViewActivity.this, isFlick);
                LocalPreferences.saveStreetShowing(StreetViewActivity.this, isStreetView);

                finish();
            }
        });

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorMgr.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);

    }

//    @Override
//    public void onResume(){
//        super.onResume();
//        sensorMgr.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
//
//    }

//    @Override
//    public void onDestroy()
//    {
//        super.onDestroy();
//        sensorMgr.unregisterListener(this, mSensor);
//    }

    @Override
    public void onBackPressed(){

    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
        mStreetViewPanorama = panorama;
        double lat = Double.parseDouble(mProperty.getLatitude());
        double longi = Double.parseDouble(mProperty.getLongitude());
        panorama.setPosition(new LatLng(lat, longi));

        panorama.setPanningGesturesEnabled(true);
        panorama.setUserNavigationEnabled(true);
        panorama.setZoomGesturesEnabled(true);
        panorama.setStreetNamesEnabled(true);

        setFlickState();
        setStreetMode();

    }

    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation location) {
        if (location != null) {
//            mMarker.setPosition(location.position);
        }
    }

    @Override
    public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera camera) {
//        mPanoCameraChangeTextView.setText("Times camera changed=" + ++mPanoCameraChangeTimes);
    }

    @Override
    public void onStreetViewPanoramaClick(StreetViewPanoramaOrientation orientation) {
        Point point = mStreetViewPanorama.orientationToPoint(orientation);
        if (point != null) {
//            mPanoClickTimes++;
//            mPanoClickTextView.setText(
//                    "Times clicked=" + mPanoClickTimes + " : " + point.toString());
            mStreetViewPanorama.animateTo(
                    new StreetViewPanoramaCamera.Builder()
                            .orientation(orientation)
                            .zoom(mStreetViewPanorama.getPanoramaCamera().zoom)
                            .build(), 1000);
        }
    }
    @Override
    public void onStreetViewPanoramaLongClick(StreetViewPanoramaOrientation orientation) {
        Point point = mStreetViewPanorama.orientationToPoint(orientation);
        if (point != null) {

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    public float delta = 0;
    public float y = 0;

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() != Sensor.TYPE_GYROSCOPE)
            return;

        float currentY = event.values[1];
        delta = currentY - y;
        if (Math.abs(delta) >= 0.01) {
            if (isFlick) {
                if (mStreetViewPanorama != null) {
                    mStreetViewPanorama.animateTo(
                            new StreetViewPanoramaCamera.Builder()
                                    .bearing(mStreetViewPanorama.getPanoramaCamera().bearing - currentY)
                                    .zoom(mStreetViewPanorama.getPanoramaCamera().zoom)
                                    .build(), 0);
                }
            }
            y = currentY;
        }
    }

    private void getDataFromIntent() {
        String json = getIntent().getStringExtra(Constants.EXTRA_PROPERTY_JSON);
        gJson = json;
        isFlick = getIntent().getBooleanExtra("flick", false);
        isStreetView = getIntent().getBooleanExtra("street", false);

        origin = getIntent().getStringExtra("origin");
        //Log.d(TAG, "getDataFromIntent: " + json);

        if (json != null) {
            Gson gson = new GsonBuilder().create();
            mProperty = gson.fromJson(json, Property.class);
        }
    }

    public void setFlickState()
    {
        if (!isFlick) {
            ((ImageView)findViewById(R.id.detailHand)).setImageResource(R.drawable.screw);
            ((ImageView)findViewById(R.id.detailHand)).setBackgroundResource(R.drawable.map_control_border);
            if (mStreetViewPanorama!=null)
                mStreetViewPanorama.setPanningGesturesEnabled(true);
            if (mGoogleMap!= null)
                mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
        } else{
            ((ImageView)findViewById(R.id.detailHand)).setImageResource(R.drawable.screw_off);
            ((ImageView)findViewById(R.id.detailHand)).setBackgroundResource(R.drawable.map_sel_control_border);

            if (mStreetViewPanorama!=null) {
                mStreetViewPanorama.setPanningGesturesEnabled(false);

                mStreetViewPanorama.animateTo(
                        new StreetViewPanoramaCamera.Builder().zoom(
                                mStreetViewPanorama.getPanoramaCamera().zoom)
                                .tilt(mStreetViewPanorama.getPanoramaCamera().tilt)
                                .bearing(mStreetViewPanorama.getPanoramaCamera().bearing)
                                .build(), 0);
            }
            if (mGoogleMap!= null)
                mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);

        }
    }

    public void setStreetMode()
    {
        if (!isStreetView) {

            showStreeView();
        } else{
            showMapView();
        }
    }

    private void showStreeView() {
        findViewById(R.id.screenViewMap).setVisibility(View.VISIBLE);
        findViewById(R.id.generalMapLayout).setVisibility(View.GONE);
        ((ImageView)findViewById(R.id.changeMap)).setImageResource(R.drawable.map_filled);
    }

    private void showMapView() {
        findViewById(R.id.screenViewMap).setVisibility(View.GONE);
        findViewById(R.id.generalMapLayout).setVisibility(View.VISIBLE);
        ((ImageView)findViewById(R.id.changeMap)).setImageResource(R.drawable.street);
    }


    public void changeMap() {

        isStreetView = !isStreetView;
        setStreetMode();

        LocalPreferences.saveStreetShowing(StreetViewActivity.this, isStreetView);

    }

    private void setupCommonMap(SupportMapFragment map) {
        map.getView().setBackgroundColor(Color.WHITE);
        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mProperty.getLatitude()), Double.parseDouble(mProperty.getLongitude())), 14));
                setFlickState();
                setStreetMode();
            }
        });
    }

    private void setupSatelliteMap(StreetViewPanoramaFragment satelliteMap) {
        satelliteMap.getStreetViewPanoramaAsync(this);
    }

}
