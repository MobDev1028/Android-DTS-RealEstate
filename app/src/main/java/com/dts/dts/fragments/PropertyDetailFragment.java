package com.dts.dts.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v4.app.FragmentManager;

import com.android.volley.VolleyError;
import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.activities.MainActivity;
import com.dts.dts.activities.RequestInfoActivity;
import com.dts.dts.activities.StreetViewActivity;
import com.dts.dts.adapters.CustomImagePageAdapter;
import com.dts.dts.animation.ProgressHUD;
import com.dts.dts.models.Img;
import com.dts.dts.models.Property;
import com.dts.dts.remote.APIRequestManager;
import com.dts.dts.utils.LocalPreferences;
import com.dts.dts.utils.Utils;
import com.dts.dts.views.CustomImageVIew;
import com.dts.dts.views.CustomScrollView;
import com.dts.dts.views.CustomViewPager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by silver on 2/9/17.
 */

public class PropertyDetailFragment extends Fragment implements StreetViewPanorama.OnStreetViewPanoramaChangeListener, StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener,
        StreetViewPanorama.OnStreetViewPanoramaClickListener, StreetViewPanorama.OnStreetViewPanoramaLongClickListener, OnStreetViewPanoramaReadyCallback,
        ViewPager.OnPageChangeListener, View.OnClickListener, APIRequestManager.APIRequestListener, SensorEventListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = PropertyDetailFragment.class.getSimpleName();

    private Property mProperty;
    private JsonObject mPropertyJson;
    private TextView imagesCountView;
    private String origin;
    private String json;
    private TextView distanceView;
    private APIRequestManager apiRequestManager;

    private boolean isFlick;
    private boolean isStreetView;
    StreetViewPanorama mStreetViewPanorama;
    GoogleMap mGoogleMap;
    private String googleMapsKey = "AIzaSyCOd0Y4CQdO05VWv6k3wZwZvq9RkfMlFgE";

    CustomScrollView scrollView;
    private GoogleApiClient mGoogleApiClient;

    public String headLabel;

    private Display mDisplay;
    private WindowManager mWindowManager;

    private View rootView;

    private SensorManager sensorMgr;
    Sensor mSensor;
    SatelliteFragment satelliteMap;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 0;
    private long FASTEST_INTERVAL = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_property_detail, container, false);
        apiRequestManager = new APIRequestManager(getContext(), this);

//        getDataFromIntent();

        initializeViews();
        getAddressFromCurrentLocation();

        mWindowManager = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();


//        LocalPreferences.saveFlick(getContext(), false);
//        LocalPreferences.saveStreetShowing(getContext(), false);

        sensorMgr = (SensorManager) getContext().getSystemService(SENSOR_SERVICE);
        mSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorMgr.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);

        return rootView;
    }

    public void initSatelliteMap(){
        if (satelliteMap == null){

            satelliteMap = new SatelliteFragment();

            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.screenViewMap, satelliteMap, "Satellite_Fragment");
            ft.addToBackStack("Satellite_Fragment");
            ft.commit();

            satelliteMap.setOnGetSatelliteFragmentListener(new SatelliteFragment.SatelliteListener() {
                @Override
                public void getSatelliteFragment(SupportStreetViewPanoramaFragment satelliteFragment) {
                    setupSatelliteMap(satelliteFragment);
                    isFlick = LocalPreferences.getIsFlick(getContext());
                    isStreetView = LocalPreferences.getIsStreet(getContext());


                    if (!isFlick) {
                        ((ImageView) rootView.findViewById(R.id.detailHand)).setImageResource(R.drawable.screw);
                        ((ImageView) rootView.findViewById(R.id.detailHand)).setBackgroundResource(R.drawable.map_control_border);

                    } else {
                        ((ImageView) rootView.findViewById(R.id.detailHand)).setImageResource(R.drawable.screw_off);
                        ((ImageView) rootView.findViewById(R.id.detailHand)).setBackgroundResource(R.drawable.map_sel_control_border);
                    }
                    if (!isStreetView){
                        showStreeView();
                    }
                    else
                        showMapView();
                }
            });
        }

    }
    @Override
    public void onResume(){
        super.onResume();

        initSatelliteMap();
    }

    public void destroySatelliteMap(){
        if (satelliteMap != null) {
            FragmentManager manager = ((Fragment) satelliteMap).getFragmentManager();
            FragmentTransaction trans = manager.beginTransaction();
            trans.remove((Fragment) satelliteMap);
            trans.commit();
            satelliteMap = null;
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        destroySatelliteMap();
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
                if (mStreetViewPanorama != null){
                    mStreetViewPanorama.animateTo(
                            new StreetViewPanoramaCamera.Builder()
                                    .bearing(mStreetViewPanorama.getPanoramaCamera().bearing - currentY)
                                    .zoom(mStreetViewPanorama.getPanoramaCamera().zoom)
                                    .build(), 0);
                }
                y = currentY;
            }
        }



    }




