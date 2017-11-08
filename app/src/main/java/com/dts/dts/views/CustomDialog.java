package com.dts.dts.views;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by BilalHaider on 11/17/2015.
 */
public class CustomDialog {
    private AlertDialog mAlertDialog;
    private CallBackListener mCallBackListener;

    public CallBackListener getCallBackListener() {
        return mCallBackListener;
    }

    public void setCallBackListener(CallBackListener mCallBackListener) {
        this.mCallBackListener = mCallBackListener;
    }

    public enum DialogType {
        INTERNET_CONNECTION_ERROR,
        DEFAULT_ERROR
    }

    public CustomDialog(Context context, String errorTitle, String errorMessage, String positiveButtonText, String negativeButtonText, final DialogType dialogType) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        if (!errorTitle.isEmpty())
            alertDialogBuilder.setTitle(errorTitle);

        if (!errorMessage.isEmpty())
            alertDialogBuilder.setMessage(errorMessage);

        alertDialogBuilder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                if (mCallBackListener != null)
                    mCallBackListener.onPositiveButtonPressed(dialog, dialogType);
                else
                    dialog.dismiss();
            }
        });

        if (!negativeButtonText.isEmpty())
            alertDialogBuilder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mCallBackListener != null)
                        mCallBackListener.onNegativeButtonPressed(dialog, dialogType);
                    else
                        dialog.dismiss();
                }
            });

        mAlertDialog = alertDialogBuilder.create();
    }

    public void showDialog() {
        mAlertDialog.show();
    }

    public void hideDialog() {
        mAlertDialog.dismiss();
    }

    public interface CallBackListener {
        void onPositiveButtonPressed(DialogInterface dialog, DialogType dialogType);

        void onNegativeButtonPressed(DialogInterface dialog, DialogType dialogType);
    }
}
