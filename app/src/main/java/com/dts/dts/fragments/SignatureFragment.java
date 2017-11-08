package com.dts.dts.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.Formatter;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.activities.MainActivity;
import com.dts.dts.adapters.FollowUpAdapter;
import com.dts.dts.animation.ProgressHUD;
import com.dts.dts.remote.APIRequestManager;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.github.gcacace.signaturepad.views.SignaturePad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by silver on 12/25/16.
 */

public class SignatureFragment extends Fragment implements APIRequestManager.APIRequestListener {
    public static final String TAG = SignatureFragment.class.getSimpleName();

    private ImageButton btnProperty;
    private TextView tvSubject;
    private TextView tvAddress;
    private TextView tvCountry;
    private SignaturePad signaturePad;

    private JSONObject dictSelectedMessage;
    private JSONObject dictProperty;

    private ArrayList<JSONObject> allMessages = new ArrayList<>();

    private Context mContext;

    private FollowUpAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private APIRequestManager apiRequestManager;
    private String DEF_IP_Address  = "119.152.216.151";

    private ProgressHUD hud;

    Handler progressHandler = new Handler();
    Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            hud.dismiss();
        }
    };


    public SignatureFragment(JSONObject dm, JSONObject dp, Context context)
    {
        dictSelectedMessage = dm;
        dictProperty = dp;
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.signature_fragment, container, false);

        btnProperty = (ImageButton) rootView.findViewById(R.id.btn_property);
        tvSubject = (TextView) rootView.findViewById(R.id.tv_subject);
        tvAddress = (TextView) rootView.findViewById(R.id.tv_address);
        tvCountry = (TextView) rootView.findViewById(R.id.tv_country);

        signaturePad = (SignaturePad) rootView.findViewById(R.id.signature_pad);
        signaturePad.setPenColor(Color.RED);

        rootView.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDoubleClick())
                    return;

                signaturePad.clear();
            }
        });

        rootView.findViewById(R.id.decline_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDoubleClick())
                    return;
                declineMessage();
            }
        });

        rootView.findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDoubleClick())
                    return;
                sendSignature();
            }
        });
        apiRequestManager = new APIRequestManager(mContext, this);

        populateFields();

        btnProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPropertyClick();
            }
        });
        return rootView;
    }


    private void populateFields() {
        tvSubject.setTextColor(Color.parseColor("#ff0500"));

        try {
            if (dictSelectedMessage.getString("type").equalsIgnoreCase("doc_sign")) {
                tvSubject.setText("SIGN LEASE");
            } else if(dictSelectedMessage.getString("type").equalsIgnoreCase("follow_up")) {
                tvSubject.setText("FOLLOW UP");

            }
            else if(dictSelectedMessage.getString("type").equalsIgnoreCase("demo")){
                tvSubject.setText("ON-SITE DEMO");

            }
            else if(dictSelectedMessage.getString("type").equalsIgnoreCase("inquire")) {
                tvSubject.setText("INQUIRED");
//                self.lblSubject.textColor = UIColor(hexString:"02ce37")

            }
            else{
                tvSubject.setText(dictSelectedMessage.getString("type"));

            }

            String address1 = dictProperty.getString("address1");
            String city = dictProperty.getString("city");
            String state = dictProperty.getString("state_or_province");
            String zip = dictProperty.getString("zip");

            tvAddress.setText(address1);
            tvCountry.setText(city + ", " + state + " " + zip);

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

        }
        catch (JSONException e1) {
            e1.printStackTrace();
        }

    }


    public void declineMessage() {
        try {
            hud = ProgressHUD.show(mContext, "Processing...", true, true, null);

            apiRequestManager.addToRequestQueue(apiRequestManager.getUpdateMsg(myHandler, String.format("%d", dictSelectedMessage.getInt("id")), "decline"), false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendSignature(){
        Bitmap sigImage = signaturePad.getSignatureBitmap();
        String base64OfPic = null;
        if (sigImage != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            sigImage.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            base64OfPic = Base64.encodeToString(b, Base64.NO_WRAP);
        }

        try {
            hud = ProgressHUD.show(mContext, "Processing...", true, true, null);

            apiRequestManager.addToRequestQueue(apiRequestManager.signDoc(myHandler, base64OfPic, dictSelectedMessage.getInt("doc_template_id"), getIPAddress()), false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String getIPAddress(){
        String address = DEF_IP_Address;
        WifiManager wm = (WifiManager) getContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }
    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            apiRequestManager.hideProgressDialog();
            int type = msg.arg1;

            JSONObject response = (JSONObject) msg.obj;

            String message = null;
            boolean isSuccess = false;

            switch (type) {
                case Constants.UPDATE_MSG:
                    hud.hide();

                    if (response == null)
                        break;

                    try {
                        message = response.getString("message");
                        if (listener != null)
                            listener.declined();

                        getFragmentManager().popBackStack();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (message != null && !message.isEmpty())
                        apiRequestManager.showDefaultPopup("", message);

                    break;

                case Constants.SIGN_DOC:
                    hud.hide();
                    if (response == null)
                        break;



                    try {
                        message = response.getString("message");
                        if (message != null && !message.isEmpty())
                            apiRequestManager.showDefaultPopup("", message);
                        else{
                            JSONObject signedDic = response.getJSONObject("data");
                            if (listener != null)
                                listener.signed(signedDic);

                            getFragmentManager().popBackStack();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
            }
        }
    };

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

        ft.add(R.id.main_layout, propertyDetailFragment);
        ft.addToBackStack(PropertyDetailFragment.TAG);
        ft.commit();

        ((MainActivity) mContext).logo_ditch.setVisibility(View.INVISIBLE);
        ((MainActivity) mContext).back_ditch.setVisibility(View.VISIBLE);

//        ((MainActivity) mContext).showPropertyDetail(dictProperty.toString(), R.id.main_layout, this);
    }

    public void onNetworkRequestSuccess(APIRequestManager.APIRequestType type, JsonObject response)
    {

    }

    public void onNetworkRequestError(APIRequestManager.APIRequestType type, VolleyError error){

    }

    private SignedListener listener;
    public void setOnSignedListener(SignedListener l)
    {
        listener = l;
    }

    public interface SignedListener {
        void signed(JSONObject signedDic);
        void declined();
    }
}
