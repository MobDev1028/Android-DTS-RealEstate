package com.dts.dts.views;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by BilalHaider on 2/11/2016.
 */
public class CustomDatePickerDialog extends DialogFragment {

    private android.app.DatePickerDialog.OnDateSetListener listener;

    public CustomDatePickerDialog() {
    }

    @SuppressLint("ValidFragment")
    public CustomDatePickerDialog(android.app.DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), listener, year, month, day);
        datePicker.getDatePicker().setTag(getTag());

        // Create a new instance of TimePickerDialog and return it
        return datePicker;
    }
}
