package com.dts.dts.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.activities.MainActivity;
import com.dts.dts.activities.RequestInfoActivity;
import com.dts.dts.activities.SearchActivity;
import com.dts.dts.adapters.MapItemPageAdapter;
import com.dts.dts.models.Property;
import com.dts.dts.remote.APIRequestManager;
import com.dts.dts.utils.LocalPreferences;
import com.dts.dts.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener, MapItemPageAdapter.OnMapItemClickListener, APIRequestManager.APIRequestListener {
    public static final String TAG = MapFragment.class.getSimpleName();

    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;
    private List<Property> properties = new ArrayList<>();
    private ViewPager viewPager;
    private List<Marker> markers = new ArrayList<>();
    private HashMap<String, Bitmap> imageList = new HashMap<>();
    private HashMap<String, Bitmap> bImageList = new HashMap<>();



    private String[] location = new String[2];
    private APIRequestManager apiRequestManager;
    private MapItemPageAdapter pagerAdapter;
    private int likeItemPosition = -1;

    private View rootView;

    public String propertyJson;

    private PropertyFragment properyFragment;

    public MapFragment(PropertyFragment properyFragment){
        this.properyFragment = properyFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_map, container, false);


        apiRequestManager = new APIRequestManager(getContext(), this);
        initializeViews();
        setupMapItems();

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        initMapView();

        return rootView;
    }

    public void initMapView()
    {
        initializeMap();
        properties.clear();

        if (propertyJson != null) {
            Gson gson = new GsonBuilder().create();
//            properties = Arrays.asList(gson.fromJson(propertyJson, Property[].class));
            List<Property> temp = Arrays.asList(gson.fromJson(propertyJson, Property[].class));

            for (int i = 0; i < temp.size(); i++){
                Property property = temp.get(i);
                String latitude = property.getLatitude();
                String longi = property.getLongitude();
                if (latitude != null && longi != null){
                    properties.add(property);
                }
            }

            Property property = properties.get(0);
            location[0] = property.getLatitude();
            location[1] = property.getLongitude();
        }

        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        apiRequestManager.destroyProgressDialog();
    }


    private void loadImageList() {

        imageList.clear();
        bImageList.clear();
        markers.clear();

        for (int i = 0; i < properties.size(); i++) {
            final Property property = properties.get(i);
            final Integer propertyId = properties.get(i).getId();

            final MarkerOptions markerOptions = new MarkerOptions();
            final LatLng latLng = new LatLng(Double.parseDouble(property.getLatitude()), Double.parseDouble(property.getLongitude()));

            String price = "" + property.getPrice();
            String bed = "" + property.getBed();
            String subTitle = bed + "BR $" + price + "/" + property.getTerm();

            float priceNumber = property.getPrice();
            String shortPrice = Utils.suffixNumber(priceNumber);

            markerOptions.position(latLng);
            markerOptions.title(property.getId() + "");
            Bitmap bitmap = getImageFromCustomView(shortPrice, R.drawable.border_layout, property.getType());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

            markers.add(mGoogleMap.addMarker(markerOptions));

            Bitmap s_bitmap = getImageFromCustomView(shortPrice, R.drawable.sel_border_layout, property.getType());

            if (!imageList.containsKey(propertyId + ""))
            {
                if (s_bitmap != null) {

                    imageList.put(propertyId + "", bitmap);
                    bImageList.put(propertyId + "", s_bitmap);
                }
            }


//            App.volleyInstance.getImageLoader().get(properties.get(i).getImgUrl().getMd(), new ImageLoader.ImageListener() {
//                @Override
//                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                    if (!imageList.containsKey(propertyId + "")) {
//                        if (response.getBitmap() != null) {
//                            imageList.put(propertyId + "", response.getBitmap());
//                            bImageList.put(propertyId + "", addWhiteBorder(response.getBitmap(), 7));
//                        }
//
////                        if (imageList.size() == properties.size()) {
////                            addMarkers(properties);
////                        }
//                    }
//                }
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                }
//            });
        }
    }

    private void initializeViews() {
        rootView.findViewById(R.id.tab_search).setOnClickListener(this);
        rootView.findViewById(R.id.tab_listings).setOnClickListener(this);

//        setupMapItems();
    }

    private void setupMapItems() {

        viewPager = (ViewPager) rootView.findViewById(R.id.vp_map_item);

        pagerAdapter = new MapItemPageAdapter(getContext(), properties, this);
        viewPager.setAdapter(pagerAdapter);


        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //ActivitySwitcher.animationIn(findViewById(R.id.activity_map), getWindowManager());
    }

    public void updateProperties()
    {

    }
