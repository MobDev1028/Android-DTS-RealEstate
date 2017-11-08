package com.dts.dts.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.activities.MainActivity;
import com.dts.dts.adapters.PropertyRecycleAdapter;
import com.dts.dts.models.APIPropertyList;
import com.dts.dts.models.ApiUserSearch;
import com.dts.dts.models.Detail;
import com.dts.dts.models.Property;
import com.dts.dts.models.PropertyMetaData;
import com.dts.dts.remote.APIRequestManager;
import com.dts.dts.utils.LocalPreferences;
import com.dts.dts.views.ItemDecorator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PropertyFragment extends Fragment implements View.OnClickListener, APIRequestManager.APIRequestListener, PropertyRecycleAdapter.OnItemClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = PropertyFragment.class.getSimpleName();

    private Context mContext;
    private RecyclerView rv_property;
    private PropertyRecycleAdapter propertyAdapter;


    private APIRequestManager apiRequestManager;
    private List<Property> properties = new ArrayList<>();

    private SwipyRefreshLayout refeshListView;
    private int itemHeight;
    private int itemWidth;

    public static long lastClickTime = 0;
    public static final long DOUBLE_CLICK_TIME_DELTA = 500;
    private static final int REQUEST_PERMISSIONS_LOCATION = 125;
    private long UPDATE_INTERVAL = 0;
    private long FASTEST_INTERVAL = 0;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private AlertDialog locationDialog;

    private int likedItemPosition = -1;

    private int currentPage = 1;

    private View rootView;
    public PropertyDetailFragment propertyDetailFragment;
    public boolean showOtherFragment = false;


    private Location DEF_DefaultLocation;

    public MapFragment mapFragment;

    public PropertyFragment(Context conext) {
        // Required empty public constructor
        mContext = conext;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.property_fragment, container, false);
        apiRequestManager = new APIRequestManager(mContext, this);

        rv_property = (RecyclerView) rootView.findViewById(R.id.rv_properties);


        rv_property.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        refeshListView = (SwipyRefreshLayout) rootView.findViewById(R.id.srl_properties);
        refeshListView.setDirection(SwipyRefreshLayoutDirection.BOTH);
        propertyAdapter = new PropertyRecycleAdapter(mContext, properties, itemWidth, itemHeight, this);
        rv_property.setAdapter(propertyAdapter);
//        fillRecyclerView();

        rootView.findViewById(R.id.tab_search).setOnClickListener(this);
        rootView.findViewById(R.id.tab_map).setOnClickListener(this);

        buildGoogleApiClient();
        setupRecyclerView();
        setupRefreshLayout();

        DEF_DefaultLocation = new Location("dummyprovider");
        DEF_DefaultLocation.setLatitude(40.774777);
        DEF_DefaultLocation.setLongitude(-73.956332);
//        apiRequestManager.addToRequestQueue(apiRequestManager.getPropertyList(myHandler, 40.774777, -73.956332), true);

        mGoogleApiClient.connect();
        if (!checkLocationServices()) {
            showLocationErrorAlert();
        }

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

