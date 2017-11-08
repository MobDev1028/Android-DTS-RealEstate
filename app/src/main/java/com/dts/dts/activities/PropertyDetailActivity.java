package com.dts.dts.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.adapters.CustomImagePageAdapter;
import com.dts.dts.models.Img;
import com.dts.dts.models.Property;
import com.dts.dts.remote.APIRequestManager;
import com.dts.dts.utils.LocalPreferences;
import com.dts.dts.utils.Utils;
import com.dts.dts.views.CustomImageVIew;
import com.dts.dts.views.CustomScrollView;
import com.dts.dts.views.CustomViewPager;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PropertyDetailActivity extends AppCompatActivity implements StreetViewPanorama.OnStreetViewPanoramaChangeListener, StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener,
        StreetViewPanorama.OnStreetViewPanoramaClickListener, StreetViewPanorama.OnStreetViewPanoramaLongClickListener, OnStreetViewPanoramaReadyCallback,
        ViewPager.OnPageChangeListener, View.OnClickListener, APIRequestManager.APIRequestListener {

    private static final String TAG = PropertyDetailActivity.class.getSimpleName();
    private Property mProperty;
    private JsonObject mPropertyJson;
    private TextView imagesCountView;
    private String origin;
    private TextView distanceView;
    private APIRequestManager apiRequestManager;

    private boolean isFlick;
    private boolean isStreetView;
    StreetViewPanorama mStreetViewPanorama;
    GoogleMap mGoogleMap;
    private String googleMapsKey = "AIzaSyCOd0Y4CQdO05VWv6k3wZwZvq9RkfMlFgE";

    CustomScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        apiRequestManager = new APIRequestManager(this, this);

        getDataFromIntent();

        initializeViews();
        getAddressFromCurrentLocation();
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
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        apiRequestManager.destroyProgressDialog();
    }

        @SuppressLint("DefaultLocale")
    private void initializeViews() {
        imagesCountView = (TextView) findViewById(R.id.txt_count_images);
        TextView locationView = (TextView) findViewById(R.id.txt_location_name);
        final CustomViewPager vpImages = (CustomViewPager) findViewById(R.id.vp_img_property);
//        vpImages.setPagingEnabled(false);

        TextView propPrice = (TextView) findViewById(R.id.txt_property_price);
        TextView propAddress1 = (TextView) findViewById(R.id.txt_address_1);
        TextView propAddress2 = (TextView) findViewById(R.id.txt_address_2);
        distanceView = (TextView) findViewById(R.id.txt_distance);





        SupportMapFragment distMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mv_diatance);


            StreetViewPanoramaFragment satelliteMap = (StreetViewPanoramaFragment) getFragmentManager()
                    .findFragmentById(R.id.mv_satelite);
            final SupportMapFragment generalMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mv_map);





            TextView bedCount = (TextView) findViewById(R.id.txt_count_bed);
        TextView bedLabel = (TextView) findViewById(R.id.txt_lbl_bed);
        TextView bathCount = (TextView) findViewById(R.id.txt_count_bath);
        TextView bathLabel = (TextView) findViewById(R.id.txt_lbl_bath);
        TextView lotSize = (TextView) findViewById(R.id.txt_lot_size);
        LinearLayout imagesLayout = (LinearLayout) findViewById(R.id.ly_prop_images);
        TextView propTitle = (TextView) findViewById(R.id.txt_prop_title);
        TextView propDesc = (TextView) findViewById(R.id.txt_prop_desc);

            TextView securityDeposite = (TextView) findViewById(R.id.txt_security_deposit);
            TextView moveInTotal = (TextView) findViewById(R.id.txt_move_in_total);



            ImageView backDitch = (ImageView) findViewById(R.id.img_logo_ditch);
            backDitch.setOnClickListener(this);

        LinearLayout highlightLayout = (LinearLayout) findViewById(R.id.ly_prop_highlights);
        LinearLayout amenityLayout = (LinearLayout) findViewById(R.id.ly_prop_amenities);

        TextView report = (TextView) findViewById(R.id.txt_report);
        report.setOnClickListener(this);

            findViewById(R.id.btn_request_info).setOnClickListener(this);
            findViewById(R.id.btn_hide_listing).setOnClickListener(this);

        locationView.setText(WordUtils.capitalize(mProperty.getCity() + ", " + mProperty.getStateOrProvince()));

        imagesCountView.setText(String.format("1/%d", mProperty.getImgs().size()));

            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);

            float scWidth = outMetrics.widthPixels;

            scrollView = (CustomScrollView) findViewById(R.id.scroll_view);
            CustomImagePageAdapter customImagePageAdapter = new CustomImagePageAdapter(this, mProperty.getImgs(), R.layout.pageritem_prop_detail_img, scWidth);
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

            propPrice.setText("$" + Utils.suffixNumber(mProperty.getPrice()) + "/month");

        propAddress1.setText(WordUtils.capitalize(mProperty.getAddress1()));
//        propAddress2.setText(WordUtils.capitalize(mProperty.getAddress2()));
        propAddress2.setText(mProperty.getCity() + ", " + mProperty.getStateOrProvince() + ", " + mProperty.getZip());


            if (!origin.isEmpty()) {
//            requestForDistance();
            }else {
            showLocationPopup();
        }
        setupDistanceMap(distMap);
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
                    if (isFlick) {
                        ((ImageView)findViewById(R.id.detailHand)).setImageResource(R.drawable.screw);
                        mStreetViewPanorama.setPanningGesturesEnabled(true);
                        mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
                    } else{
                        ((ImageView)findViewById(R.id.detailHand)).setImageResource(R.drawable.screw_off);
                        mStreetViewPanorama.setPanningGesturesEnabled(false);
                        mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);

