package com.dts.dts.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.remote.APIRequestManager;
import com.dts.dts.utils.LocalPreferences;
import com.dts.dts.views.HNEditText;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestInfoActivity extends AppCompatActivity implements View.OnClickListener, APIRequestManager.APIRequestListener {

    private static final String FORMAT_PHONE_NUMBER = "(###)###-####";
    private static final String FORMAT_PIN_CODE = "####";
    private HNEditText phoneEText;
    private String phoneNumber = "";
    private int numberLength;
    private int cursorPosition;
    private APIRequestManager apiRequestManager;
    private ImageView btnTextMyCode;
    private boolean isPinEntry = false;
    private String pinCode = "";
    private int requestType = 0;
    private String propertyId = "";

    private RelativeLayout termLayout;
    private RelativeLayout cidLayout;

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_info);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        apiRequestManager = new APIRequestManager(this, this);

        getDataFromIntent();

        initializeViews();
        initializeTermView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        requestType = intent.getIntExtra(Constants.EXTRA_REQUEST_TYPE, -1);
        propertyId = intent.getStringExtra(Constants.EXTRA_PROPERTY_ID);
    }

    private void initializeTermView(){
        termLayout = (RelativeLayout) findViewById(R.id.term_layout);
        cidLayout = (RelativeLayout) findViewById(R.id.cid_layout);

        webView = (WebView) findViewById(R.id.webView);

        findViewById(R.id.reject_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                termLayout.setAlpha(1f);
                cidLayout.setAlpha(0);
                termLayout.animate().alpha(0).setDuration(500).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        termLayout.setVisibility(View.GONE);
                        finish();
                    }
                });
            }
        });

        findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                termLayout.setAlpha(1f);
                termLayout.animate().alpha(0).setDuration(500).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        termLayout.setVisibility(View.GONE);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(phoneEText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });

            }
        });

        apiRequestManager.addToRequestQueue(apiRequestManager.getTermsOfService(myHandler), false);
    }

    private void initializeViews() {
        phoneEText = (HNEditText) findViewById(R.id.et_phone_number);
        phoneEText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return false;
            }
        });


        btnTextMyCode = (ImageView) findViewById(R.id.btn_txt_my_code);

        findViewById(R.id.btn_txt_my_code).setOnClickListener(this);
        findViewById(R.id.btn_close_this_window).setOnClickListener(this);
        findViewById(R.id.btn_change_number).setOnClickListener(this);

        btnTextMyCode.setEnabled(false);

        setupPhoneText();
    }

    private void setupPhoneText() {
        //phoneEText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        phoneEText.setSelection(1);
        phoneEText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                int length = charSequence.length();
//                if (phoneNumber.length() < 10) {
//                    btnTextMyCode.setEnabled(false);
//                }else
//                {
//                    btnTextMyCode.setEnabled(true);
//
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = phoneEText.getText().toString();
                if (text.length() != (isPinEntry ? FORMAT_PIN_CODE.length() : FORMAT_PHONE_NUMBER.length())) {
                    cursorPosition = text.indexOf("#");


                    if (text.endsWith(" "))
                        return;

                    if (phoneNumber.length() == 3 && text.indexOf(")") == 3)
                    {
                        cursorPosition = 3;
                    }

                    if (phoneNumber.length() == 6 && text.indexOf("-") == 7)
                    {
                        cursorPosition = 7;
                    }


                    if (!isPinEntry) {
                        btnTextMyCode.setEnabled(false);
                        phoneNumber = text.replace("(", "").replace(")", "").replace("-", "").replaceAll("#", "");
                        if (phoneNumber.length() >= 10) {
                            phoneNumber = phoneNumber.substring(0, 10);
                            btnTextMyCode.setEnabled(true);
                                cursorPosition = 13;
                        }
                        numberLength = phoneNumber.length();
                    } else {
                        pinCode = text.replaceAll("#", "");
                        numberLength = pinCode.length();
                    }

                    text = isPinEntry ? FORMAT_PIN_CODE : FORMAT_PHONE_NUMBER;
                    for (int i = 0; i < numberLength; i++) {
                        text = text.replaceFirst("#", (isPinEntry ? pinCode.charAt(i) : phoneNumber.charAt(i)) + "");
                    }
                    phoneEText.setText(text);
                    if (cursorPosition == -1)
                        cursorPosition = (isPinEntry ? FORMAT_PIN_CODE.length()-1 : FORMAT_PHONE_NUMBER.length() - 1);
                    phoneEText.setSelection(cursorPosition);

                    pinCodeCompletionCheck();
                }
            }

        });
    }

    private void pinCodeCompletionCheck() {
        if (isPinEntry)
            if (pinCode.length() == 4) {
                apiRequestManager.addToRequestQueue(apiRequestManager.registerUser(myHandler, phoneNumber, pinCode), true);
            }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_txt_my_code:
                requestPin();
                //showPinCodeView();
                break;
            case R.id.btn_close_this_window:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(phoneEText.getWindowToken(), 0);
                break;
            case R.id.btn_change_number:
                showPhoneNumberView();
                break;
        }
    }

    private void showPinCodeView() {

//        findViewById(R.id.lbl_sending_code_now).setVisibility(View.VISIBLE);

//        findViewById(R.id.ly_form_request_info).setVisibility(View.GONE);
//        findViewById(R.id.btn_txt_my_code).setVisibility(View.GONE);
//        findViewById(R.id.ly_phone_number).setVisibility(View.VISIBLE);

        TextView entry1Text = (TextView) findViewById(R.id.lbl_entry_type_1);
        TextView entry2Text = (TextView) findViewById(R.id.lbl_entry_type_2);
        TextView phoneText = (TextView) findViewById(R.id.txt_phone_number);

        phoneText.setText(phoneEText.getText().toString().replace("(", "").replace(")", ".").replace("-", "."));

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                findViewById(R.id.lbl_sending_code_now).setVisibility(View.GONE);
                findViewById(R.id.ly_form_request_info).setVisibility(View.VISIBLE);
            }
        };

        handler.sendEmptyMessageDelayed(0, 2000);

        entry1Text.setText("Enter ");
        entry2Text.setText("Your Code");

        isPinEntry = true;
        phoneEText.setText(FORMAT_PIN_CODE);
        phoneEText.setSelection(0);
        phoneEText.setTextSize(25);
    }

    private void showPhoneNumberView() {
        findViewById(R.id.btn_txt_my_code).setVisibility(View.VISIBLE);
        findViewById(R.id.ly_phone_number).setVisibility(View.INVISIBLE);

        TextView entry1Text = (TextView) findViewById(R.id.lbl_entry_type_1);
        TextView entry2Text = (TextView) findViewById(R.id.lbl_entry_type_2);

        entry1Text.setText("Confirm ");
        entry2Text.setText("Your Phone");

        isPinEntry = false;
        phoneEText.setText(FORMAT_PHONE_NUMBER);
        phoneEText.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        phoneEText.setSelection(0);
        phoneEText.setTextSize(15);
        btnTextMyCode.setEnabled(false);
    }

    private void requestPin() {
        apiRequestManager.setShouldHideProgress(false);

        hidePhoneNumberView();

        apiRequestManager.addToRequestQueue(apiRequestManager.requestPin(myHandler, phoneNumber), false);
    }

    private void hidePhoneNumberView()
    {
        findViewById(R.id.lbl_sending_code_now).setVisibility(View.VISIBLE);
        findViewById(R.id.ly_form_request_info).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_txt_my_code).setVisibility(View.INVISIBLE);
        findViewById(R.id.ly_phone_number).setVisibility(View.VISIBLE);

    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            apiRequestManager.hideProgressDialog();
            int type = msg.arg1;
            int state = msg.arg2;
            JSONObject response = (JSONObject) msg.obj;
            switch (type)
            {
                case Constants.REQUEST_PIN:
                    showPinCodeView();
                    break;
                case Constants.REGISTER_USER:
                    if (state == 1) {
                        try {
                            LocalPreferences.saveUserToken(RequestInfoActivity.this, response.getString("data").toString());

                            JSONObject dicMetaData = response.getJSONObject("metadata");
                            if (dicMetaData != null)
                            {
                                String cid = dicMetaData.getString("cid");
                                LocalPreferences.saveUserCid(RequestInfoActivity.this, cid);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (requestType == 0)
                            apiRequestManager.addToRequestQueue(apiRequestManager.inquireProperty(myHandler, propertyId), false);
                        else if (requestType == 2)
                            apiRequestManager.addToRequestQueue(apiRequestManager.likeProperty(myHandler, propertyId), false);
                        else
                            showHomeScreen();
                    }
                    else
                    {
                        int i = 0;
                        phoneEText.setTextColor(ContextCompat.getColor(RequestInfoActivity.this, android.R.color.holo_red_light));

                        final TranslateAnimation moveAnim = new TranslateAnimation(30, -30, 0, 0);
                        moveAnim.setDuration(200);
                        moveAnim.setFillAfter(true);
                        moveAnim.setRepeatMode(Animation.INFINITE);
                        moveAnim.setRepeatCount(3);
                        phoneEText.startAnimation(moveAnim);

                        moveAnim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                //that's to move the viewpager to the desired location.
//                                pager.setCurrentItem(0, true);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                phoneEText.setTranslationX(30);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                    }
                    break;
                case Constants.INQUIRE_PROPERTY:
                case Constants.LIKE_PROPERTY:
                    showHomeScreen();
                case Constants.TERMS_OF_SERVICE:
                    try {
                        boolean isSuccess = response.getBoolean("success");
                        if (isSuccess) {
                            String url = response.getString("data");
                            url = "<font face='Arial'>" + url + "</font>";
                            webView.loadData(url, "text/html; charset=UTF-8", null);
                            WebSettings webSettings = webView.getSettings();
                            webSettings.setDefaultFontSize(8);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }


        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        apiRequestManager.destroyProgressDialog();
    }



    @Override
    public void onNetworkRequestSuccess(APIRequestManager.APIRequestType type, JsonObject response) {
        switch (type) {
            case REQUEST_PIN:
                showPinCodeView();
                break;
            case REGISTER_USER:
                LocalPreferences.saveUserToken(this, response.get("data").getAsString());
                if (requestType == 0)
                    apiRequestManager.addToRequestQueue(apiRequestManager.inquireProperty(myHandler, propertyId), false);
                else if (requestType == 2)
                    apiRequestManager.addToRequestQueue(apiRequestManager.likeProperty(myHandler, propertyId), false);
                else
                    showHomeScreen();
                break;
            case INQUIRE_PROPERTY:
            case LIKE_PROPERTY:
                showHomeScreen();
                break;
        }
    }

    private void showHomeScreen() {
        apiRequestManager.hideProgressDialog();
        phoneEText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light));

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Intent intent = new Intent(RequestInfoActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        handler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    public void onNetworkRequestError(APIRequestManager.APIRequestType type, VolleyError error) {
        switch (type) {
            case REQUEST_PIN:
                if (!error.getMessage().contains("The provided number was not valid"))
                    apiRequestManager.showDefaultPopup("Error", error.getMessage());
                else
                    showPinCodeView();
                break;
            case REGISTER_USER:
                phoneEText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                //apiRequestManager.showDefaultPopup("Error", error.getMessage());
                break;
            case INQUIRE_PROPERTY:
            case LIKE_PROPERTY:
                phoneEText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                apiRequestManager.hideProgressDialog();
                break;
        }
    }
}
