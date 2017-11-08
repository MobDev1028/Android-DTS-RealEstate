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
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.daimajia.swipe.util.Attributes;
import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.activities.MainActivity;
import com.dts.dts.adapters.MessageAdapter;
import com.dts.dts.adapters.MessageRecycleAdapter;
import com.dts.dts.adapters.PropertyRecycleAdapter;
import com.dts.dts.adapters.SwiftAdapter;
import com.dts.dts.models.APIPropertyList;
import com.dts.dts.models.ApiUserSearch;
import com.dts.dts.models.Detail;
import com.dts.dts.models.Parent;
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

import java.util.ArrayList;
import java.util.List;


public class MessageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, APIRequestManager.APIRequestListener, MessageAdapter.OnItemClickListener{
    public static final String TAG = MessageFragment.class.getSimpleName();

    private Context mContext;
    private RecyclerView rv_property;
    private PropertyRecycleAdapter propertyAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private APIRequestManager apiRequestManager;
    private List<Property> properties = new ArrayList<>();

    private SwipeRefreshLayout refeshListView;
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

    private View rootView;

    public boolean showOtherFragment = false;
    public PropertyDetailFragment propertyDetailFragment;
    public DocFragment docFragment;
    public FollowupFragment followupFragment;
    public DemoFragment demoFragment;

    private boolean isBack;
    ExpandableListView rv_message;
//    MessageRecycleAdapter messageAdapter;
    MessageAdapter messageAdapter;

    private ArrayList<Parent> dataSource = new ArrayList<>();

    public MessageFragment(Context conext) {
        // Required empty public constructor
        mContext = conext;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.message_fragment, container, false);
        apiRequestManager = new APIRequestManager(mContext, this);

        rv_message = (ExpandableListView) rootView.findViewById(R.id.rv_properties);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);
        getMessageInfos(true);

        ((MainActivity) getActivity()).locationLabel.setText("Messages");

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
//        showOtherFragment = false;


    }
    public void getMessageInfos(boolean bLoading)
    {
        swipeRefreshLayout.setRefreshing(bLoading);
        apiRequestManager.addToRequestQueue(apiRequestManager.getMessageList(myHandler, null), false);

    }

    private void fillRecyclerView() {
        messageAdapter = new MessageAdapter(mContext, dataSource, this);
        rv_message.setAdapter(messageAdapter);
    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            apiRequestManager.hideProgressDialog();
            swipeRefreshLayout.setRefreshing(false);
            int type = msg.arg1;

            JSONObject response = (JSONObject) msg.obj;


            switch (type) {
                case Constants.MESSAGE_LIST:

                    if (response == null)
                        break;

                    String message = null;
                    try {
                        dataSource.clear();
                        message = response.getString("message");
                        JSONObject data = response.getJSONObject("data");
                        JSONArray threads = data.getJSONArray("thread");
                        for (int i = 0; i < threads.length(); i++)
                        {
                            Parent parent = new Parent();
                            parent.dictProperty = threads.getJSONObject(i);
                            JSONArray tempArray = parent.dictProperty.getJSONArray("msgs");
                            parent.childs = new ArrayList<>();

                            for (int j = 0; j < tempArray.length(); j++)
                            {
                                JSONObject dictMsg = tempArray.getJSONObject(j);
                                if (dictMsg != null)
                                {
                                    String dictMsgString = dictMsg.getString("type");
                                    if (dictMsgString != null)
                                        parent.childs.add(dictMsg);
                                }
                            }
                            parent.State = Parent.Collapsed;
                            parent.dict = parent.childs.get(0);
                            dataSource.add(parent);

                        }
                        swipeRefreshLayout.setRefreshing(false);
                        fillRecyclerView();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (message != null && !message.isEmpty())
                        apiRequestManager.showDefaultPopup("", message);


                    fillRecyclerView();
                    break;
                case Constants.UPDATE_MSG:
                    fillRecyclerView();
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

    public void onActionClick(int parentPosition, int childPosition)
    {
        JSONObject dictSelectedMessage = dataSource.get(parentPosition).childs.get(childPosition);
        JSONObject dictProperty = dataSource.get(parentPosition).dictProperty;

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        try {
            if (dictSelectedMessage.getString("type").equalsIgnoreCase("doc_sign")) {
                docFragment = new DocFragment(dictSelectedMessage, dictProperty, getContext());
                docFragment.setOnDocListener(new DocFragment.DocFragmentListener() {
                    @Override
                    public void confirm() {
                        getMessageInfos(true);
                    }

                    @Override
                    public void declined() {
                        getMessageInfos(true);
                    }
                });
                ft.add(R.id.message_layout, docFragment);
                ft.addToBackStack(DocFragment.TAG);
                ft.commit();
            }
            else if(dictSelectedMessage.getString("type").equalsIgnoreCase("demo"))
            {
                demoFragment = new DemoFragment(dictSelectedMessage, dictProperty, getContext());
                demoFragment.setOnDemoListener(new DemoFragment.DemoFragmentListener() {
                    @Override
                    public void doneAction() {
                        getMessageInfos(true);
                    }
                });
                ft.add(R.id.message_layout, demoFragment);
                ft.addToBackStack(DemoFragment.TAG);
                ft.commit();
            }
            else if (dictSelectedMessage.getString("type").equalsIgnoreCase("inquire")) {
                followupFragment = new FollowupFragment(dictSelectedMessage, dictProperty, getContext());
                ft.add(R.id.message_layout, followupFragment);
                ft.addToBackStack(FollowupFragment.TAG);
                ft.commit();
            }
            else {
                followupFragment = new FollowupFragment(dictSelectedMessage, dictProperty, getContext());
                ft.add(R.id.message_layout, followupFragment);
                ft.addToBackStack(FollowupFragment.TAG);
                ft.commit();
            }
            ((MainActivity) getActivity()).back_ditch.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).logo_ditch.setVisibility(View.GONE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onRefresh() {
        getMessageInfos(true);
    }

    public void onPropertyClick(int position)
    {
        if (isDoubleClick())
            return;

        JSONObject propertyDic = dataSource.get(position).dictProperty;

        String propertyJson = propertyDic.toString();
//        String origin = "";
//
        showOtherFragment = true;
        propertyDetailFragment = new PropertyDetailFragment();
        propertyDetailFragment.getDataFromIntent(propertyJson, "");

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        ft.add(R.id.message_layout, propertyDetailFragment);
        ft.addToBackStack(PropertyDetailFragment.TAG);
        ft.commit();

        ((MainActivity) mContext).logo_ditch.setVisibility(View.INVISIBLE);
        ((MainActivity) mContext).back_ditch.setVisibility(View.VISIBLE);

//        Gson gson = new GsonBuilder().create();
//        Property property = gson.fromJson(propertyJson, Property.class);
//        headerLabel = property.getCity() + " " + property.getStateOrProvince();

//        ((MainActivity) mContext).showPropertyDetail(propertyJson, R.id.message_layout, this);

    }

    public void onDeleteMessage(int position)
    {
        if (isDoubleClick())
            return;

        dataSource.remove(position);

        try {
            apiRequestManager.addToRequestQueue(apiRequestManager.getUpdateMsg(myHandler, String.format("%d", dataSource.get(position).dict.getInt("id")), "archive"), false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void onNetworkRequestSuccess(APIRequestManager.APIRequestType type, JsonObject response)
    {

    }

    public void onNetworkRequestError(APIRequestManager.APIRequestType type, VolleyError error){

    }
}
