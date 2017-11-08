package com.dts.dts.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.models.UserGeneralInfo;
import com.dts.dts.remote.APIRequestManager;
import com.dts.dts.utils.LocalPreferences;
import com.dts.dts.utils.SideMenuCreator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class GeneralInfoActivity extends AppCompatActivity implements View.OnClickListener, APIRequestManager.APIRequestListener {

    private SideMenuCreator.SideMenuToggle menuToggle;
    private APIRequestManager apiRequestManager;
    private UserGeneralInfo userInfo;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etAddress1;
    private EditText etAddress2;
    private EditText etZipcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_info);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        apiRequestManager = new APIRequestManager(this, this);

        findViewById(R.id.btn_save_user_info).setOnClickListener(this);
        setupSideMenu();

        initializeViews();

        apiRequestManager.addToRequestQueue( apiRequestManager.getUserGeneral(myHandler), true);
    }

    private void initializeViews() {
        etFirstName = (EditText) findViewById(R.id.et_first_name);
        etLastName = (EditText) findViewById(R.id.et_last_name);
        etEmail = (EditText) findViewById(R.id.et_email);
        etAddress1 = (EditText) findViewById(R.id.et_address_1);
        etAddress2 = (EditText) findViewById(R.id.et_address_2);
        etZipcode = (EditText) findViewById(R.id.et_zipcode);

    }

    private void setupSideMenu() {
        findViewById(R.id.btn_side_menu).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_side_menu).setOnClickListener(this);
        SideMenuCreator menuCreator = new SideMenuCreator(this, findViewById(R.id.activity_general_info));
        menuToggle = menuCreator.getMenuToggle();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_side_menu:
                menuToggle.toggleDrawer();
                break;
            case R.id.btn_save_user_info:
                saveUserInfo();
                break;
        }
    }

    private void saveUserInfo() {
        userInfo.setFirstName(etFirstName.getText().toString());
        userInfo.setLastName(etLastName.getText().toString());
        userInfo.setEmail(etEmail.getText().toString());
        userInfo.setAddress1(etAddress1.getText().toString());
        userInfo.setAddress2(etAddress2.getText().toString());
        userInfo.setZip(etZipcode.getText().toString());

        apiRequestManager.addToRequestQueue(apiRequestManager.saveUserGenerals(userInfo), true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            apiRequestManager.hideProgressDialog();
            int type = msg.arg1;

            JSONObject response = (JSONObject) msg.obj;
            switch (type)
            {
                case Constants.GET_USER_GENERAL:
                    try {
                        fillViews((JsonElement)response.get("data"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.SAVE_USER_GENERAL:
                    apiRequestManager.showDefaultPopup("", "Info Saved");
                    break;
            }


        }
    };

    @Override
    public void onNetworkRequestSuccess(APIRequestManager.APIRequestType type, JsonObject response) {
        switch (type) {
            case GET_USER_GENERAL:
                fillViews(response.get("data"));
                break;
            case SAVE_USER_GENERAL:
                apiRequestManager.showDefaultPopup("", "Info Saved");
                break;
        }
    }

    private void fillViews(JsonElement data) {
        Gson gson = new GsonBuilder().create();
        userInfo = gson.fromJson(data, UserGeneralInfo.class);

        etFirstName.setText(userInfo.getFirstName());
        etLastName.setText(userInfo.getLastName());
        etEmail.setText(userInfo.getEmail());
        etAddress1.setText(userInfo.getAddress1());
        etAddress2.setText(userInfo.getAddress2());
        etZipcode.setText(userInfo.getZip());
    }

    @Override
    public void onNetworkRequestError(APIRequestManager.APIRequestType type, VolleyError error) {
        apiRequestManager.showDefaultPopup("Error", error.getMessage());
    }

    @Override
    public void onBackPressed() {
        if (menuToggle != null) {
            if (menuToggle.isDrawerOpen()) {
                menuToggle.toggleDrawer();
            }

        }
        super.onBackPressed();

    }
}
