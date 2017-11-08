package com.dts.dts.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.adapters.FavoriteRecycleAdapter;
import com.dts.dts.adapters.PropertyRecycleAdapter;
import com.dts.dts.animation.ActivitySwitcher;
import com.dts.dts.fragments.FavoriteFragment;
import com.dts.dts.fragments.FeedbackFragment;
import com.dts.dts.fragments.MapFragment;
import com.dts.dts.fragments.MessageFragment;
import com.dts.dts.fragments.MyDitchFragment;
import com.dts.dts.fragments.PrivacyFragment;
import com.dts.dts.fragments.PropertyDetailFragment;
import com.dts.dts.fragments.PropertyFragment;
import com.dts.dts.fragments.SearchAgentFragment;
import com.dts.dts.fragments.SupportFragment;
import com.dts.dts.fragments.TermsOfServiceFragment;
import com.dts.dts.models.APIPropertyList;
import com.dts.dts.models.ApiUserSearch;
import com.dts.dts.models.Detail;
import com.dts.dts.models.Property;
import com.dts.dts.models.PropertyMetaData;
import com.dts.dts.remote.APIRequestManager;
import com.dts.dts.utils.LocalPreferences;
import com.dts.dts.views.HNTextView;
import com.dts.dts.views.ItemDecorator;
import com.dts.dts.views.LinearLayoutManager;
import com.dts.dts.utils.SideMenuCreator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.dts.dts.remote.APIRequestManager.APIRequestType.PROPERTY_LIST;

public class MainActivity extends AppCompatActivity implements View.OnClickListener/*, PropertyRecycleAdapter.OnItemClickListener*/ {

    private SideMenuCreator.SideMenuToggle menuToggle;


    public LinearLayout tabbar_layout;

    private RelativeLayout homeButton_layout;
    private RelativeLayout messageButton_layout;
    private RelativeLayout heartButton_layout;

    private int current_layout = 0;
    private final int PROPERTY_LAYOUT_IDX = 0;
    private final int MESSAGE_LAYOUT_IDX = 1;
    private final int HEART_LAYOUT_IDX = 2;

    private ImageView homeButton;
    private ImageView messageButton;
    private ImageView heartButton;

    public TextView locationLabel;

    public ImageView back_ditch;
    public ImageView logo_ditch;

    public static PropertyFragment propertyFragment = null;
    public static FavoriteFragment favoriteFragment = null;
    public static MessageFragment messageFragment = null;
    public static MyDitchFragment myDitchFragment = null;


    private final int TAB_PROPERTY = 0;
    private final int TAB_MESSAGE = 1;
    private final int TAB_FAVORITE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        findViewById(R.id.btn_near_my_location).setOnClickListener(this);
        locationLabel = (TextView) findViewById(R.id.btn_near_my_location);

        propertyFragment = null;
        favoriteFragment = null;
        messageFragment = null;
        myDitchFragment = null;


        setupLayouts();

        setupButtons();

        setLayoutState();

