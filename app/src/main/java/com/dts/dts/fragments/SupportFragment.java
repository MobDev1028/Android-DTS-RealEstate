package com.dts.dts.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.animation.ProgressHUD;
import com.dts.dts.remote.APIRequestManager;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by silver on 1/26/17.
 */

public class SupportFragment extends Fragment {
    public static final String TAG = SupportFragment.class.getSimpleName();

    private TextView textCodeButton, crashButton, noListingsButton, unknownButton, messageCountLabel;
    private EditText messageTextView;

    private ProgressHUD hud;

    private String selectedProblem;
    private int DEF_Max_MessageCount = 120;

    private Context mContext;
    public SupportFragment(Context context)
    {
        mContext = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.support_fragment, container, false);
        textCodeButton = (TextView) rootView.findViewById(R.id.textcode);
        crashButton = (TextView) rootView.findViewById(R.id.crash);
        noListingsButton = (TextView) rootView.findViewById(R.id.nolisting);
        unknownButton = (TextView) rootView.findViewById(R.id.unknown);
        messageTextView = (EditText) rootView.findViewById(R.id.message);
        messageCountLabel = (TextView) rootView.findViewById(R.id.messageCount);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFFFFFFF); // Changes this drawbale to use a single color instead of a gradient
        gd.setCornerRadius(3);
        gd.setStroke(1, Color.argb(255, 192, 192, 192));

        textCodeButton.setBackground(gd);
        crashButton.setBackground(gd);
        noListingsButton.setBackground(gd);
        unknownButton.setBackground(gd);
        messageTextView.setBackground(gd);

        textCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onProblemTypeClicked(textCodeButton);
            }
        });

        crashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onProblemTypeClicked(crashButton);
            }
        });

        noListingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onProblemTypeClicked(noListingsButton);
            }
        });

        unknownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onProblemTypeClicked(unknownButton);
            }
        });

        messageTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newText = charSequence.toString();
                int numberofChars = (newText.length()<DEF_Max_MessageCount)?newText.length():DEF_Max_MessageCount;
                messageCountLabel.setText(DEF_Max_MessageCount-numberofChars +  " remaining");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Button submit = (Button) rootView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmit();
            }
        });
        return rootView;
    }

    Handler progressHandler = new Handler();
    Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            hud.dismiss();
        }
    };

    public void onSubmit()
    {
        if (selectedProblem == null) {
            hud = ProgressHUD.show(mContext, "Please select problem type.", true, true, null);
            progressHandler.postDelayed(progressRunnable, 2000);

        }
        else if (messageTextView.getText().length() == 0) {
            hud = ProgressHUD.show(mContext, "Please input message.", true, true, null);
            progressHandler.postDelayed(progressRunnable, 2000);

        } else {
            hud = ProgressHUD.show(mContext, "Processing...", true, true, null);
            APIRequestManager apiRequestManager = new APIRequestManager(mContext, null);

            apiRequestManager.addToRequestQueue(apiRequestManager.createSupportTicket(myHandler, selectedProblem, messageTextView.getText().toString()), false);

        }


    }

    Handler completeHandler = new Handler();
    Runnable completeRunnable = new Runnable() {
        @Override
        public void run() {
            hud.dismiss();
            getFragmentManager().popBackStack();
        }
    };


    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int type = msg.arg1;
            hud.dismiss();
            JSONObject response = (JSONObject) msg.obj;
            switch (type) {
                case Constants.CREATE_SUPPORT_TICKET:
                    if (response == null) {
                        hud = ProgressHUD.show(mContext, "Failed", true, true, null);
                        progressHandler.postDelayed(progressRunnable, 2000);
                        break;
                    }


                    try {
                        boolean isSuccess = response.getBoolean("success");
                        if (isSuccess) {
                            hud = ProgressHUD.show(mContext, "Your support ticket has been created.", true, true, null);
                            hud.setHudTitle("Success");
                            completeHandler.postDelayed(completeRunnable, 2000);
                        } else {
                            hud = ProgressHUD.show(mContext, response.getString("message"), true, true, null);
                            hud.setHudTitle("Failed");
                            progressHandler.postDelayed(progressRunnable, 2000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    private void onProblemTypeClicked(TextView button){
        TextView[] buttonArray = {textCodeButton, crashButton, noListingsButton, unknownButton};

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.WHITE); // Changes this drawbale to use a single color instead of a gradient
        gd.setCornerRadius(3);
        gd.setStroke(1, Color.argb(255, 192, 192, 192));

        for (int i = 0; i < 4; i++) {
            buttonArray[i].setBackground(gd);
        }

        GradientDrawable gd_ = new GradientDrawable();
        gd_.setColor(Color.argb(255, 67, 137, 202)); // Changes this drawbale to use a single color instead of a gradient
        gd_.setCornerRadius(3);
        gd_.setStroke(1, Color.argb(255, 67, 137, 202));
        button.setBackground(gd_);

        selectedProblem = button.getText().toString();
    }
}
