package com.dts.dts.fragments;

import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.dts.dts.Constants;
import com.dts.dts.R;
import com.dts.dts.animation.ProgressHUD;
import com.dts.dts.remote.APIRequestManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by silver on 1/27/17.
 */

public class FeedbackFragment extends Fragment {
    public static final String TAG = FeedbackFragment.class.getSimpleName();

    private ImageButton happy;
    private ImageButton sad;
    private TextView messageCountLabel;
    private EditText messageTextView;
    private ProgressHUD hud;
    private Context mContext;

    private String type;

    private int DEF_Max_MessageCount = 120;


    public FeedbackFragment(Context context){
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.feedback_fragment, container, false);
        happy = (ImageButton) rootView.findViewById(R.id.happy);
        sad = (ImageButton) rootView.findViewById(R.id.sad);
        messageCountLabel = (TextView) rootView.findViewById(R.id.messageCount);
        messageTextView = (EditText) rootView.findViewById(R.id.message);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFFFFFFF); // Changes this drawbale to use a single color instead of a gradient
        gd.setCornerRadius(3);
        gd.setStroke(1, Color.argb(255, 192, 192, 192));

        messageTextView.setBackground(gd);

        Button sendfeedback = (Button) rootView.findViewById(R.id.send_feedback);
        sendfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFeedback();
            }
        });

        happy.setColorFilter(Color.BLACK);
        sad.setColorFilter(Color.BLACK);
        happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHappyClicked();
            }
        });

        sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSadClicked();
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
        return rootView;
    }

    public void onHappyClicked()
    {
        happy.setColorFilter(Color.GREEN);
        sad.setColorFilter(Color.BLACK);
        type = "POSITIVE";

    }

    public void onSadClicked()
    {
        happy.setColorFilter(Color.BLACK);
        sad.setColorFilter(Color.GREEN);
        type = "NEGATIVE";


    }
    Handler progressHandler = new Handler();
    Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            hud.dismiss();
        }
    };

    public void sendFeedback()
    {
        if (type == null) {
            hud = ProgressHUD.show(mContext, "Please select the feeling mode.", true, true, null);
            progressHandler.postDelayed(progressRunnable, 2000);
        } else if (messageTextView.getText().length() == 0) {
            hud = ProgressHUD.show(mContext, "Please input message.", true, true, null);
            progressHandler.postDelayed(progressRunnable, 2000);
        } else {
            hud = ProgressHUD.show(mContext, "Processing...", true, true, null);
            progressHandler.postDelayed(progressRunnable, 2000);

            APIRequestManager apiRequestManager = new APIRequestManager(mContext, null);

            apiRequestManager.addToRequestQueue(apiRequestManager.sendFeedback(myHandler, type, messageTextView.getText().toString()), false);
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
            if (response == null) {
                hud = ProgressHUD.show(mContext, "Failed", true, true, null);
                progressHandler.postDelayed(progressRunnable, 2000);
                return;
            }


            try {
                boolean isSuccess = response.getBoolean("success");
                if (isSuccess) {
                    hud = ProgressHUD.show(mContext, "Your feedback has been sent.", true, true, null);
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
        }
    };

}
