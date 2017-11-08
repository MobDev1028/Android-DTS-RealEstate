package com.dts.dts.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.activities.MainActivity;
import com.dts.dts.adapters.FollowUpAdapter;
import com.dts.dts.animation.ProgressHUD;
import com.dts.dts.remote.APIRequestManager;
import com.dts.dts.views.CustomEditText;
import com.dts.dts.views.DelayAutoCompleteTextView;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by silver on 12/25/16.
 */

public class FollowupFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, APIRequestManager.APIRequestListener {
    public static final String TAG = FollowupFragment.class.getSimpleName();

    private ImageButton btnProperty;
    private TextView tvSubject;
    private TextView tvAddress;
    private TextView tvCountry;
    private ListView followListView;

    private CustomEditText etMessage;
    private TextView btnSend;

    private String messageContent;

    private JSONObject dictSelectedMessage;
    private JSONObject dictProperty;

    private ArrayList<JSONObject> allMessages = new ArrayList<>();

    private Context mContext;

    private FollowUpAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private APIRequestManager apiRequestManager;

    private int keyboardHeight;

    private View rootView;

    private ProgressHUD hud;

    public FollowupFragment(JSONObject dm, JSONObject dp, Context context)
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

        rootView = inflater.inflate(R.layout.follow_up, container, false);

        keyboardHeight = 0;

        btnProperty = (ImageButton) rootView.findViewById(R.id.btn_property);
        tvSubject = (TextView) rootView.findViewById(R.id.tv_subject);
        tvAddress = (TextView) rootView.findViewById(R.id.tv_address);
        tvCountry = (TextView) rootView.findViewById(R.id.tv_country);

        etMessage = (CustomEditText) rootView.findViewById(R.id.et_message);

        etMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = etMessage.hasFocusable();
                etMessage.setFocusable(true);
                etMessage.setFocusableInTouchMode(true);

                RelativeLayout messagebar = (RelativeLayout) rootView.findViewById(R.id.message_bar);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messagebar.getLayoutParams();
                if (keyboardHeight > 0) {
                    params.setMargins(0, 0, 0, (int)(keyboardHeight * getActivity().getResources().getDisplayMetrics().density));
                    messagebar.setLayoutParams(params);
                }
            }
        });

        etMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
//                checkKeyboardHeight(rootView);
                RelativeLayout messagebar = (RelativeLayout) rootView.findViewById(R.id.message_bar);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messagebar.getLayoutParams();
                if (b == true){
                    if (keyboardHeight > 0) {
                        float n = getActivity().getResources().getDisplayMetrics().density;
                        params.setMargins(0, 0, 0, (int)(keyboardHeight * getActivity().getResources().getDisplayMetrics().density));
//                        params.setMargins(0, 0, 0, (int)(800));

                        messagebar.setLayoutParams(params);
                    }
                }
                else{
                    params.setMargins(0, 0, 0, 0);
                    messagebar.setLayoutParams(params);

                    etMessage.setFocusable(true);
                    etMessage.setFocusableInTouchMode(true);
                }
            }
        });

        etMessage.setOnEditTextImeBackListener(new CustomEditText.EditTextImeBackListener() {
            @Override
            public void onImeBack(CustomEditText ctrl, String text) {
                etMessage.setFocusable(false);
            }
        });



        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (getActivity() != null){
                    int h1 = rootView.getRootView().getHeight();
                    int h2 = rootView.getHeight() - (int)(50 * getActivity().getResources().getDisplayMetrics().density);
                    keyboardHeight = h1 - h2 - 60;
                }
            }
        });

        btnSend = (TextView) rootView.findViewById(R.id.send_button);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();


            }
        });

        followListView = (ListView) rootView.findViewById(R.id.follow_listview);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