        String json = getIntent().getStringExtra(Constants.EXTRA_PROPERTY_JSON);
        String origin = getIntent().getStringExtra("origin");

    }

    @Override
    public void onResume() {
        super.onResume();
//        if (LocalPreferences.getFlagSearchNow(this)) {
//            if (propertyFragment != null)
//                propertyFragment.getSearchList();
//        }
    }

    private void setupLayouts()
    {
        tabbar_layout = (LinearLayout) findViewById(R.id.ly_tabbar_view);

        current_layout = PROPERTY_LAYOUT_IDX;

        homeButton_layout = (RelativeLayout) findViewById(R.id.ly_homebutton);
        messageButton_layout = (RelativeLayout) findViewById(R.id.ly_messagebutton);
        heartButton_layout = (RelativeLayout) findViewById(R.id.ly_heartbutton);
    }

    private void setupButtons()
    {
        homeButton = (ImageView) findViewById(R.id.ib_home);
        messageButton = (ImageView) findViewById(R.id.ib_message);
        heartButton = (ImageView) findViewById(R.id.ib_heart);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_layout != PROPERTY_LAYOUT_IDX && propertyFragment != null)
                {
                    if (propertyFragment.mapFragment != null) {
                        if (propertyFragment.mapFragment.showOtherFragment == false){
                            locationLabel.setText(LocalPreferences.getLocationAddress(MainActivity.this));
                            logo_ditch.setVisibility(View.VISIBLE);
                            back_ditch.setVisibility(View.INVISIBLE);
                        }
                        else {
                            if (propertyFragment.mapFragment.propertyDetailFragment != null) {
                                locationLabel.setText(propertyFragment.mapFragment.propertyDetailFragment.headLabel);
                                logo_ditch.setVisibility(View.INVISIBLE);
                                back_ditch.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    else{
                        if (propertyFragment.showOtherFragment == false){
                            locationLabel.setText(LocalPreferences.getLocationAddress(MainActivity.this));
                            logo_ditch.setVisibility(View.VISIBLE);
                            back_ditch.setVisibility(View.INVISIBLE);
                        }
                        else {
                            if (propertyFragment.propertyDetailFragment != null) {
                                locationLabel.setText(propertyFragment.propertyDetailFragment.headLabel);
                                logo_ditch.setVisibility(View.INVISIBLE);
                                back_ditch.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                }
                current_layout = PROPERTY_LAYOUT_IDX;
                setLayoutState();
            }
        });

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_layout != MESSAGE_LAYOUT_IDX && messageFragment != null)
                {
                    if (messageFragment.docFragment != null){
                        if (messageFragment.docFragment.showOtherFragment == false)
                            locationLabel.setText("");
                        else{

                            if (messageFragment.docFragment.propertyDetailFragment != null){
                                locationLabel.setText(messageFragment.docFragment.propertyDetailFragment.headLabel);
                                logo_ditch.setVisibility(View.INVISIBLE);
                                back_ditch.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    else if (messageFragment.demoFragment != null){
                        if (messageFragment.demoFragment.showOtherFragment == false)
                            locationLabel.setText("");
                        else{

                            if (messageFragment.demoFragment.propertyDetailFragment != null){
                                locationLabel.setText(messageFragment.demoFragment.propertyDetailFragment.headLabel);
                                logo_ditch.setVisibility(View.INVISIBLE);
                                back_ditch.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    else if (messageFragment.followupFragment != null){
                        if (messageFragment.followupFragment.showOtherFragment == false)
                            locationLabel.setText("");
                        else{

                            if (messageFragment.followupFragment.propertyDetailFragment != null){
                                locationLabel.setText(messageFragment.followupFragment.propertyDetailFragment.headLabel);
                                logo_ditch.setVisibility(View.INVISIBLE);
                                back_ditch.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    else{
                        if (messageFragment.showOtherFragment == false){
                            locationLabel.setText("Messages");
                            logo_ditch.setVisibility(View.VISIBLE);
                            back_ditch.setVisibility(View.INVISIBLE);
                        }
                        else{

                            if (messageFragment.propertyDetailFragment != null){
                                locationLabel.setText(messageFragment.propertyDetailFragment.headLabel);
                                logo_ditch.setVisibility(View.INVISIBLE);
                                back_ditch.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                }

                current_layout = MESSAGE_LAYOUT_IDX;
                setLayoutState();
            }
        });

        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_layout != HEART_LAYOUT_IDX && favoriteFragment != null)
                {
                    if (favoriteFragment.showOtherFragment == false){
                        locationLabel.setText("Favs & Nopes");
                        logo_ditch.setVisibility(View.VISIBLE);
                        back_ditch.setVisibility(View.INVISIBLE);
                    }
                    else{
                        if (favoriteFragment.propertyDetailFragment != null)
                        {
                            locationLabel.setText(favoriteFragment.propertyDetailFragment.headLabel);
                            logo_ditch.setVisibility(View.INVISIBLE);
                            back_ditch.setVisibility(View.VISIBLE);
                        }
                    }
                }

                current_layout = HEART_LAYOUT_IDX;
                setLayoutState();
            }
        });

        logo_ditch = (ImageView) findViewById(R.id.img_logo_ditch);
        back_ditch = (ImageView) findViewById(R.id.img_back_ditch);
        back_ditch.setVisibility(View.INVISIBLE);
        back_ditch.setOnClickListener(this);
    }

    private void setLayoutState()
    {
        if (current_layout == PROPERTY_LAYOUT_IDX)
        {
            homeButton_layout.setBackgroundColor(Color.rgb(56, 157, 195));

            messageButton_layout.setBackgroundColor(Color.TRANSPARENT);
            heartButton_layout.setBackgroundColor(Color.TRANSPARENT);

            if (propertyFragment == null){
                propertyFragment = new PropertyFragment(this);

                addFragment(propertyFragment, PropertyFragment.TAG);
                locationLabel.setText(LocalPreferences.getLocationAddress(this));
                back_ditch.setVisibility(View.INVISIBLE);
                logo_ditch.setVisibility(View.VISIBLE);
                if(!LocalPreferences.getUserToken(this).isEmpty())
                {
                    findViewById(R.id.btn_side_menu).setVisibility(View.VISIBLE);
                }
            }else{
                showFragment(propertyFragment);
                if (propertyFragment.mapFragment != null){
                    if (propertyFragment.mapFragment.propertyDetailFragment != null)
                        propertyFragment.mapFragment.propertyDetailFragment.initSatelliteMap();
                }
                else if (propertyFragment.propertyDetailFragment != null)
                    propertyFragment.propertyDetailFragment.initSatelliteMap();

            }


        }
        else if (current_layout == MESSAGE_LAYOUT_IDX)
        {
            messageButton_layout.setBackgroundColor(Color.rgb(56, 157, 195));

            homeButton_layout.setBackgroundColor(Color.TRANSPARENT);
            heartButton_layout.setBackgroundColor(Color.TRANSPARENT);

            if (messageFragment == null){
                messageFragment = new MessageFragment(this);
                addFragment(messageFragment, MessageFragment.TAG);
                back_ditch.setVisibility(View.INVISIBLE);
                logo_ditch.setVisibility(View.VISIBLE);
                if(!LocalPreferences.getUserToken(this).isEmpty())
                {
                    findViewById(R.id.btn_side_menu).setVisibility(View.VISIBLE);
                }
            }else{
                showFragment(messageFragment);
                if (messageFragment.demoFragment != null){
                    if (messageFragment.demoFragment.propertyDetailFragment != null)
                        messageFragment.demoFragment.propertyDetailFragment.initSatelliteMap();

                    logo_ditch.setVisibility(View.INVISIBLE);
                    back_ditch.setVisibility(View.VISIBLE);
                }
                else if (messageFragment.docFragment != null){
                    if (messageFragment.docFragment.propertyDetailFragment != null)
                        messageFragment.docFragment.propertyDetailFragment.initSatelliteMap();

                    logo_ditch.setVisibility(View.INVISIBLE);
                    back_ditch.setVisibility(View.VISIBLE);
                }
                else if (messageFragment.followupFragment != null){
                    if (messageFragment.followupFragment.propertyDetailFragment != null)
                        messageFragment.followupFragment.propertyDetailFragment.initSatelliteMap();

                    logo_ditch.setVisibility(View.INVISIBLE);
                    back_ditch.setVisibility(View.VISIBLE);
                }
                else if (messageFragment.propertyDetailFragment != null) {
                    messageFragment.propertyDetailFragment.initSatelliteMap();

                    logo_ditch.setVisibility(View.INVISIBLE);
                    back_ditch.setVisibility(View.VISIBLE);
                }
            }
        }
        else if (current_layout == HEART_LAYOUT_IDX)
        {
            heartButton_layout.setBackgroundColor(Color.rgb(56, 157, 195));
            homeButton_layout.setBackgroundColor(Color.TRANSPARENT);
            messageButton_layout.setBackgroundColor(Color.TRANSPARENT);

            if (favoriteFragment == null){
                favoriteFragment = new FavoriteFragment(this);
                addFragment(favoriteFragment, FavoriteFragment.TAG);
                back_ditch.setVisibility(View.INVISIBLE);
                logo_ditch.setVisibility(View.VISIBLE);

                if(!LocalPreferences.getUserToken(this).isEmpty())
                {
                    findViewById(R.id.btn_side_menu).setVisibility(View.VISIBLE);
                }
            }else{
                showFragment(favoriteFragment);
                if (favoriteFragment.propertyDetailFragment != null)
                    favoriteFragment.propertyDetailFragment.initSatelliteMap();
            }
        }
    }

    public void showFragment(Fragment selectedFragment) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(propertyFragment != null)
            ft.hide(propertyFragment);
        if(messageFragment != null)
            ft.hide(messageFragment);
        if(favoriteFragment != null)
            ft.hide(favoriteFragment);

        ft.show(selectedFragment);
        ft.commit();

        setSatelliteState();
    }

    public void setSatelliteState(){
        if (propertyFragment != null) {
            if (propertyFragment.propertyDetailFragment != null) {
                propertyFragment.propertyDetailFragment.destroySatelliteMap();
            } else if (propertyFragment.mapFragment != null) {
                if (propertyFragment.mapFragment.propertyDetailFragment != null) {
                    propertyFragment.mapFragment.propertyDetailFragment.destroySatelliteMap();
                }
            }
        }
        if (messageFragment != null){

            if (messageFragment.propertyDetailFragment != null){
                messageFragment.propertyDetailFragment.destroySatelliteMap();
            }
            else if(messageFragment.docFragment != null){
                if (messageFragment.docFragment.propertyDetailFragment != null) {
                    messageFragment.docFragment.propertyDetailFragment.destroySatelliteMap();
                }
            }
            else if(messageFragment.followupFragment != null){
                if (messageFragment.followupFragment.propertyDetailFragment != null) {
                    messageFragment.followupFragment.propertyDetailFragment.destroySatelliteMap();
                }
            }
            else if(messageFragment.demoFragment != null){
                if (messageFragment.demoFragment.propertyDetailFragment != null) {
                    messageFragment.demoFragment.propertyDetailFragment.destroySatelliteMap();
                }
            }
        }

        if (favoriteFragment != null){

            if (favoriteFragment.propertyDetailFragment != null){
                favoriteFragment.propertyDetailFragment.destroySatelliteMap();
            }
        }
    }

    public void addFragment(Fragment newFragment, String tag) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.ly_property, newFragment);
        ft.addToBackStack(tag);
        ft.commit();
    }

    public void replaceFragment(Fragment newFragment, String tag) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.ly_property, newFragment);
        ft.addToBackStack(tag);
        ft.commit();
    }
    private void setupViewsIfSignedUp() {
        if(!LocalPreferences.getUserToken(this).isEmpty())
        {
            setupSideMenu();
            findViewById(R.id.btn_side_menu).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_side_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    menuToggle.toggleDrawer();

                }
            });
        }
        tabbar_layout.setVisibility(View.GONE);
        if(!LocalPreferences.getUserToken(this).isEmpty())
        {
            tabbar_layout.setVisibility(View.VISIBLE);
        }
    }

    private void setupSideMenu() {
        SideMenuCreator menuCreator = new SideMenuCreator(this, findViewById(R.id.activity_main));
        menuToggle = menuCreator.getMenuToggle();

        menuCreator.setOnMenuClickListener(new SideMenuCreator.OnMenuClickListener() {
            @Override
            public void onMenuClicked(int resId) {
                findViewById(R.id.btn_near_my_location).setVisibility(View.VISIBLE);

                switch (resId) {
                    case R.id.my_details:
                        break;
                    case R.id.my_rent:
                        break;
                    case R.id.my_search_agent:
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                        if (propertyFragment != null) {
//                            ft.remove(propertyFragment).commit();
                            propertyFragment = null;
                        }if (messageFragment != null) {
//                            ft.remove(messageFragment).commit();

                            messageFragment = null;
                        }
                        if (favoriteFragment != null){
//                            ft.remove(favoriteFragment).commit();

                            favoriteFragment = null;
                        }
                        if (myDitchFragment != null){
//                            ft.remove(myDitchFragment).commit();
                            myDitchFragment = null;
                        }

                        ((HNTextView)findViewById(R.id.btn_near_my_location)).setText("Search Agent");
                        SearchAgentFragment fragment = new SearchAgentFragment();
                        replaceFragment(fragment, SearchAgentFragment.TAG);

                        break;
                    case R.id.ditch_my_space:
                        break;
                    case R.id.my_ditch:
                        if (propertyFragment != null)
                            propertyFragment = null;
                        if (messageFragment != null)
                            messageFragment = null;
                        if (favoriteFragment != null)
                            favoriteFragment = null;
                        if (myDitchFragment != null)
                            myDitchFragment = null;

                        ((HNTextView)findViewById(R.id.btn_near_my_location)).setText("My Ditch");
                        myDitchFragment = new MyDitchFragment(MainActivity.this);
                        replaceFragment(myDitchFragment, MyDitchFragment.TAG);
                        break;
                    case R.id.technical_support:
                        if (propertyFragment != null)
                            propertyFragment = null;
                        if (messageFragment != null)
                            messageFragment = null;
                        if (favoriteFragment != null)
                            favoriteFragment = null;

                        ((HNTextView)findViewById(R.id.btn_near_my_location)).setText("Technical Support");
                        SupportFragment supportFragment = new SupportFragment(MainActivity.this);
                        replaceFragment(supportFragment, SupportFragment.TAG);

                        break;
                    case R.id.feedback:
                        if (propertyFragment != null)
                            propertyFragment = null;
                        if (messageFragment != null)
                            messageFragment = null;
                        if (favoriteFragment != null)
                            favoriteFragment = null;

                        ((HNTextView)findViewById(R.id.btn_near_my_location)).setText("Feedback");
                        FeedbackFragment feedbackFragment = new FeedbackFragment(MainActivity.this);
                        replaceFragment(feedbackFragment, FeedbackFragment.TAG);
                        break;

                    case R.id.privacy_policy:
                        if (propertyFragment != null)
                            propertyFragment = null;
                        if (messageFragment != null)
                            messageFragment = null;
                        if (favoriteFragment != null)
                            favoriteFragment = null;

                        ((HNTextView)findViewById(R.id.btn_near_my_location)).setText("Privacy Policy");
                        PrivacyFragment privacyFragment = new PrivacyFragment(MainActivity.this);
                        replaceFragment(privacyFragment, PrivacyFragment.TAG);
                        break;
                    case R.id.term_of_service:
                        if (propertyFragment != null)
                            propertyFragment = null;
                        if (messageFragment != null)
                            messageFragment = null;
                        if (favoriteFragment != null)
                            favoriteFragment = null;

                        ((HNTextView)findViewById(R.id.btn_near_my_location)).setText("Terms of Service");
                        TermsOfServiceFragment tosFragment = new TermsOfServiceFragment(MainActivity.this);
                        replaceFragment(tosFragment, TermsOfServiceFragment.TAG);
                        break;

                }
            }
        });
    }


    @Override
    public void onBackPressed() {

        onClick(findViewById(R.id.img_back_ditch));
    }


    public JsonObject getJsonObjectFromJSON(JSONObject data)
    {
        String jsonString = data.toString();
        Gson gson = new Gson();

        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        return jsonObject;
    }




    @Override
    protected void onStart() {
        super.onStart();
        setupViewsIfSignedUp();

    }


    @Override
    public void onClick(View view) {
        if (isDoubleClick())
            return;
        switch (view.getId()) {
            case R.id.btn_near_my_location:
                if (propertyFragment != null)
                    propertyFragment.getPropertyData();
                break;
            case R.id.btn_side_menu:
                menuToggle.toggleDrawer();
                break;
            case R.id.img_back_ditch:
                if (propertyFragment != null && current_layout == PROPERTY_LAYOUT_IDX){
                    if (propertyFragment.mapFragment != null){
                        if (propertyFragment.mapFragment.propertyDetailFragment != null) {
                            propertyFragment.mapFragment.propertyDetailFragment.getFragmentManager().popBackStack();
                            propertyFragment.mapFragment.propertyDetailFragment = null;
                            if(!LocalPreferences.getUserToken(this).isEmpty())
                                findViewById(R.id.btn_side_menu).setVisibility(View.VISIBLE);
                            propertyFragment.mapFragment.showOtherFragment = false;

                        }
                    }
                    else {
                        if (propertyFragment.propertyDetailFragment != null) {
                            propertyFragment.propertyDetailFragment.getFragmentManager().popBackStack();
                            LocalPreferences.saveFlick(MainActivity.this, false);
                            LocalPreferences.saveStreetShowing(MainActivity.this, false);
                            propertyFragment.propertyDetailFragment = null;
                            if(!LocalPreferences.getUserToken(this).isEmpty())
                                findViewById(R.id.btn_side_menu).setVisibility(View.VISIBLE);
                            propertyFragment.showOtherFragment = false;

                        }
                    }

                    locationLabel.setText(LocalPreferences.getLocationAddress(MainActivity.this));
                    back_ditch.setVisibility(View.INVISIBLE);
                    logo_ditch.setVisibility(View.VISIBLE);
                }

                else if (messageFragment != null && current_layout == MESSAGE_LAYOUT_IDX){
                    if (messageFragment.demoFragment != null){
                        if (messageFragment.demoFragment.propertyDetailFragment != null) {
                            messageFragment.demoFragment.propertyDetailFragment.getFragmentManager().popBackStack();
                            messageFragment.demoFragment.propertyDetailFragment = null;

                            locationLabel.setText("");
                            messageFragment.demoFragment.showOtherFragment = false;
                            back_ditch.setVisibility(View.VISIBLE);
                            logo_ditch.setVisibility(View.INVISIBLE);
                        }
                        else{
                            messageFragment.demoFragment.getFragmentManager().popBackStack();
                            messageFragment.demoFragment= null;

                            locationLabel.setText("Messages");
                            back_ditch.setVisibility(View.INVISIBLE);
                            logo_ditch.setVisibility(View.VISIBLE);

                        }
                    }
                    else if (messageFragment.followupFragment != null){
                        if (messageFragment.followupFragment.propertyDetailFragment != null) {
                            messageFragment.followupFragment.propertyDetailFragment.getFragmentManager().popBackStack();
                            messageFragment.followupFragment.propertyDetailFragment = null;

                            locationLabel.setText("");
                            messageFragment.followupFragment.showOtherFragment = false;
                            back_ditch.setVisibility(View.VISIBLE);
                            logo_ditch.setVisibility(View.INVISIBLE);
                        }
                        else{
                            messageFragment.followupFragment.getFragmentManager().popBackStack();
                            messageFragment.followupFragment= null;

                            locationLabel.setText("Messages");
                            back_ditch.setVisibility(View.INVISIBLE);
                            logo_ditch.setVisibility(View.VISIBLE);
                            tabbar_layout.setVisibility(View.VISIBLE);
                        }
                    }
                    else if (messageFragment.docFragment != null){
                        if (messageFragment.docFragment.propertyDetailFragment != null) {
                            messageFragment.docFragment.propertyDetailFragment.getFragmentManager().popBackStack();
                            messageFragment.docFragment.propertyDetailFragment = null;
                            messageFragment.docFragment.showOtherFragment = false;

                            locationLabel.setText("");
                            back_ditch.setVisibility(View.VISIBLE);
                            logo_ditch.setVisibility(View.INVISIBLE);
                        }
                        else{
                            messageFragment.docFragment.getFragmentManager().popBackStack();
                            messageFragment.docFragment= null;

                            locationLabel.setText("Messages");
                            back_ditch.setVisibility(View.INVISIBLE);
                            logo_ditch.setVisibility(View.VISIBLE);

                        }
                    }
                    else if (messageFragment.propertyDetailFragment != null)
                    {
                        messageFragment.propertyDetailFragment.getFragmentManager().popBackStack();
                        LocalPreferences.saveFlick(MainActivity.this, false);
                        LocalPreferences.saveStreetShowing(MainActivity.this, false);
                        messageFragment.propertyDetailFragment = null;
                        if(!LocalPreferences.getUserToken(this).isEmpty())
                            findViewById(R.id.btn_side_menu).setVisibility(View.VISIBLE);

                        locationLabel.setText("Messages");
                        messageFragment.showOtherFragment = false;
                        back_ditch.setVisibility(View.INVISIBLE);
                        logo_ditch.setVisibility(View.VISIBLE);
                    }

                    messageFragment.getMessageInfos(false);
                }

                else if (favoriteFragment != null && current_layout == HEART_LAYOUT_IDX){
                    if (favoriteFragment.propertyDetailFragment != null)
                    {
                        favoriteFragment.propertyDetailFragment.getFragmentManager().popBackStack();
                        LocalPreferences.saveFlick(MainActivity.this, false);
                        LocalPreferences.saveStreetShowing(MainActivity.this, false);

                        favoriteFragment.propertyDetailFragment = null;
                        if(!LocalPreferences.getUserToken(this).isEmpty())
                            findViewById(R.id.btn_side_menu).setVisibility(View.VISIBLE);

                    }

                    locationLabel.setText("Favs & Nopes");
                    favoriteFragment.showOtherFragment = false;
                    back_ditch.setVisibility(View.INVISIBLE);
                    logo_ditch.setVisibility(View.VISIBLE);
                }
                else if(myDitchFragment != null)
                {
                    if (myDitchFragment.propertyDetailFragment != null)
                    {
                        myDitchFragment.propertyDetailFragment.getFragmentManager().popBackStack();
                        LocalPreferences.saveFlick(MainActivity.this, false);
                        LocalPreferences.saveStreetShowing(MainActivity.this, false);

                        myDitchFragment.propertyDetailFragment = null;
                        if(!LocalPreferences.getUserToken(this).isEmpty())
                            findViewById(R.id.btn_side_menu).setVisibility(View.VISIBLE);

                    }

                    locationLabel.setText("My Ditch");
                    myDitchFragment.showOtherFragment = false;
                    back_ditch.setVisibility(View.INVISIBLE);
                    logo_ditch.setVisibility(View.VISIBLE);
                }

                break;
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

    public void showSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.stay, R.anim.slide_in_up);

    }

    public void showRequestInfoView(String id) {

        Intent intent = new Intent(this, RequestInfoActivity.class);
        intent.putExtra(Constants.EXTRA_PROPERTY_ID, id);
        intent.putExtra(Constants.EXTRA_REQUEST_TYPE, 2);

        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

//    public void showPropertyDetail(String propertyJson, int id, Fragment fragment) {
//        String currentLocation = LocalPreferences.getCurrentLocation(this);
//        propertyDetailFragment = new PropertyDetailFragment();
//        propertyDetailFragment.getDataFromIntent(propertyJson, currentLocation);
//
//        FragmentTransaction ft = fragment.getChildFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
//
//        ft.add(id, propertyDetailFragment);
//        ft.addToBackStack(MapFragment.TAG);
//        ft.commit();
//
//        logo_ditch.setVisibility(View.INVISIBLE);
//        back_ditch.setVisibility(View.VISIBLE);
//    }



    public void openUrl(String url)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public void updateLocationLabel(Location location){
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0){
            String address = addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();

            String[] splites = address.split(", ");
            if (splites.length > 0)
                city = splites[0];

            if (splites.length > 1){
                String[] spliteStates = splites[1].split(" ");
                state = spliteStates[0];
            }

            if (state == null){
                state = "";
            }
            if (city != null && state != null)
                locationLabel.setText(city + ", " + state);
            LocalPreferences.saveLocationAddress(MainActivity.this, locationLabel.getText().toString());

        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK) {
//            String json = data.getStringExtra(Constants.EXTRA_PROPERTY_JSON);
//            String origin = data.getStringExtra("origin");
//            showPropertyDetail(json, origin);
//            // do something with B's return values
//        }
//    }
}