//                        self.mapView.userInteractionEnabled = false
//                        if orientation != nil {
//                            self.streetView.animateToCamera(GMSPanoramaCamera(orientation: orientation, zoom: 1.0), animationDuration: 0)
//                        }

                        mStreetViewPanorama.animateTo(
                                new StreetViewPanoramaCamera.Builder().zoom(
                                        mStreetViewPanorama.getPanoramaCamera().zoom)
                                        .tilt(mStreetViewPanorama.getPanoramaCamera().tilt)
                                        .bearing(mStreetViewPanorama.getPanoramaCamera().bearing)
                                        .build(), 0);
                    }

                    isFlick = !isFlick;
                }
            });

            findViewById(R.id.fullscreen).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fullScreen();
                }
            });

        bedCount.setText(String.valueOf(mProperty.getBed()));
        bathCount.setText(String.valueOf(mProperty.getBath()));
        lotSize.setText(String.valueOf(mProperty.getLotSize()));


            if (mProperty.getSecurity_deposit() != 0) {
                securityDeposite.setText("$"+ mProperty.getSecurity_deposit());
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
                moveInTotal.setText("$0");
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

        setupSatelliteMap(satelliteMap);

        propTitle.setText(mProperty.getTitle());
        propDesc.setText(mProperty.getDescription());

        String[] hTexts = getResources().getStringArray(R.array.text_build_amenities);
        String[] hKeys = getResources().getStringArray(R.array.key_build_amenities);
        setupAmenitiesLayout(highlightLayout, hTexts, hKeys);

        String[] amTexts = getResources().getStringArray(R.array.text_unit_amenities);
        String[] amKeys = getResources().getStringArray(R.array.key_unit_amenities);
        setupAmenitiesLayout(amenityLayout, amTexts, amKeys);

            showStreeView();
    }


    private void getAddressFromCurrentLocation()
    {
        if (origin == null || origin.isEmpty())
            return;
        String[] location = origin.split(",");
        Geocoder gCoder = new Geocoder(this, Locale.getDefault());
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
        findViewById(R.id.screenViewMap).setVisibility(View.VISIBLE);
        findViewById(R.id.generalMapLayout).setVisibility(View.GONE);
        ((ImageView)findViewById(R.id.changeMap)).setImageResource(R.drawable.ic_prop_detail_map);
    }

    private void showMapView() {
        findViewById(R.id.screenViewMap).setVisibility(View.GONE);
        findViewById(R.id.generalMapLayout).setVisibility(View.VISIBLE);
        ((ImageView)findViewById(R.id.changeMap)).setImageResource(R.drawable.ico_streetview_streetmap);
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
        Toast toast = Toast.makeText(this, "Need your current location to calculate distance.", Toast.LENGTH_LONG);
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

    private void setupSatelliteMap(StreetViewPanoramaFragment satelliteMap) {
        satelliteMap.getStreetViewPanoramaAsync(this);
    }

    private void setupDistanceMap(SupportMapFragment distMap) {
        distMap.getView().setBackgroundColor(Color.WHITE);
        ViewGroup.LayoutParams params = distMap.getView().getLayoutParams();
        Drawable overlay = ContextCompat.getDrawable(this, R.drawable.prop_detail_overlay_distance);
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
        LayoutInflater inflater = LayoutInflater.from(this);

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
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < images.size(); i++) {
            View view = inflater.inflate(R.layout.listitem_img_prop_detail, null);
            CustomImageVIew imageView = (CustomImageVIew) view.findViewById(R.id.img_prop_detail_image);
            Picasso.with(this).load(images.get(i).getImgUrl().getMd()).into(imageView);
            imagesLayout.addView(view);

            imageView.setOnVerticalScrollEnableListener(new CustomImageVIew.OnVerticalScrollEnableListener() {
                @Override
                public void onVerticalScrollEnable(boolean enable) {
                    scrollView.setScrollingEnabled(enable);
                }
            });
        }
    }

    private void getDataFromIntent() {
        String json = getIntent().getStringExtra(Constants.EXTRA_PROPERTY_JSON);
        origin = getIntent().getStringExtra(Constants.EXTRA_CURRENT_LOCATION);
        isFlick = getIntent().getBooleanExtra("flick", false);
        isStreetView = getIntent().getBooleanExtra("street", false);
        //Log.d(TAG, "getDataFromIntent: " + json);

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
        Intent intent = new Intent(this, RequestInfoActivity.class);
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

        String token = LocalPreferences.getUserToken(this);

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
                apiRequestManager.addToRequestQueue(apiRequestManager.reportProperty(myHandler, String.valueOf(mProperty.getId())), true);
                break;
            case R.id.img_logo_ditch:
                finish();
                break;
        }
    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            apiRequestManager.hideProgressDialog();
            int type = msg.arg1;
            String message = null;

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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (message != null && !message.isEmpty())
                        apiRequestManager.showDefaultPopup("", message);

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
        Intent intent = new Intent(this, StreetViewActivity.class);
        intent.putExtra(Constants.EXTRA_PROPERTY_JSON, mPropertyJson.toString());
        intent.putExtra("flick", isFlick);
        intent.putExtra("street", isStreetView);
        startActivity(intent);
//        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
