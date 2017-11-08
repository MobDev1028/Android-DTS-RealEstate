package com.dts.dts.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.activities.MainActivity;
import com.dts.dts.adapters.FavoriteRecycleAdapter;
import com.dts.dts.adapters.MyDitchRecycleAdapter;
import com.dts.dts.models.APIPropertyList;
import com.dts.dts.models.Property;
import com.dts.dts.remote.APIRequestManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MyDitchFragment extends Fragment implements View.OnClickListener, APIRequestManager.APIRequestListener, MyDitchRecycleAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener{
    public static final String TAG = MyDitchFragment.class.getSimpleName();

    private Context mContext;
    private TextView statusLabel;
    private ListView rv_ditch;
    private MyDitchRecycleAdapter myDitchAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    private APIRequestManager apiRequestManager;

    private View rootView;

    private int itemHeight;
    private int itemWidth;

    public boolean showOtherFragment = false;
    public PropertyDetailFragment propertyDetailFragment;
    public static long lastClickTime = 0;
    public static final long DOUBLE_CLICK_TIME_DELTA = 500;

    private List<Property> properties = new ArrayList<>();

    public MyDitchFragment(Context conext) {
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

        rv_ditch = (ListView) rootView.findViewById(R.id.rv_properties);
        statusLabel = (TextView) rootView.findViewById(R.id.statusLabel);
        setupRecyclerView();

        getDitches(true);
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
        getDitches(true);
    }

    private void getDitches(boolean bLoading)
    {
        swipeRefreshLayout.setRefreshing(bLoading);
        apiRequestManager.addToRequestQueue(apiRequestManager.getMyPropertyList(myHandler), false);

    }

    private void fillRecyclerView() {
        myDitchAdapter = new MyDitchRecycleAdapter(mContext, properties, this);
        rv_ditch.setAdapter(myDitchAdapter);

    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int type = msg.arg1;
            swipeRefreshLayout.setRefreshing(false);

            JSONObject response = (JSONObject) msg.obj;

            String message = null;
            apiRequestManager.hideProgressDialog();

            switch (type) {
                case Constants.PROPERTY_LIST:

                    if (response == null)
                        break;

                    properties.clear();
                    Gson gson = new GsonBuilder().create();
                    String formattedResponse = response.toString()
                            .replace("\"data\":{\"", "\"property_meta_data\":{\"")
                            .replace("\"data\":[{", "\"property\":[{");
                    APIPropertyList propertyList = gson.fromJson(formattedResponse, APIPropertyList.class);
                    List<Property> tmpProperties = propertyList.getPropertyMetaData().getProperty();
                    for (int i = 0; i < tmpProperties.size(); i++){
                        properties.add(tmpProperties.get(i));

                    }
                    if (properties.size() == 0)
                        statusLabel.setVisibility(View.VISIBLE);
                    else
                        statusLabel.setVisibility(View.GONE);

                    if (message != null && !message.isEmpty())
                        apiRequestManager.showDefaultPopup("", message);

                    fillRecyclerView();
                    break;
                case Constants.DELETE_PROPERTY:

                    try {
                        message = response.getString("message");
                        boolean isSuccess = response.getBoolean("success");
                        if (isSuccess)
                        {
                            getDitches(false);
                        }
                        else{
                            if (message != null && !message.isEmpty()) {
                                apiRequestManager.showDefaultPopup("Failed to delete", message);
                            }
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
    public void onPropertyClick(int position) {
        if (isDoubleClick())
            return;

        Property property = properties.get(position);
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
    public void onDeleteClick(int position)
    {
        Property dictProperty = properties.get(position);
        apiRequestManager.addToRequestQueue(apiRequestManager.deleteProperty(myHandler, String.format("%s", dictProperty.getId())), true);
    }

    public void onNetworkRequestSuccess(APIRequestManager.APIRequestType type, JsonObject response)
    {

    }

    public void onNetworkRequestError(APIRequestManager.APIRequestType type, VolleyError error){

    }
}