//    @Override
//    public void onPause(){
//        super.onPause();
//        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        mGoogleApiClient.disconnect();
//    }

    @Override
    public void onResume() {
        super.onResume();

        if (LocalPreferences.getFlagSearchNow(mContext)) {
            getSearchList();
            LocalPreferences.saveFlagSearchNow(mContext, false);
        }
    }

    @Override
    public void onClick(View view) {
        if (isDoubleClick())
            return;
        switch (view.getId()) {
            case R.id.tab_search:
                ((MainActivity) mContext).showSearchActivity();
                break;
            case R.id.tab_map:
                Gson gson = new GsonBuilder().create();
                PropertyMetaData metaData = new PropertyMetaData();
                metaData.setProperty(properties);

                JsonObject jsonObject = gson.fromJson(gson.toJson(metaData), JsonObject.class);

                mapFragment = new MapFragment(this);
                mapFragment.propertyJson = jsonObject.get("property").toString();


                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.flip_in, R.anim.flip_out, R.anim.flip_in, R.anim.flip_out);

                ft.add(R.id.main_layout, mapFragment);
                ft.addToBackStack(MapFragment.TAG);
                ft.commit();



//                ((MainActivity) mContext).showMapActivity(mLastLocation, properties);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSIONS_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    getPropertyData();
                    if (mLocationRequest == null)
                        startLocationUpdates();
                } else {
                    Toast.makeText(mContext, "Please enable location permission," +
                            " it is required for accessing your location.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void getSearchList(){
        refeshListView.setRefreshing(true);
        apiRequestManager.addToRequestQueue(apiRequestManager.createUserSearch(myHandler, LocalPreferences.getSearchCriteria(mContext), LocalPreferences.getSearchSchedule(mContext), LocalPreferences.getSearchAgentFlag(mContext)), mapFragment != null ? true : false);
//        LocalPreferences.saveFlagSearchNow(mContext, false);

    }

    private void setupRecyclerView() {
        com.dts.dts.views.LinearLayoutManager linearLayoutManager = new com.dts.dts.views.LinearLayoutManager(mContext);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        rv_property.setLayoutManager(linearLayoutManager);
        rv_property.addItemDecoration(new ItemDecorator(getResources().getDimensionPixelSize(R.dimen.marginListitemProperty)));
        ((SimpleItemAnimator) rv_property.getItemAnimator()).setSupportsChangeAnimations(false);


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.mask_listitem_property, opt);

        float mskHeight = opt.outHeight; // height of mask
        float mskWidth = opt.outWidth; // width of mask

        itemHeight = (int) (metrics.widthPixels * mskHeight / mskWidth);
        itemWidth = metrics.widthPixels;
    }

    private void setupRefreshLayout() {
        refeshListView.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                if (direction == SwipyRefreshLayoutDirection.TOP){
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        refeshListView.setRefreshing(false);
                        return;
                    }
                    if (mLastLocation != null) {

                        if (LocalPreferences.getSearchCriteria(getContext()) == null || LocalPreferences.getSearchCriteria(getContext()).isEmpty()){
                            currentPage = 1;
                            apiRequestManager.addToRequestQueue(apiRequestManager.getPropertyList(myHandler, mLastLocation.getLatitude(), mLastLocation.getLongitude(), currentPage), false);
                            refeshListView.setRefreshing(true);
                        }
                        else{
                            getSearchList();

                        }
                    } else {
                        refeshListView.setRefreshing(false);
                        showLocationToast();
                    }
                }
                if (direction == SwipyRefreshLayoutDirection.BOTTOM){
                    if (LocalPreferences.getSearchCriteria(getContext()) == null || LocalPreferences.getSearchCriteria(getContext()).isEmpty())
                    {
                        refeshListView.setRefreshing(true);
                        fetchMoreListings();
                    }
                    else
                        refeshListView.setRefreshing(false);


                }
            }
        });
    }

    private void fillRecyclerView() {
//        updateScrollViewHeight();
        propertyAdapter = new PropertyRecycleAdapter(mContext, properties, itemWidth, itemHeight, this);
        rv_property.setAdapter(propertyAdapter);
    }

    private void updateScrollViewHeight(){
        int height = 267*properties.size()+30;
//        rootView.findViewById(R.id.scroll_content).setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
//       refeshListView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 267*properties.size()+30));

//        self.mainScrollViewHeight.constant = DEF_TableItem_Height*listingArray.count+DEF_TableView_Offset;

    }

    public void fetchMoreListings(){
        currentPage++;
        apiRequestManager.addToRequestQueue(apiRequestManager.getPropertyList(myHandler, mLastLocation.getLatitude(), mLastLocation.getLongitude(), currentPage), false);

    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            apiRequestManager.hideProgressDialog();
            int type = msg.arg1;

            JSONObject response = (JSONObject) msg.obj;;
            if (response == null)
                return;

            boolean success = false;
            try {
                success = response.getBoolean("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (!success)
            {
                refeshListView.setRefreshing(false);
                rootView.findViewById(R.id.ly_empty_list).setVisibility(View.VISIBLE);
                return;
            }

            switch (type)
            {
                case Constants.PROPERTY_LIST:
                    refeshListView.setRefreshing(false);

                    if (response == null){
                        apiRequestManager.hideProgressDialog();
                        break;
                    }


                    Gson gson = new GsonBuilder().create();
                    String formattedResponse = response.toString()
                            .replace("\"data\":{\"", "\"property_meta_data\":{\"")
                            .replace("\"data\":[{", "\"property\":[{");
                    APIPropertyList propertyList = gson.fromJson(formattedResponse, APIPropertyList.class);
                    List<Property> tmpProperties = propertyList.getPropertyMetaData().getProperty();

                    if (currentPage == 1) {
                        properties.clear();
                        for (int i = 0; i < tmpProperties.size(); i++){
                            properties.add(tmpProperties.get(i));
                        }
                        if (properties.size() == 0 &&  (mLastLocation.getLatitude() != DEF_DefaultLocation.getLatitude() || mLastLocation.getLongitude() != DEF_DefaultLocation.getLongitude())){
                            mLastLocation.setLatitude(DEF_DefaultLocation.getLatitude());
                            mLastLocation.setLongitude(DEF_DefaultLocation.getLongitude());
                            getPropertyData();
                        }
                        else {
                            apiRequestManager.hideProgressDialog();

                            fillRecyclerView();

                            ((MainActivity) getActivity()).updateLocationLabel(mLastLocation);
                            setLayoutIfListEmpty();


                        }
                    }
                    else if (currentPage > 1) {
                        for (int i = 0; i < tmpProperties.size(); i++){
                            properties.add(tmpProperties.get(i));
                        }

                        propertyAdapter.notifyDataSetChanged();
                        rv_property.scrollToPosition(tmpProperties.size()+1);
                        setLayoutIfListEmpty();
                    }


                    break;
                case Constants.CREATE_USER_SEARCH:
                    apiRequestManager.addToRequestQueue(apiRequestManager.getSearchResults(myHandler, response), false);

                    break;
                case Constants.SEARCH_RESULTS:
                    refeshListView.setRefreshing(false);

                    Gson gson1 = new GsonBuilder().create();

                    ApiUserSearch userSearch = gson1.fromJson(response.toString().replace("\"data\":{", "\"search_meta_data\":{"), ApiUserSearch.class);

                    List<Detail> details = userSearch.getSearchMetaData().getUserSearches().get(0).getResults().get(0).getDetails();
                    properties.clear();
                    for (int i = 0; i < details.size(); i++) {
                        properties.add(details.get(i).getPropertyFields());
                    }
                    if (!properties.isEmpty()) {
                        fillRecyclerView();
                    }

                    if (properties.size() == 0){
                        apiRequestManager.hideProgressDialog();

                    }

                    else {
                        Property firstProperty = properties.get(0);
                        if (mLastLocation == null)
                            mLastLocation = new Location("dummyprovider");

                        mLastLocation.setLatitude(Double.parseDouble(firstProperty.getLatitude()));
                        mLastLocation.setLongitude(Double.parseDouble(firstProperty.getLongitude()));
                        apiRequestManager.hideProgressDialog();



                        if (LocalPreferences.getSearchCriteria(getContext()) == null || LocalPreferences.getSearchCriteria(getContext()).isEmpty())
                            return;

                        if (mapFragment == null)
                            return;

                        Gson _gson = new GsonBuilder().create();
                        PropertyMetaData metaData = new PropertyMetaData();
                        metaData.setProperty(properties);

                        JsonObject jsonObject = _gson.fromJson(_gson.toJson(metaData), JsonObject.class);

                        mapFragment.propertyJson = jsonObject.get("property").toString();
//                        mapFragment.propertyJson = userSearch.toString();
                        mapFragment.initMapView();

                        ((MainActivity) getActivity()).updateLocationLabel(mLastLocation);


                    }
                    setLayoutIfListEmpty();

                    break;
                case Constants.LIKE_PROPERTY:

                    if (propertyAdapter != null) {

                        JsonObject object = ((MainActivity)mContext).getJsonObjectFromJSON(response);
                        JsonElement data = object.get("data");

                        if (data == null || data.isJsonNull())
                            properties.get(likedItemPosition).setLiked(0);
                        else
                            properties.get(likedItemPosition).setLiked(1);

                        propertyAdapter.notifyItemChanged(likedItemPosition);
                    }
                    apiRequestManager.hideProgressDialog();
                    setLayoutIfListEmpty();

                    break;
            }


        }
    };

    private void showLocationToast() {
        Toast toast = Toast.makeText(mContext, "Couldn't find your Current Location", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static boolean isDoubleClick(){
        long clickTime = System.currentTimeMillis();
        if(clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
            lastClickTime = clickTime;
            return true;
        }
        lastClickTime = clickTime;
        return false;
    }

    private void setLayoutIfListEmpty() {
        if (properties.isEmpty()) {
            if (propertyAdapter != null)
                propertyAdapter.notifyDataSetChanged();
            rootView.findViewById(R.id.ly_empty_list).setVisibility(View.VISIBLE);
        } else
            rootView.findViewById(R.id.ly_empty_list).setVisibility(View.GONE);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mLastLocation == null)
            mLastLocation = location;
        LocalPreferences.saveCurrentLocation(getContext(), mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
        mLastLocation = location;
        LocalPreferences.saveSelectedLat(getContext(), location);

        if (LocalPreferences.getSearchCriteria(getContext()) == null || LocalPreferences.getSearchCriteria(getContext()).isEmpty())
        {
            getPropertyData();
        }
        else {
            getSearchList();

        }
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    @Override
    public void onItemClick(int position) {
        if (isDoubleClick())
            return;

        Property property = properties.get(position);
        Gson gson = new GsonBuilder().create();
        String propertyJson = gson.toJson(property);


        LocalPreferences.saveFlick(getContext(), false);
        LocalPreferences.saveStreetShowing(getContext(), false);



        showOtherFragment = true;
        propertyDetailFragment = new PropertyDetailFragment();
        propertyDetailFragment.getDataFromIntent(propertyJson, "");

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        ft.add(R.id.main_layout, propertyDetailFragment);
        ft.addToBackStack(PropertyDetailFragment.TAG);
        ft.commit();

        ((MainActivity) mContext).logo_ditch.setVisibility(View.INVISIBLE);
        ((MainActivity) mContext).back_ditch.setVisibility(View.VISIBLE);

//        ((MainActivity) mContext).showPropertyDetail(propertyJson, R.id.main_layout, this);
    }

    @Override
    public void onHeartClick(int position) {
        if (isDoubleClick())
            return;

        String cid = LocalPreferences.getUserCid(mContext);
        String propertyID = String.valueOf(properties.get(position).getId());
        if (cid.isEmpty())
            ((MainActivity) mContext).showRequestInfoView(propertyID);
        else {
            likedItemPosition = position;
            apiRequestManager.addToRequestQueue(apiRequestManager.likeProperty(myHandler, propertyID), true);
        }
    }

    public void onNetworkRequestSuccess(APIRequestManager.APIRequestType type, JsonObject response)
    {

    }

    public void onNetworkRequestError(APIRequestManager.APIRequestType type, VolleyError error){

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void checkForLocationPermission() {
        // Here, thisActivity is the current activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSIONS_LOCATION);
            }
        }
    }

    public void getPropertyData() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mLastLocation != null) {
            refeshListView.setRefreshing(true);
            apiRequestManager.addToRequestQueue(apiRequestManager.getPropertyList(myHandler, mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1), false);
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkForLocationPermission();
        startLocationUpdates();
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
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

    public boolean checkLocationServices() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void showLocationErrorAlert() {
        if (locationDialog == null)
            locationDialog = new AlertDialog.Builder(mContext).setTitle("Location Error")
                    .setMessage("Unable to get location. Please turn on location services and try again.")
                    .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            dialog.dismiss();
                        }
                    }).create();
        if (!locationDialog.isShowing())
            locationDialog.show();
    }

}
