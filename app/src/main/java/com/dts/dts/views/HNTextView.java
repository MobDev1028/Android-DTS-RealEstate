package com.dts.dts.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.dts.dts.R;


/**
 * Created by BilalHaider on 1/12/2016.
 */
public class HNTextView extends AppCompatTextView {


    public HNTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray values = context.obtainStyledAttributes(attrs,
                R.styleable.HNTextView);
        int typeface = values.getInt(
                R.styleable.HNTextView_bhFontType, 0);
        values.recycle();

        String[] fontPaths = getResources().getStringArray(R.array.fonts_path);

        String typefacePath = fontPaths[typeface];
        /*if (typeface == 0) {
            typefacePath = "fonts/helvetica_neue.ttf";
        } else if (typeface == 1) {
            typefacePath = "fonts/helvetica_neue_thin.ttf";
        } else if (typeface == 2) {
            typefacePath = "fonts/helvetica_neue_light.ttf";
        } else if (typeface == 3) {
            typefacePath = "fonts/helvetica_neue_medium.ttf";
        } else if (typeface == 4) {
            typefacePath = "fonts/helvetica_neue_lt_italic.ttf";
        }*/

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                typefacePath);
        setTypeface(tf);
        setIncludeFontPadding(false);
    }
}
