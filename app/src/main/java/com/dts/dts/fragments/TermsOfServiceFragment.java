package com.dts.dts.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.activities.MainActivity;
import com.dts.dts.adapters.FavoriteRecycleAdapter;
import com.dts.dts.animation.ProgressHUD;
import com.dts.dts.models.APIPropertyList;
import com.dts.dts.models.Property;
import com.dts.dts.remote.APIRequestManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TermsOfServiceFragment extends Fragment{
    public static final String TAG = TermsOfServiceFragment.class.getSimpleName();

    private Context mContext;

    private WebView termsOfServiceWebView;
    private ProgressHUD hud;

    public TermsOfServiceFragment(Context conext) {
        // Required empty public constructor
        mContext = conext;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.termsofservice_fragment, container, false);
        termsOfServiceWebView = (WebView) rootView.findViewById(R.id.terms_service);
        getTermsOfService();
        return rootView;
    }

    private void getTermsOfService()
    {
        hud = ProgressHUD.show(mContext, "Loading...", true, true, null);
        APIRequestManager apiRequestManager = new APIRequestManager(mContext, null);

        apiRequestManager.addToRequestQueue(apiRequestManager.getTermsOfService(myHandler), false);
    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int type = msg.arg1;
            hud.dismiss();
            JSONObject response = (JSONObject) msg.obj;
            if (response == null) {
                return;
            }


            try {
                boolean isSuccess = response.getBoolean("success");
                if (isSuccess) {
                    String url = response.getString("data");
                    url = "<font face='Arial'>" + url + "</font>";
                    termsOfServiceWebView.loadData(url, "text/html; charset=UTF-8", null);
                    WebSettings webSettings = termsOfServiceWebView.getSettings();
                    webSettings.setDefaultFontSize(8);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}