//    @Override
    public void onBackPressed()
    {
        getFragmentManager().popBackStack();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        apiRequestManager.destroyProgressDialog();
        sensorMgr.unregisterListener(this, mSensor);

    }

    @SuppressLint("DefaultLocale")
    private void initializeViews() {
//        self.locationLabel.text = [NSString stringWithFormat:@"%@, %@", [mPropertyData.city capitalizedString], mPropertyData.state_or_province];
        headLabel = mProperty.getCity() + " " + mProperty.getStateOrProvince();
        ((MainActivity) getActivity()).locationLabel.setText(headLabel);
        ((MainActivity) getActivity()).findViewById(R.id.btn_side_menu).setVisibility(View.INVISIBLE);

        imagesCountView = (TextView) rootView.findViewById(R.id.txt_count_images);
        TextView locationView = (TextView) rootView.findViewById(R.id.txt_location_name);
        final CustomViewPager vpImages = (CustomViewPager) rootView.findViewById(R.id.vp_img_property);
//        vpImages.setPagingEnabled(false);

        TextView propPrice = (TextView) rootView.findViewById(R.id.txt_property_price);
        TextView propAddress1 = (TextView) rootView.findViewById(R.id.txt_address_1);
        TextView propAddress2 = (TextView) rootView.findViewById(R.id.txt_address_2);
        distanceView = (TextView) rootView.findViewById(R.id.txt_distance);

//        satelliteMap = (SupportStreetViewPanoramaFragment) getChildFragmentManager().findFragmentById(R.id.mv_satelite);
//
//        setupSatelliteMap(satelliteMap);


        SupportMapFragment distMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mv_diatance);

        final SupportMapFragment generalMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mv_map);

        TextView bedCount = (TextView) rootView.findViewById(R.id.txt_count_bed);
        TextView bedLabel = (TextView) rootView.findViewById(R.id.txt_lbl_bed);
        TextView bathCount = (TextView) rootView.findViewById(R.id.txt_count_bath);
        TextView bathLabel = (TextView) rootView.findViewById(R.id.txt_lbl_bath);
        TextView lotSize = (TextView) rootView.findViewById(R.id.txt_lot_size);
        LinearLayout imagesLayout = (LinearLayout) rootView.findViewById(R.id.ly_prop_images);
        TextView propTitle = (TextView) rootView.findViewById(R.id.txt_prop_title);
        TextView propDesc = (TextView) rootView.findViewById(R.id.txt_prop_desc);

        TextView securityDeposite = (TextView) rootView.findViewById(R.id.txt_security_deposit);
        TextView moveInTotal = (TextView) rootView.findViewById(R.id.txt_move_in_total);



        ImageView backDitch = (ImageView) rootView.findViewById(R.id.img_logo_ditch);
        backDitch.setOnClickListener(this);

        LinearLayout highlightLayout = (LinearLayout) rootView.findViewById(R.id.ly_prop_highlights);
        LinearLayout amenityLayout = (LinearLayout) rootView.findViewById(R.id.ly_prop_amenities);

        TextView report = (TextView) rootView.findViewById(R.id.txt_report);
        report.setOnClickListener(this);

        rootView.findViewById(R.id.btn_request_info).setOnClickListener(this);
        rootView.findViewById(R.id.btn_hide_listing).setOnClickListener(this);
        rootView.findViewById(R.id.call_button).setOnClickListener(this);


        locationView.setText(WordUtils.capitalize(mProperty.getCity() + ", " + mProperty.getStateOrProvince()));

        imagesCountView.setText(String.format("1/%d", mProperty.getImgs().size()));

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float scWidth = outMetrics.widthPixels;

        scrollView = (CustomScrollView) rootView.findViewById(R.id.scroll_view);
        CustomImagePageAdapter customImagePageAdapter = new CustomImagePageAdapter(getContext(), mProperty.getImgs(), R.layout.pageritem_prop_detail_img, scWidth);
        vpImages.setAdapter(customImagePageAdapter);
        customImagePageAdapter.setOnHorizontalScrollEnableListener(new CustomImagePageAdapter.OnHorizontalScrollEnableListener() {
            @Override
            public void onHorizontalScrollEnable(boolean enable) {
                vpImages.setPagingEnabled(enable);
            }
        });

        customImagePageAdapter.setOnVerticalScrollEnableListener(new CustomImagePageAdapter.OnVerticalScrollEnableListener() {
            @Override
            public void onVerticalScrollEnable(boolean enable) {
                scrollView.setScrollingEnabled(enable);
            }
        });


        propPrice.setText("$" + Utils.suffixNumber(mProperty.getPrice()) + "/" + mProperty.getTerm());

        propAddress1.setText(WordUtils.capitalize(mProperty.getAddress1()));
