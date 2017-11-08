package com.dts.dts.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.dts.dts.Constants;
import com.dts.dts.activities.MainActivity;
import com.dts.dts.activities.RequestInfoActivity;
import com.dts.dts.activities.SearchActivity;

import org.json.JSONObject;

import java.util.Map;

public class LocalPreferences {

    private static final String KEY_IS_FROM_MAP_ACTIVITY = "is_from_map_activity";
    private static final String KEY_SEARCH_CRITERIA = "search_criteria";
    private static final String KEY_SEARCH_NOW_FLAG = "search_now_flag";
    private static final String KEY_SEARCH_AGENT_FLAG = "search_agent_flag";
    private static final String KEY_USER_TOKEN = "user_token";
    private static final String KEY_CID = "cid";
    private static final String KEY_SELECTED_REGION = "selectedRegion";
    private static final String KEY_SELECTED_LAT = "selectedLat";
    private static final String KEY_SELECTED_LONG = "selectedLong";
    private static final String KEY_LOCATION_ADDRESS = "location_address";
    private static final String  KEY_CURRENT_LOCATION = "current_location";
    private static final String KEY_IS_FLICK = "flick";
    private static final String KEY_IS_STREET = "street";

    private String KEY_USER_FULL_NAME = "user_full_name";
    private String KEY_USER_ID = "userId";

    private SharedPreferences preferences;

    private LocalPreferences(Context context) {
        preferences = context.getSharedPreferences("ditchPreferences", Context.MODE_PRIVATE);
    }

    private SharedPreferences getPreferences() {
        return preferences;
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        LocalPreferences prefs = new LocalPreferences(context);
        SharedPreferences preferences = prefs.getPreferences();
        return preferences.edit();
    }

    private static SharedPreferences newPreferencesInstance(Context context) {
        LocalPreferences prefs = new LocalPreferences(context);
        return prefs.getPreferences();
    }

    public static void saveFlagIsFromMapActivity(Context context, Boolean flag) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(KEY_IS_FROM_MAP_ACTIVITY, flag);
        editor.apply();
    }

    public static void saveLocationAddress(Context context, String address){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(KEY_LOCATION_ADDRESS, address);
        editor.apply();

    }

    public static void saveSearchCriteria(Context context, String jsonParams) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(KEY_SEARCH_CRITERIA, jsonParams);
        editor.apply();
    }
    public static void saveSearchSchedule(Context context, String schedule) {
        SharedPreferences.Editor editor = getEditor(context);

        // use the name as the key, and the icon as the value
        editor.putString("schedule", schedule);
        editor.apply();
    }


    public static void saveFlagSearchNow(Context context, boolean flag) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(KEY_SEARCH_NOW_FLAG, flag);
        editor.apply();
    }

    public static void saveFlick(Context context, boolean flag) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(KEY_IS_FLICK, flag);
        editor.apply();
    }
    public static void saveStreetShowing(Context context, boolean flag) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(KEY_IS_STREET, flag);
        editor.apply();
    }

    public static void saveFlagSearchAgent(Context context, boolean flag) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(KEY_SEARCH_AGENT_FLAG, flag);
        editor.apply();
    }

    public static void saveUserToken(Context context, String token) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(KEY_USER_TOKEN, token);
        editor.apply();
    }

    public static void saveUserCid(Context context, String cid)
    {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(KEY_CID, cid);
        editor.apply();
    }

    public static String getCurrentLocation(Context context) {
        SharedPreferences preferences = newPreferencesInstance(context);
        return preferences.getString(KEY_CURRENT_LOCATION, "");
    }

    public static String getLocationAddress(Context context) {
        SharedPreferences preferences = newPreferencesInstance(context);
        return preferences.getString(KEY_LOCATION_ADDRESS, "Near me");
    }

    public static boolean getIsFlick(Context context) {
        SharedPreferences preferences = newPreferencesInstance(context);
        return preferences.getBoolean(KEY_IS_FLICK, false);
    }
    public static boolean getIsStreet(Context context) {
        SharedPreferences preferences = newPreferencesInstance(context);
        return preferences.getBoolean(KEY_IS_STREET, false);
    }
    public static boolean getFlagIsFromMapActivity(Context context) {
        SharedPreferences preferences = newPreferencesInstance(context);
        return preferences.getBoolean(KEY_IS_FROM_MAP_ACTIVITY, false);
    }

    public static String getSearchCriteria(Context context) {
        SharedPreferences preferences = newPreferencesInstance(context);
        return preferences.getString(KEY_SEARCH_CRITERIA, "");
    }

    public static String getSearchSchedule(Context context) {
        SharedPreferences preferences = newPreferencesInstance(context);
        return preferences.getString("schedule", "");
    }

    public static boolean getFlagSearchNow(Context context) {
        SharedPreferences preferences = newPreferencesInstance(context);
        return preferences.getBoolean(KEY_SEARCH_NOW_FLAG, false);
    }

    public static boolean getSearchAgentFlag(Context context) {
        SharedPreferences preferences = newPreferencesInstance(context);
        return preferences.getBoolean(KEY_SEARCH_AGENT_FLAG, false);
    }

    public static String getUserToken(Context context) {
        SharedPreferences preferences = newPreferencesInstance(context);
        return preferences.getString(KEY_USER_TOKEN, "");
    }

    public static String getUserCid(Context context) {
        SharedPreferences preferences = newPreferencesInstance(context);
        return preferences.getString(KEY_CID, "");
    }

    public static String getSelectedRegion(Context context) {
        SharedPreferences preferences = newPreferencesInstance(context);
        return preferences.getString(KEY_SELECTED_REGION, "");
    }

    public static Location getSelectedLat(Context context) {
        SharedPreferences preferences = newPreferencesInstance(context);
        long lati = preferences.getLong(KEY_SELECTED_LAT, 0);
        long longi = preferences.getLong(KEY_SELECTED_LONG, 0);
        Location location = new Location("dummyprovider");
        location.setLatitude(lati);
        location.setLongitude(longi);

        return location;
    }

    public static void saveSelectedRegion(Context context, String token) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(KEY_SELECTED_REGION, token);
        editor.apply();
    }

    public static void saveCurrentLocation(Context context, String curLocation){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(KEY_CURRENT_LOCATION, curLocation);
        editor.apply();
    }

    public static void saveSelectedLat(Context context, Location location) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putLong(KEY_SELECTED_LAT, (long)location.getLatitude());
        editor.putLong(KEY_SELECTED_LONG, (long)location.getLongitude());
        editor.apply();
    }
}