//        swipeRefreshLayout.setOnRefreshListener(this);



        mAdapter = new FollowUpAdapter(mContext, allMessages);
        followListView.setAdapter(mAdapter);


        apiRequestManager = new APIRequestManager(mContext, this);

        populateFields();
        try {
            if(dictSelectedMessage.getString("type").equalsIgnoreCase("inquire")) {
                rootView.findViewById(R.id.message_bar).setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        allMessages.add(dictSelectedMessage);

        populateMessages();
        sortMessages();

        fillRecyclerView();

        btnProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPropertyClick();
            }
        });

        setupUI(rootView);

        return rootView;
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (v == btnSend){
//                        sendMessage();
                        return false;
                    }
                    RelativeLayout messagebar = (RelativeLayout) rootView.findViewById(R.id.message_bar);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messagebar.getLayoutParams();
                    params.setMargins(0, 0, 0, 0);
                    messagebar.setLayoutParams(params);

                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
    @Override
    public void onResume(){
        if (rootView != null) {
            RelativeLayout messagebar = (RelativeLayout) rootView.findViewById(R.id.message_bar);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messagebar.getLayoutParams();

            params.setMargins(0, 0, 0, 0);
            messagebar.setLayoutParams(params);

            etMessage.setFocusable(true);
            etMessage.setFocusableInTouchMode(true);
            ((MainActivity) getActivity()).tabbar_layout.setVisibility(View.GONE);

        }

        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        InputMethodManager inputMethodManager =
                (InputMethodManager) getContext().getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                getActivity().getCurrentFocus().getWindowToken(), 0);
    }


    private void populateMessages()
    {
        try {
            if (dictSelectedMessage.getJSONArray("children").length() > 0)
            {
                JSONArray childrenMessages = dictSelectedMessage.getJSONArray("children");
                for (int i = 0; i < childrenMessages.length(); i++)
                {
                    JSONObject dict = childrenMessages.getJSONObject(i);
                    JSONObject dictChildMsg = dict;
                    allMessages.add(dictChildMsg);

                    JSONArray grandChildrenMessags = dictChildMsg.getJSONArray("children");
                    if (grandChildrenMessags.length() > 0) {
                        for (int j = 0; j < grandChildrenMessags.length(); j++)
                        {
                            JSONObject dictGrandChildMsg = grandChildrenMessags.getJSONObject(j);
                            allMessages.add(dictGrandChildMsg);

                        }
                    }
                }

            }

        }
        catch (JSONException e1) {
                e1.printStackTrace();
        }
    }

    private void sortMessages() {
        for (int i = 0; i < allMessages.size(); i++) {
            for (int j = i+1; j < allMessages.size(); j++) {
                JSONObject target = allMessages.get(i);
                JSONObject other = allMessages.get(j);
                try {
                    if (target.getString("updated_at").compareTo(other.getString("updated_at")) == 1) {
                        allMessages.set(i, other);
                        allMessages.set(j, target);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void populateFields() {
        try {
            tvSubject.setTextColor(Color.parseColor("#ff0500"));

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
                tvSubject.setTextColor(Color.parseColor("#02ce37"));

            }
            else{
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

        }
        catch (JSONException e1) {
            e1.printStackTrace();
        }

    }

    private void fillRecyclerView() {
        mAdapter = new FollowUpAdapter(mContext, allMessages);
        followListView.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
    }



    public void onRefresh() {
        fetchMovies();
    }

    public void fetchMovies()
    {
        try {
            apiRequestManager.addToRequestQueue(apiRequestManager.getMessageList(myHandler, dictSelectedMessage.getString("id")), false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(){

        double recipientID = 0;
        double messageID = 0;
        try {
            recipientID = dictSelectedMessage.getDouble("sender_id");
            messageID = dictSelectedMessage.getDouble("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageContent = etMessage.getText().toString();

        etMessage.setText("");
        hud = ProgressHUD.show(mContext, "Sending...", true, true, null);

        apiRequestManager.addToRequestQueue(apiRequestManager.sendMessage(myHandler, recipientID, messageContent, messageID), false);

        InputMethodManager inputMethodManager =
                (InputMethodManager) getContext().getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                getActivity().getCurrentFocus().getWindowToken(), 0);

        RelativeLayout messagebar = (RelativeLayout) rootView.findViewById(R.id.message_bar);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messagebar.getLayoutParams();

        params.setMargins(0, 0, 0, 0);
        messagebar.setLayoutParams(params);

        etMessage.setFocusable(true);
        etMessage.setFocusableInTouchMode(true);
    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            apiRequestManager.hideProgressDialog();
            int type = msg.arg1;

            JSONObject response = (JSONObject) msg.obj;


            switch (type) {
                case Constants.MESSAGE_LIST:

                    if (response == null)
                        break;

                    String message = null;
                    try {

                        message = response.getString("message");
                        JSONObject data = response.getJSONObject("data");
                        JSONArray threads = data.getJSONArray("thread");
                        JSONArray msgs = threads.getJSONObject(0).getJSONArray("msgs");
                        dictSelectedMessage = msgs.getJSONObject(0);
                        allMessages.clear();
                        allMessages.add(dictSelectedMessage);
                        populateMessages();

                        swipeRefreshLayout.setRefreshing(false);

                        fillRecyclerView();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (message != null && !message.isEmpty())
                        apiRequestManager.showDefaultPopup("", message);


                    fillRecyclerView();
                    break;

                case Constants.SEND_MESSAGE:
                    hud.dismiss();
                    if (response == null)
                        break;
                    try {
                        boolean isSuccess = response.getBoolean("success");
                        if (isSuccess == false){
                            apiRequestManager.showDefaultPopup("", response.getString("message"));
                            return;
                        }

                        JSONObject object = new JSONObject();
                        object.put("recipient_id", 1);
                        object.put("content", messageContent);
                        object.put("updated_at_formatted", "1 seconds ago");

                        allMessages.add(object);

                        swipeRefreshLayout.setRefreshing(false);

//                        fillRecyclerView();
                        mAdapter.notifyDataSetChanged();
                        followListView.smoothScrollToPosition(allMessages.size()-1);

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


}
