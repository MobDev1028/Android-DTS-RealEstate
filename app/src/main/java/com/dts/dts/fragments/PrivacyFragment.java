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

import com.dts.dts.R;
import com.dts.dts.animation.ProgressHUD;
import com.dts.dts.remote.APIRequestManager;

import org.json.JSONException;
import org.json.JSONObject;


public class PrivacyFragment extends Fragment{
    public static final String TAG = PrivacyFragment.class.getSimpleName();

    private Context mContext;

    private WebView privacyWebView;
    private ProgressHUD hud;

    public PrivacyFragment(Context conext) {
        // Required empty public constructor
        mContext = conext;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.privacy_fragment, container, false);
        privacyWebView = (WebView) rootView.findViewById(R.id.privacy);
        getPrivacyPolicy();
        return rootView;
    }

    private void getPrivacyPolicy()
    {
        hud = ProgressHUD.show(mContext, "Loading...", true, true, null);
        APIRequestManager apiRequestManager = new APIRequestManager(mContext, null);

        apiRequestManager.addToRequestQueue(apiRequestManager.getPrivacy(myHandler), false);
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
                    privacyWebView.loadData(url, "text/html; charset=UTF-8", null);
                    WebSettings webSettings = privacyWebView.getSettings();
                    webSettings.setDefaultFontSize(8);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}
