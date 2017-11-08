package com.dts.dts.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.activities.MainActivity;
import com.dts.dts.models.FileDownloader;
import com.dts.dts.remote.APIRequestManager;
import com.dts.dts.utils.LocalPreferences;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Created by silver on 12/26/16.
 */

public class DocFragment extends Fragment {
    public static final String TAG = DocFragment.class.getSimpleName();

    private JSONObject dictSelectedMessage;
    private JSONObject dictProperty;
    private JSONObject dictSignedResponse;

    private Context mContext;


    private ImageButton btnProperty;
    private TextView tvSubject;
    private TextView tvAddress;
    private TextView tvCountry;
    private Button btnSign;
    private Button btnDecline;
    private boolean mIsSigned;

    private WebView webView;

    private APIRequestManager apiRequestManager;

    private boolean isFromSignature;
    private String signedDoc_Id;

    public DocFragment(JSONObject dm, JSONObject dp, Context context)
    {
        dictSelectedMessage = dm;
        dictProperty = dp;
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) getActivity()).locationLabel.setText("");
        View rootView = inflater.inflate(R.layout.doc_message, container, false);

        btnProperty = (ImageButton) rootView.findViewById(R.id.btn_property);
        tvSubject = (TextView) rootView.findViewById(R.id.tv_subject);
        tvAddress = (TextView) rootView.findViewById(R.id.tv_address);
        tvCountry = (TextView) rootView.findViewById(R.id.tv_country);
        btnSign = (Button) rootView.findViewById(R.id.sign_button);
        btnDecline = (Button) rootView.findViewById(R.id.decline_button);


        btnProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPropertyClick();
            }
        });

        populateFields();

        btnSign.setVisibility(View.VISIBLE);
        btnDecline.setVisibility(View.VISIBLE);

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDecline();
            }
        });

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSign();
            }
        });

        webView = (WebView) rootView.findViewById(R.id.web_view);

        apiRequestManager = new APIRequestManager(mContext, null);

        try {
            if (dictSelectedMessage.getInt("declined") != 0) {
                btnSign.setVisibility(View.GONE);
                btnDecline.setVisibility(View.GONE);
                downloadDocument();
            } else if (!dictSelectedMessage.isNull("doc")) {
                if (dictSelectedMessage.getJSONObject("doc").getInt("signed") != 0) {
                    btnSign.setVisibility(View.GONE);
                    btnDecline.setVisibility(View.GONE);
                    downloadConfirmedSignedDocument();
                }
                else {
                    downloadDocument();
                }

            } else {
                downloadDocument();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private void downloadDocument()
    {
        try {
            String request = apiRequestManager.getDoccontent(myHandler, dictSelectedMessage.getJSONObject("doc_template").getString("filename"), dictSelectedMessage.getJSONObject("doc_template").getString("title"), "template");
            writeToPath(request, dictSelectedMessage.getJSONObject("doc_template").getString("title") + ".pdf");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void downloadConfirmedSignedDocument()
    {
//        try {
//            apiRequestManager.addToRequestQueue(apiRequestManager.getDoccontent(myHandler, dictSelectedMessage.getJSONObject("doc").getString("filename"), dictSelectedMessage.getJSONObject("doc_template").getString("title"), "signed"), false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        try {
            String request = apiRequestManager.getDoccontent(myHandler, dictSelectedMessage.getJSONObject("doc").getString("filename"), dictSelectedMessage.getJSONObject("doc_template").getString("title"), "signed");
            writeToPath(request, dictSelectedMessage.getJSONObject("doc_template").getString("title") + ".pdf");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void downloadSignedDoc(String filename, String title)
    {
        String request = apiRequestManager.getDoccontent(myHandler, filename, title, "temp");
        writeToPath(request, title + ".pdf");

    }

    private class DownloadFile extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "pdf");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return fileName;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // use result as file name

            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/pdf/" + result);  // -> filename = maven.pdf
            Uri path = Uri.fromFile(pdfFile);

            webView.loadUrl(path.getPath());
        }
    }

    public void writeToPath(String url, String filename) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.loadUrl("http://docs.google.com/gview?embedded=true&url="+url);
//        filename = filename.replace(" ", "");
//        new DownloadFile().execute(url, filename);
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

    public void onClickDecline()
    {
        declineMessage();
    }

    public void onClickSign()
    {
        if (mIsSigned == false) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();

            SignatureFragment newFragment = new SignatureFragment(dictSelectedMessage, dictProperty, getContext());
            newFragment.setOnSignedListener(new SignatureFragment.SignedListener() {
                @Override
                public void signed(JSONObject signedDic) {
                    mIsSigned = true;

                    try {
                        String signedDoc_Filename = signedDic.getString("filename");
                        String signedDoc_Title = signedDic.getString("title");
                        signedDoc_Id = signedDic.getString("id");
                        downloadSignedDoc(signedDoc_Filename, signedDoc_Title);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    btnSign.setText("CONFIRM");

                }

                @Override
                public void declined() {
                    mIsSigned = false;
                    btnSign.setText("SIGN");

                }
            });
            ft.add(R.id.main_layout, newFragment);
            ft.addToBackStack(FollowupFragment.TAG);
            ft.commit();
        }
        else {
            confirmDocSignature();
        }

    }

    private void declineMessage()
    {
        try {
            apiRequestManager.addToRequestQueue(apiRequestManager.getUpdateMsg(myHandler, String.format("%d", dictSelectedMessage.getInt("id")), "decline"), false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void confirmDocSignature()
    {
        try {
            apiRequestManager.addToRequestQueue(apiRequestManager.confirmDocSignature(myHandler, signedDoc_Id, String.format("%d", dictSelectedMessage.getInt("id"))), false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            apiRequestManager.hideProgressDialog();
            int type = msg.arg1;

            JSONObject response = (JSONObject) msg.obj;

            String message = null;

            switch (type) {
                case Constants.DOC_CONTENT:

                    if (response == null)
                        break;

                    try {
                        message = response.getString("message");
                            writeToPath(dictSelectedMessage.getJSONObject("doc_template").getString("title"), response.toString());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (message != null && !message.isEmpty())
                        apiRequestManager.showDefaultPopup("", message);

                    break;
                case Constants.UPDATE_MSG:
                    if (response == null)
                        break;

                    try {
                        message = response.getString("message");
                        getFragmentManager().popBackStack();
                        if (listener != null)
                            listener.declined();

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
                            listener.confirm();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (message != null && !message.isEmpty())
                        apiRequestManager.showDefaultPopup("", message);
                    break;
            }
        }
    };

    private DocFragmentListener listener;
    public void setOnDocListener(DocFragmentListener l)
    {
        listener = l;
    }

    public interface DocFragmentListener {
        void confirm();
        void declined();
    }
}