//    @Override
    public void onBackPressed() {

        getFragmentManager().popBackStack();
        properyFragment.mapFragment = null;
//        ((MainActivity) getActivity()).initMapFragment();
//        overridePendingTransition(R.anim.flip_in, R.anim.flip_out);
        //animatedStartActivity();
    }

//    private void animatedStartActivity() {
//        // we only animateOut this activity here.
//        // The new activity will animateIn from its onResume() - be sure to implement it.
//        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        LocalPreferences.saveFlagIsFromMapActivity(this, true);
//        // disable default animation for new intent
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        ActivitySwitcher.animationOut(findViewById(R.id.activity_map), getWindowManager(), new ActivitySwitcher.AnimationFinishedListener() {
//            @Override
//            public void onAnimationFinished() {
//                //startActivity(intent);
//                com.dts.dts.activities.MapActivity.this.finish();
//            }
//        });
//    }


    private Marker prevMarker;

    private void initializeMap() {
//        mMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        prevMarker = null;
        if (mGoogleMap != null){
            mGoogleMap.clear();
            mGoogleMap = null;
        }
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        //Log.d("test", "onMarkerClick: " + marker.getTitle());
                        setMarkerIcon(marker);

                        int selectedItem = 0;
                        for (Property property : properties) {
                            if (String.valueOf(property.getId()).equals(marker.getTitle())) {
                                selectedItem = properties.indexOf(property);
                                break;
                            }
                        }

                        viewPager.setCurrentItem(selectedItem);
                        viewPager.setVisibility(View.VISIBLE);
                        return false;
                    }
                });

                if (location != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(location[0]), Double.parseDouble(location[1])), 12));
                }
                loadImageList();
            }
        });


    }

    private void setMarkerIcon(Marker marker) {

//        for (int i = 0; i < properties.size(); i++) {
//                            float priceNumber = properties.get(i).getPrice();
//                            String shortPrice = suffixNumber(priceNumber);
//                            Bitmap bitmap = getImageFromCustomView(shortPrice, R.drawable.border_layout, properties.get(i).getType());
//
//                            Marker m = markers.get(i);
//                            m.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
//
//                        }
//


        marker.setInfoWindowAnchor(0, 30);
        if (prevMarker != null) {
            if (!prevMarker.equals(marker)) {
                String title = marker.getTitle();
                if (bImageList.containsKey(marker.getTitle()) && bImageList.containsKey(prevMarker.getTitle()))
                {
                    prevMarker.setIcon(BitmapDescriptorFactory.fromBitmap(imageList.get(prevMarker.getTitle())));
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(bImageList.get(marker.getTitle())));
                    prevMarker = marker;
                }
            }
        } else
        {
            prevMarker = marker;
            String title = marker.getTitle();
            if (bImageList.containsKey(marker.getTitle()))
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(bImageList.get(marker.getTitle())));
        }

    }


    private Bitmap getImageFromCustomView(String price, int backgroudId, String type)
    {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.circle_view, null);
        RelativeLayout bg_view = (RelativeLayout) view.findViewById(R.id.background_view);
        bg_view.setBackgroundResource(backgroudId);
        bg_view.setAlpha(0.5f);
        TextView priceView = (TextView) view.findViewById(R.id.price);
        priceView.setText(price);
        view.findViewById(R.id.sub_view).setVisibility(View.GONE);

        if (type.equalsIgnoreCase("SUBLET")){
            view.findViewById(R.id.sub_view).setVisibility(View.VISIBLE);
        }

        Bitmap viewCapture = null;


        view.setDrawingCacheEnabled(true);

