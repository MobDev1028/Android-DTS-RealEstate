package com.dts.dts.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.activities.MainActivity;
import com.dts.dts.remote.APIRequestManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by silver on 1/31/17.
 */

public class DemoFragment extends Fragment {
    public static final String TAG = DemoFragment.class.getSimpleName();

    private ImageButton btnProperty;
    private TextView tvSubject;
    private TextView tvAddress;
    private TextView tvCountry;
    private Button btnAccept;
    private Button btnDecline;
    private TextView tvMessage;

    private GoogleMap mGoogleMap;

    private JSONObject dictSelectedMessage;
    private JSONObject dictProperty;
    Context mContext;
    private APIRequestManager apiRequestManager;

    public DemoFragment(JSONObject dm, JSONObject dp, Context context){
        mContext = context;
        dictSelectedMessage = dm;
        dictProperty = dp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) getActivity()).locationLabel.setText("");

        View rootView = inflater.inflate(R.layout.demo_message, container, false);

        btnProperty = (ImageButton) rootView.findViewById(R.id.btn_property);
        tvSubject = (TextView) rootView.findViewById(R.id.tv_subject);
        tvAddress = (TextView) rootView.findViewById(R.id.tv_address);
        tvCountry = (TextView) rootView.findViewById(R.id.tv_country);
        btnAccept = (Button) rootView.findViewById(R.id.btn_accept);
        btnDecline = (Button) rootView.findViewById(R.id.decline_button);
        tvMessage = (TextView) rootView.findViewById(R.id.tv_message);

        populateFields();

        btnDecline.setVisibility(View.VISIBLE);
        btnAccept.setVisibility(View.VISIBLE);
//        btnAccept.setEnabled(true);

        try {
            if (dictSelectedMessage.getInt("accepted") != 0){
                btnAccept.setText("Already accepted");
                btnDecline.setVisibility(View.INVISIBLE);
                btnAccept.setEnabled(false);
            }
            else if (dictSelectedMessage.getInt("declined") != 0){
                btnDecline.setText("Declined");
                btnAccept.setVisibility(View.INVISIBLE);
            }



            SupportMapFragment generalMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mv_diatance);

            setupDistanceMap(generalMap);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPropertyClick();
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDecline();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAccept();
            }
        });

        rootView.findViewById(R.id.mapbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = null;
                try {
                    address = dictProperty.getString("address");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                address =  address.trim();
                String url = "http://maps.google.com/?address="+address;
//                try {
//                    url = URLEncoder.encode(url, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                url = url.replace(" ", "%20");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                if (browserIntent.resolveActivity(mContext.getPackageManager()) != null)
                    startActivity(browserIntent);
//                }


            }
        });

        apiRequestManager = new APIRequestManager(mContext, null);

        return rootView;
    }

    private void populateFields(){
        tvSubject.setTextColor(Color.parseColor("#ff0500"));
        try {
            if (dictSelectedMessage.getString("type").equalsIgnoreCase("doc_sign")) {
                tvSubject.setText("SIGN LEASE");
            }
            else if (dictSelectedMessage.getString("type").equalsIgnoreCase("follow_up")) {
                tvSubject.setText("FOLLOW UP");

            }
            else if (dictSelectedMessage.getString("type").equalsIgnoreCase("demo")) {
                tvSubject.setText("ON-SITE DEMO");

            }
            else if (dictSelectedMessage.getString("type").equalsIgnoreCase("inquire")) {
                tvSubject.setText("INQUIRED");
                tvSubject.setTextColor(Color.parseColor("#02ce37"));
            }
            else {
                tvSubject.setText(dictSelectedMessage.getString("type").toUpperCase());

            }

            String address1 = dictProperty.getString("address1");
            String city = dictProperty.getString("city");
            String state = dictProperty.getString("state_or_province");
            String zip = dictProperty.getString("zip");

            tvAddress.setText(address1);
            tvCountry.setText(city + ", " + state + " " + zip);
            tvMessage.setText(dictSelectedMessage.getString("content"));
            final String imgURL = dictProperty.getJSONObject("img_url").getString("sm");

            Picasso.with(mContext).load(imgURL).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.image_placeholder).into(btnProperty, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(mContext).load(imgURL).into(btnProperty);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setupDistanceMap(SupportMapFragment distMap) {
        double latitude = 0.0;
        double longitude = 0.0;
        try {
            latitude = Double.parseDouble(dictProperty.getString("latitude"));
            longitude = Double.parseDouble(dictProperty.getString("longitude"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        distMap.getView().setBackgroundColor(Color.WHITE);

        final double finalLatitude = latitude;
        final double finalLongitude = longitude;
        distMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(finalLatitude, finalLongitude), 14));

                MarkerOptions markerOpt = new MarkerOptions();
                markerOpt.position(new LatLng(finalLatitude, finalLongitude));
                markerOpt.title("");
                markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.playground_map_red));
                googleMap.addMarker(markerOpt);

            }
        });
    }

    public boolean showOtherFragment = false;
    public PropertyDetailFragment propertyDetailFragment;

    public void onPropertyClick()
    {
        if (isDoubleClick())
            return;

        showOtherFragment = true;
        propertyDetailFragment = new PropertyDetailFragment();
        propertyDetailFragment.getDataFromIntent(dictProperty.toString(), "");

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        ft.add(R.id.demo_layout, propertyDetailFragment);
        ft.addToBackStack(PropertyDetailFragment.TAG);
        ft.commit();

        ((MainActivity) mContext).logo_ditch.setVisibility(View.INVISIBLE);
        ((MainActivity) mContext).back_ditch.setVisibility(View.VISIBLE);
//        ((MainActivity) mContext).showPropertyDetail(dictProperty.toString(), R.id.demo_layout, this);
    }

    private void declineMessage()
    {
        try {
            apiRequestManager.addToRequestQueue(apiRequestManager.getUpdateMsg(myHandler, String.format("%d", dictSelectedMessage.getInt("id")), "decline"), false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onClickDecline()
    {
        declineMessage();
    }

    public void onClickAccept(){
        try {
            apiRequestManager.addToRequestQueue(apiRequestManager.getUpdateMsg(myHandler, String.format("%d", dictSelectedMessage.getInt("id")), "accept"), false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            apiRequestManager.hideProgressDialog();
            int type = msg.arg1;

            JSONObject response = (JSONObject) msg.obj;

            String message = null;

            switch (type) {
                case Constants.UPDATE_MSG:
                    if (response == null)
                        break;

                    try {
                        message = response.getString("message");
                        getFragmentManager().popBackStack();
                        if (listener != null)
                            listener.doneAction();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (message != null && !message.isEmpty())
                        apiRequestManager.showDefaultPopup("", message);
                    break;
                case Constants.CONFIRM_CONFIG:
                    if (response == null)
                        break;

                    try {
                        message = response.getString("message");
                        getFragmentManager().popBackStack();
                        if (listener != null)
                            listener.doneAction();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (message != null && !message.isEmpty())
                        apiRequestManager.showDefaultPopup("", message);
                    break;
            }
        }
    };
    private DemoFragmentListener listener;
    public void setOnDemoListener(DemoFragmentListener l)
    {
        listener = l;
    }

    public interface DemoFragmentListener {
        void doneAction();
    }
}
