package com.dts.dts.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.dts.dts.R;
import com.dts.dts.adapters.PropertyRecycleAdapter;
import com.dts.dts.models.Property;
import com.dts.dts.remote.APIRequestManager;
import com.dts.dts.views.ItemDecorator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment implements APIRequestManager.APIRequestListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private APIRequestManager apiRequestManager;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refeshListView;
    private int itemHeight;
    private int itemWidth;
    private Location mLastLocation;
    private List<Property> properties;
    private PropertyRecycleAdapter recyclerAdapter;


    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        apiRequestManager = new APIRequestManager(getActivity(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mParam1 != null) {
            Gson gson = new GsonBuilder().create();
            properties = Arrays.asList(gson.fromJson(mParam1, Property[].class));
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_properties);
        refeshListView = (SwipeRefreshLayout) view.findViewById(R.id.srl_properties);

        view.findViewById(R.id.tab_search).setOnClickListener(this);
        view.findViewById(R.id.tab_map).setOnClickListener(this);

        setupRecyclerView();
        setupRefreshLayout();
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new ItemDecorator(getResources().getDimensionPixelSize(R.dimen.marginListitemProperty)));

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
        refeshListView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (mLastLocation != null)
                    apiRequestManager.addToRequestQueue(apiRequestManager.getPropertyList(myHandler, mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1), false);
                refeshListView.setRefreshing(true);
            }
        });
    }

    private void fillRecyclerView() {
        recyclerAdapter = new PropertyRecycleAdapter(getActivity(), properties, itemWidth, itemHeight, null);
        recyclerView.setAdapter(recyclerAdapter);
    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };
    @Override
    public void onNetworkRequestSuccess(APIRequestManager.APIRequestType type, JsonObject response) {

    }

    @Override
    public void onNetworkRequestError(APIRequestManager.APIRequestType type, VolleyError error) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_search:
                //showSearchActivity();
                break;
            case R.id.tab_map:
                //((MainActivity) getActivity()).transactFragment();
                break;
        }
    }
}