// this is the important code :)
// Without it the view will have a dimension of 0,0 and the bitmap will be null
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false); // clear drawing cache


        return b;

    }

    synchronized private void addMarkers(List<Property> properties) {
        boolean addedNew = false;
        markers = new ArrayList<>();
        for (Property property : properties) {
            MarkerOptions markerOptions = new MarkerOptions();

            LatLng latLng = new LatLng(Double.parseDouble(property.getLatitude()), Double.parseDouble(property.getLongitude()));
            markerOptions.position(latLng);
            markerOptions.title(property.getId() + "");
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(imageList.get(property.getId() + "")));
            markers.add(mGoogleMap.addMarker(markerOptions));
            addedNew = true;
        }
        /*if (addedNew) {
            LatLngBounds bounds = builder.build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 57));

        }*/
    }

    private Bitmap addWhiteBorder(Bitmap bmp, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.RED);
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return bmpWithBorder;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_search:
                showSearchActivity();
                break;
            case R.id.tab_listings:
                onBackPressed();
                break;
        }
    }

    private void showSearchActivity() {
        ((MainActivity) getActivity()).showSearchActivity();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        markers.get(position).showInfoWindow();
        setMarkerIcon(markers.get(position));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(markers.get(position).getPosition()), 250, null);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onHeartClick(int position) {
        String token = LocalPreferences.getUserToken(getContext());
        if (token.isEmpty())
            showRequestInfoView(properties.get(position).getId());
        else {
            likeItemPosition = position;
            apiRequestManager.addToRequestQueue(apiRequestManager.likeProperty(myHandler, String.valueOf(properties.get(position).getId())), true);
        }
    }



    public boolean showOtherFragment = false;
    public PropertyDetailFragment propertyDetailFragment;

    @Override
    public void onImageClick(String json){
//        ((MainActivity) getActivity()).showPropertyDetail(json, R.id.activity_map, this);

        showOtherFragment = true;
        propertyDetailFragment = new PropertyDetailFragment();
        propertyDetailFragment.getDataFromIntent(json, "");

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        ft.add(R.id.activity_map, propertyDetailFragment);
        ft.addToBackStack(PropertyDetailFragment.TAG);
        ft.commit();

        ((MainActivity) getActivity()).logo_ditch.setVisibility(View.INVISIBLE);
        ((MainActivity) getActivity()).back_ditch.setVisibility(View.VISIBLE);
    }

    private void showRequestInfoView(Integer id) {
        Intent intent = new Intent(getActivity(), RequestInfoActivity.class);
        intent.putExtra(Constants.EXTRA_PROPERTY_ID, id + "");
        intent.putExtra(Constants.EXTRA_REQUEST_TYPE, 2);
        startActivity(intent);
    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            apiRequestManager.hideProgressDialog();
            int type = msg.arg1;

            JSONObject response = (JSONObject) msg.obj;
            switch (type)
            {
                case Constants.LIKE_PROPERTY:
                    if (pagerAdapter != null) {

                        JsonObject object = getJsonObjectFromJSON(response);
                        JsonElement data = object.get("data");

                        if (data == null || data.isJsonNull())
                            properties.get(likeItemPosition).setLiked(0);
                        else
                            properties.get(likeItemPosition).setLiked(1);
                        pagerAdapter.notifyDataSetChanged();
                    }
                    break;
            }


        }
    };

    public JsonObject getJsonObjectFromJSON(JSONObject data)
    {
        String jsonString = data.toString();
        Gson gson = new Gson();

        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        return jsonObject;
    }

    @Override
    public void onNetworkRequestSuccess(APIRequestManager.APIRequestType type, JsonObject response) {
        switch (type) {
            case LIKE_PROPERTY:
                if (pagerAdapter != null) {
                    JsonElement data = response.get("data");
                    if (data == null || data.isJsonNull())
                        properties.get(likeItemPosition).setLiked(0);
                    else
                        properties.get(likeItemPosition).setLiked(1);
                    pagerAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onNetworkRequestError(APIRequestManager.APIRequestType type, VolleyError error) {

    }
}