//        propAddress2.setText(WordUtils.capitalize(mProperty.getAddress2()));
        propAddress2.setText(mProperty.getCity() + ", " + mProperty.getStateOrProvince() + ", " + mProperty.getZip());


//        if (!origin.isEmpty()) {
////            requestForDistance();
//        }else {
//            showLocationPopup();
//        }
        setupDistanceMap(distMap);
        setupCommonMap(generalMap);

        rootView.findViewById(R.id.changeMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMap();
            }
        });

        rootView.findViewById(R.id.detailHand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFlick) {
                    ((ImageView)rootView.findViewById(R.id.detailHand)).setImageResource(R.drawable.screw);
                    ((ImageView)rootView.findViewById(R.id.detailHand)).setBackgroundResource(R.drawable.map_control_border);

                    mStreetViewPanorama.setPanningGesturesEnabled(true);
                    mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);


                } else{
                    ((ImageView)rootView.findViewById(R.id.detailHand)).setImageResource(R.drawable.screw_off);
                    ((ImageView)rootView.findViewById(R.id.detailHand)).setBackgroundResource(R.drawable.map_sel_control_border);
                    mStreetViewPanorama.setPanningGesturesEnabled(false);
                    mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);


                }

                isFlick = !isFlick;
            }
        });

        rootView.findViewById(R.id.fullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullScreen();
            }
        });

        bedCount.setText(String.valueOf(mProperty.getBed()));
        bathCount.setText(String.valueOf(mProperty.getBath()));
        lotSize.setText(String.valueOf(Math.round(mProperty.getLotSize())));


        if (mProperty.getSecurity_deposit() != 0) {
//            int deposit = Integer.parseInt(""+mProperty.getSecurity_deposit());
            securityDeposite.setText("$"+ Math.round(mProperty.getSecurity_deposit()));
        } else {
            securityDeposite.setText("Free");
        }


        if (mProperty.getMove_in_cost() != null && mProperty.getMove_in_cost().length() > 0) {

            if (mProperty.getMove_in_cost().toLowerCase().equalsIgnoreCase("1st month only")) {
                moveInTotal.setText("$"+ Math.round(mProperty.getPrice()));
            } else if (mProperty.getMove_in_cost().toLowerCase().equalsIgnoreCase("1st month + sec deposit")) {
                int totalAmount = 0;
                if (mProperty.getPrice() != null) {
                    totalAmount += Math.round(mProperty.getPrice());
                }
                if (mProperty.getSecurity_deposit() > 0) {
                    totalAmount += mProperty.getSecurity_deposit();
                }
                moveInTotal.setText("$" + totalAmount);
            } else if (mProperty.getMove_in_cost().toLowerCase().equalsIgnoreCase("1st month + sec deposit + last month")) {
                int totalAmount = 0;
                if (mProperty.getPrice() != null) {
                    totalAmount = totalAmount + Math.round(mProperty.getPrice())*2;
                }
                if (mProperty.getSecurity_deposit() != null) {
                    totalAmount += Math.round(mProperty.getSecurity_deposit());
                }
                moveInTotal.setText("$"+ totalAmount);
            }
        } else {
            moveInTotal.setText("Free");
        }




        if (mProperty.getBed() > 1) {
            bedLabel.setText("Bed Rooms");
        } else {
            bedLabel.setText("Bed Room");
        }

        if (mProperty.getBath() > 1) {
            bathLabel.setText("Bath Rooms");
        } else {
            bathLabel.setText("Bath Room");
        }

        vpImages.addOnPageChangeListener(this);

        setupImagesLayout(imagesLayout);


        propTitle.setText(mProperty.getTitle());
        propDesc.setText(mProperty.getDescription());

        String[] hTexts = getResources().getStringArray(R.array.text_build_amenities);
        String[] hKeys = getResources().getStringArray(R.array.key_build_amenities);
        setupAmenitiesLayout(highlightLayout, hTexts, hKeys);

        String[] amTexts = getResources().getStringArray(R.array.text_unit_amenities);
        String[] amKeys = getResources().getStringArray(R.array.key_unit_amenities);
        setupAmenitiesLayout(amenityLayout, amTexts, amKeys);

        showStreeView();

        buildGoogleApiClient();
        mGoogleApiClient.connect();
        if (!checkLocationServices()) {
            showLocationErrorAlert();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    private void getAddressFromCurrentLocation()
    {
        if (origin == null || origin.isEmpty())
            return;
        String[] location = origin.split(",");
        Geocoder gCoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gCoder.getFromLocation(Double.parseDouble(location[0]), Double.parseDouble(location[1]), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses == null)
            return;

//        String currentAddress = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
        String currentAddress = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getAdminArea();

        getDriveDuration(currentAddress, mProperty.getAddress());
    }

    private String getFormattedAddress(String address) {
        String addressToReturn = address.replace(", ", ",").replace(" ", "+");
        return addressToReturn;
    }
    private void getDriveDuration(String currentAddress, String destinationAddress)
    {
        String formattedCurrentAddress = getFormattedAddress(currentAddress);
        String formattedDesitationAddress = getFormattedAddress(destinationAddress);
        requestForDistance(formattedCurrentAddress, formattedDesitationAddress);
    }
    private void showStreeView() {
        rootView.findViewById(R.id.screenViewMap).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.generalMapLayout).setVisibility(View.GONE);
        ((ImageView)rootView.findViewById(R.id.changeMap)).setImageResource(R.drawable.map_filled);
    }

    private void showMapView() {
        rootView.findViewById(R.id.screenViewMap).setVisibility(View.GONE);
        rootView.findViewById(R.id.generalMapLayout).setVisibility(View.VISIBLE);
        ((ImageView)rootView.findViewById(R.id.changeMap)).setImageResource(R.drawable.street);
    }


    public void changeMap() {
        if (isStreetView) {

            showStreeView();
        } else{
            showMapView();
        }

        isStreetView = !isStreetView;

    }
    private void showLocationPopup() {
        /*final CustomDialog dialog = new CustomDialog(this, "Attention!"
                , "Need your current location to calculate distance.", "OK", ""
                , CustomDialog.DialogType.DEFAULT_ERROR);
        dialog.showDialog();*/
        Toast toast = Toast.makeText(getActivity(), "Need your current location to calculate distance.", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void requestForDistance(String cur, String dist) {
        String destination = mProperty.getLatitude() + "," + mProperty.getLongitude();
        //String destination = "31.543263,74.403534";
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="
                + cur + "&destinations=" + dist + "&key=" + googleMapsKey;

//        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=Washington,DC&destinations=New+York+City,NY&key="
//                + googleMapsKey;

//        String url = "https://maps.googleapis.com/maps/api/directions/json?units=imperial&origin="
//                + origin + "&destination=" + destination + "&sensor=false";

        //Log.d(TAG, "requestForDistance: origin-" + origin + " destination-" + destination + " url-" + url);

        OkHttpClient client = new OkHttpClient();

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                distanceView.setText((String)msg.obj);
            }
        };

        final Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                String json = response.body().string();
                //Log.d(TAG, "onResponse: " + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String status = jsonObject.getString("status");

                    if (status.equals("OK")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        if (jsonArray.length() > 0)
                        {
                            JSONObject elementDic = jsonArray.getJSONObject(0);
                            if (elementDic != null) {
                                JSONArray elements = elementDic.getJSONArray("elements");
                                if (elements != null && elements.length() > 0) {
                                    JSONObject geoElement = elements.getJSONObject(0);
                                    if (geoElement != null) {
                                        String s = geoElement.getString("status");
                                        if (s != null) {
                                            if (s.equalsIgnoreCase("OK")) {
                                                JSONObject duration = geoElement.getJSONObject("duration");
                                                if (duration != null)
                                                {
                                                    String driveDuration = duration.getString("text");
                                                    Message msg = new Message();
                                                    msg.obj = driveDuration;
                                                    handler.sendMessage(msg);

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
//                        String text = jsonArray.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
//                        handler.sendEmptyMessage(Integer.parseInt(text.split(" ")[0]));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupSatelliteMap(SupportStreetViewPanoramaFragment satelliteMap) {
        satelliteMap.getStreetViewPanoramaAsync(this);
    }

    private void setupDistanceMap(SupportMapFragment distMap) {
        distMap.getView().setBackgroundColor(Color.WHITE);
        ViewGroup.LayoutParams params = distMap.getView().getLayoutParams();
        Drawable overlay = ContextCompat.getDrawable(getContext(), R.drawable.prop_detail_overlay_distance);
        //params = new RelativeLayout.LayoutParams(overlay.getIntrinsicWidth(), overlay.getIntrinsicHeight());
        params.height = overlay.getIntrinsicHeight();
        //distMap.getView().setLayoutParams(params);
        distMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mProperty.getLatitude()), Double.parseDouble(mProperty.getLongitude())), 14));
            }
        });
    }

    private void setupCommonMap(SupportMapFragment map) {
        map.getView().setBackgroundColor(Color.WHITE);
        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mProperty.getLatitude()), Double.parseDouble(mProperty.getLongitude())), 14));
            }
        });
    }
    private void setupAmenitiesLayout(LinearLayout layout, String[] texts, String[] keys) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (int i = 0; i < texts.length; i++) {
            if (mPropertyJson.has(keys[i]) && mPropertyJson.get(keys[i]).getAsInt() == 1) {
                TextView textView = (TextView) inflater.inflate(R.layout.listitem_pd_amenities, null);
                textView.setText(texts[i]);
                layout.addView(textView);
            }
        }
    }

    private void setupImagesLayout(LinearLayout imagesLayout) {
        List<Img> images = mProperty.getImgs();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < images.size(); i++) {
            View view = inflater.inflate(R.layout.listitem_img_prop_detail, null);
            CustomImageVIew imageView = (CustomImageVIew) view.findViewById(R.id.img_prop_detail_image);
            Picasso.with(getContext()).load(images.get(i).getImgUrl().getMd()).placeholder(R.drawable.image_placeholder).into(imageView);
            imagesLayout.addView(view);

            imageView.setOnVerticalScrollEnableListener(new CustomImageVIew.OnVerticalScrollEnableListener() {
                @Override
                public void onVerticalScrollEnable(boolean enable) {
                    scrollView.setScrollingEnabled(enable);
                }
            });
        }
    }

    public void getDataFromIntent(String json, String origin) {
        this.origin = origin;
//        isFlick = false;
//        isStreetView = false;

        if (json != null) {
            Gson gson = new GsonBuilder().create();
            mProperty = gson.fromJson(json, Property.class);
            mPropertyJson = gson.fromJson(json, JsonObject.class);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        imagesCountView.setText(position + 1 + "/" + mProperty.getImgs().size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void showRequestInfoView(String id) {
        Intent intent = new Intent(getActivity(), RequestInfoActivity.class);
        intent.putExtra(Constants.EXTRA_PROPERTY_ID, id);
        intent.putExtra(Constants.EXTRA_REQUEST_TYPE, 0);
        startActivity(intent);
    }


    public static long lastClickTime = 0;
    public static final long DOUBLE_CLICK_TIME_DELTA = 500;

    public static boolean isDoubleClick(){
        long clickTime = System.currentTimeMillis();
        if(clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
            lastClickTime = clickTime;
            return true;
        }
        lastClickTime = clickTime;
        return false;
    }

    @Override
    public void onClick(View view) {
        if (isDoubleClick())
            return;

        String token = LocalPreferences.getUserToken(getContext());

        switch (view.getId()) {
            case R.id.btn_request_info:

                if (token.isEmpty())
                    showRequestInfoView(String.valueOf(mProperty.getId()));
                else
                    apiRequestManager.addToRequestQueue(apiRequestManager.inquireProperty(myHandler, String.valueOf(mProperty.getId())), true);
                break;
            case R.id.btn_hide_listing:
                if (token.isEmpty())
                    showRequestInfoView(String.valueOf(mProperty.getId()));
                else
                    apiRequestManager.addToRequestQueue(apiRequestManager.hideProperty(myHandler, String.valueOf(mProperty.getId())), true);
                break;
            case R.id.txt_report:
                hud = ProgressHUD.show(getContext(), "Reporting...", true, true, null);
                apiRequestManager.addToRequestQueue(apiRequestManager.reportProperty(myHandler, String.valueOf(mProperty.getId())), false);
                break;
            case R.id.call_button:
                if (mProperty.getAuthorUserInfo().getCid() != null && mProperty.getAuthorUserInfo().getCid().length() > 0) {
                    String url = "tel://" + mProperty.getAuthorUserInfo().getCid();

                    final PackageManager packageManager = getContext().getPackageManager();
                    final Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
                    List<ResolveInfo> list = packageManager.queryIntentActivities( intent, PackageManager.MATCH_DEFAULT_ONLY );
                    Boolean canOpen = list.size() > 0;
                    if (canOpen){
                        startActivity(intent);
                    } else {
                        hud = ProgressHUD.show(getContext(), "This device is not confgured to make call.", true, true, null);
                        progressHandler.postDelayed(progressRunnable, 2000);
                    }
                }

                break;
            case R.id.img_logo_ditch:
//                finish();
                break;
        }
    }

    private ProgressHUD hud;
    Handler progressHandler = new Handler();
    Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            hud.dismiss();
        }
    };

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            apiRequestManager.hideProgressDialog();
            int type = msg.arg1;
            String message = null;
            boolean success = false;
            JSONObject response = (JSONObject) msg.obj;

            switch (type)
            {
                case Constants.INQUIRE_PROPERTY:
                    try {
                        message = response.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (message != null && !message.isEmpty())
                        apiRequestManager.showDefaultPopup("", message);
                    break;

                case Constants.HIDE_PROPERTY:
                    try {
                        message = response.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (message != null && !message.isEmpty())
                        apiRequestManager.showDefaultPopup("", message);
                    break;

                case Constants.CREATE_SUPPORT_TICKET:

                    try {
                        message = response.getString("message");
                        success = response.getBoolean("success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    if (message != null && !message.isEmpty()) {
                    if (!success)
                    {

                        hud.setHudTitle("Failed to report");
                        hud.setMessage(message, false);
                        progressHandler.postDelayed(progressRunnable, 2000);
//                        apiRequestManager.showDefaultPopup("", message);
                    }
                    else{
                        hud.setMessage("Reported", false);
                        progressHandler.postDelayed(progressRunnable, 2000);

                    }
            }


        }
    };

    @Override
    public void onNetworkRequestSuccess(APIRequestManager.APIRequestType type, JsonObject response) {
        switch (type) {
            case INQUIRE_PROPERTY:
                JsonElement message = response.get("message");
                if (message != null && !message.isJsonNull() && !message.getAsString().isEmpty())
                    apiRequestManager.showDefaultPopup("", message.getAsString());
                break;
        }
    }

    @Override
    public void onNetworkRequestError(APIRequestManager.APIRequestType type, VolleyError error) {
        apiRequestManager.showDefaultPopup("Error", error.getMessage());
    }

    public void fullScreen()
    {

        rootView.findViewById(R.id.screenViewMap).setVisibility(View.GONE);

        Intent intent = new Intent(getActivity(), StreetViewActivity.class);
        intent.putExtra(Constants.EXTRA_PROPERTY_JSON, mPropertyJson.toString());
        intent.putExtra("flick", isFlick);
        intent.putExtra("street", isStreetView);
        intent.putExtra("origin", this.origin);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


    }

    @Override
    public void onLocationChanged(Location location) {

        origin = location.getLatitude() + "," + location.getLongitude();

        getAddressFromCurrentLocation();

        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public boolean checkLocationServices() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void showLocationErrorAlert() {
//        if (locationDialog == null)
//            locationDialog = new AlertDialog.Builder(getContext()).setTitle("Location Error")
//                    .setMessage("Unable to get location. Please turn on location services and try again.")
//                    .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                            dialog.dismiss();
//                        }
//                    }).create();
//        if (!locationDialog.isShowing())
//            locationDialog.show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
