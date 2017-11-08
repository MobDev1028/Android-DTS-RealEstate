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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.activities.MainActivity;
import com.dts.dts.adapters.FavoriteRecycleAdapter;
import com.dts.dts.adapters.PropertyRecycleAdapter;
import com.dts.dts.models.APIPropertyList;
import com.dts.dts.models.ApiUserSearch;
import com.dts.dts.models.Detail;
import com.dts.dts.models.Property;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends Fragment implements View.OnClickListener, APIRequestManager.APIRequestListener, FavoriteRecycleAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener{
    public static final String TAG = FavoriteFragment.class.getSimpleName();

    private Context mContext;
    private ListView rv_favorite;
    private FavoriteRecycleAdapter favoriteAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    private APIRequestManager apiRequestManager;
    private List<Property> favorites;

    private View rootView;

    private int itemHeight;
    private int itemWidth;

    public boolean showOtherFragment = false;
    public PropertyDetailFragment propertyDetailFragment;
    public static long lastClickTime = 0;
    public static final long DOUBLE_CLICK_TIME_DELTA = 500;


    public FavoriteFragment(Context conext) {
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
        rootView = inflater.inflate(R.layout.favorite_fragment, container, false);
        apiRequestManager = new APIRequestManager(mContext, this);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);
        rv_favorite = (ListView) rootView.findViewById(R.id.rv_properties);

        setupRecyclerView();

        getFavoriteProperties(true);

        ((MainActivity) getActivity()).locationLabel.setText("Favs & Nopes");

        return rootView;
    }

    private void setupRecyclerView() {
//        com.dts.dts.views.LinearLayoutManager linearLayoutManager = new com.dts.dts.views.LinearLayoutManager(mContext);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
//        linearLayoutManager.setSmoothScrollbarEnabled(true);
//        rv_favorite.setLayoutManager(linearLayoutManager);
//        rv_favorite.addItemDecoration(new ItemDecorator(getResources().getDimensionPixelSize(R.dimen.marginListitemProperty)));
//        ((SimpleItemAnimator) rv_favorite.getItemAnimator()).setSupportsChangeAnimations(false);



    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        apiRequestManager.destroyProgressDialog();
    }

    @Override
    public void onClick(View view) {
        if (isDoubleClick())
            return;
        switch (view.getId()) {
            case R.id.tab_search:
                ((MainActivity) mContext).showSearchActivity();
                break;
        }
    }

    public void onRefresh() {
        getFavoriteProperties(true);
    }

    private void getFavoriteProperties(boolean bLoading)
    {
        swipeRefreshLayout.setRefreshing(bLoading);
        apiRequestManager.addToRequestQueue(apiRequestManager.getFavoriteList(myHandler), false);

    }

    private void fillRecyclerView() {
        favoriteAdapter = new FavoriteRecycleAdapter(mContext, favorites, this);
        rv_favorite.setAdapter(favoriteAdapter);

    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int type = msg.arg1;
//            apiRequestManager.hideProgressDialog();
            swipeRefreshLayout.setRefreshing(false);

            JSONObject response = (JSONObject) msg.obj;

            String message = null;

            switch (type) {
                case Constants.FAVORITE_LIST:
                    apiRequestManager.hideProgressDialog();

                    if (response == null)
                        break;

                    Gson gson = new GsonBuilder().create();
                    favorites = new ArrayList<>();

                    try {
                        message = response.getString("message");
                        JSONObject data = response.getJSONObject("data");
                        JSONObject favDic = data.getJSONObject("favs");
                        JSONObject hideDic = data.getJSONObject("hides");


                        String formatted = response.toString()
                            .replace("\"data\":{\"favs\":{\"", "\"property_meta_data\":{\"")
                            .replace("\"data\":[{", "\"property\":[{")
                            .replace("},\"success\":", ",\"success\":");


                        String formattedResponse = "{property_meta_data:" + favDic.toString()
                                .replace("\"data\":[{", "\"property\":[{")
                                .replace("}]}", "}]}}");

                        APIPropertyList favList = gson.fromJson(formattedResponse, APIPropertyList.class);
                        for (int i = 0; i < favList.getPropertyMetaData().getProperty().size(); i++)
                        {
                            favList.getPropertyMetaData().getProperty().get(i).setHidden(0);
                            favorites.add(favList.getPropertyMetaData().getProperty().get(i));
                        }

                        formattedResponse = "{property_meta_data:" + hideDic.toString()
                                .replace("\"data\":[{", "\"property\":[{")
                                .replace("}]}", "}]}}");

                        APIPropertyList hideList = gson.fromJson(formattedResponse, APIPropertyList.class);
                        for (int i = 0; i < hideList.getPropertyMetaData().getProperty().size(); i++)
                        {
                            hideList.getPropertyMetaData().getProperty().get(i).setHidden(1);
                            favorites.add(hideList.getPropertyMetaData().getProperty().get(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (message != null && !message.isEmpty())
                        apiRequestManager.showDefaultPopup("", message);



//                    String formattedResponse = response.toString()
//                            .replace("\"data\":{\"favs\":{\"", "\"property_meta_data\":{\"")
//                            .replace("\"data\":[{", "\"property\":[{")
//                            .replace("},\"success\":", ",\"success\":");
//
//                    APIPropertyList propertyList = gson.fromJson(formattedResponse, APIPropertyList.class);
//
//
//                    try {
//                        JSONObject hidesDic = response.getJSONObject("data").getJSONObject("hides");
//                        JSONArray properties = hidesDic.getJSONArray("data");
//                        for (int i = 0; i < properties.length(); i++)
//                        {
//                            JSONObject propertyDic = properties.getJSONObject(i);
//                            Property property = gson.fromJson(propertyDic.toString(), Property.class);
//                            propertyList.getPropertyMetaData().addProperty(property);
//                        }
//
//                        favorites = new ArrayList<>();
//                        favorites = propertyList.getPropertyMetaData().getProperty();
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    fillRecyclerView();
                    break;
                case Constants.HIDE_PROPERTY:
                    try {
                        message = response.getString("message");
                        {
//                            if (!apiRequestManager.hideProgress())
                            getFavoriteProperties(false);
                        }
                        if (message != null && !message.isEmpty()) {
                            apiRequestManager.showDefaultPopup("", message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };


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
    public void onActionClick(int position) {
        if (isDoubleClick())
            return;
        Property dictProperty = favorites.get(position);
        String address = dictProperty.getAddress();

        String url = "http://maps.google.com/?address=" + address;
        ((MainActivity) mContext).openUrl(url);

    }

    @Override
    public void onPropertyClick(int position) {
        if (isDoubleClick())
            return;

        Property property = favorites.get(position);
        Gson gson = new GsonBuilder().create();
        String propertyJson = gson.toJson(property);

        showOtherFragment = true;
        propertyDetailFragment = new PropertyDetailFragment();
        propertyDetailFragment.getDataFromIntent(propertyJson, "");

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        ft.add(R.id.favorite_layout, propertyDetailFragment);
        ft.addToBackStack(PropertyDetailFragment.TAG);
        ft.commit();

        ((MainActivity) mContext).logo_ditch.setVisibility(View.INVISIBLE);
        ((MainActivity) mContext).back_ditch.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideClick(int position)
    {
        Property dictProperty = favorites.get(position);

        apiRequestManager.addToRequestQueue(apiRequestManager.hideProperty(myHandler, String.format("%s", dictProperty.getId())), true);

    }

    @Override
    public void onUnfavClick(int postion)
    {
        apiRequestManager.addToRequestQueue(apiRequestManager.likeProperty(myHandler, String.format("%s", favorites.get(postion).getId())), false);
        favorites.remove(postion);
        fillRecyclerView();

    }

    public void onNetworkRequestSuccess(APIRequestManager.APIRequestType type, JsonObject response)
    {

    }

    public void onNetworkRequestError(APIRequestManager.APIRequestType type, VolleyError error){

    }
}
