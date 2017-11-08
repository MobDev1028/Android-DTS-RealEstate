package com.dts.dts.utils;

import static java.lang.Math.log10;

/**
 * Created by silver on 2/8/17.
 */

public class Utils {
    public static String suffixNumber(float num){
        String sign = ((num < 0) ? "-" : "" );

        num = Math.abs(num);

        if (num < 1000.0){
            int numAsInt = (int)num;
            return sign+numAsInt;
        }

        int exp = (int)(log10(num) / 3.0 );

        String[] units = {"K","M","G","T","P","E"};

        double roundedNum = Math.round(100 * num / Math.pow(1000.0, (double)exp)) / 100.0;

        String strRoundedNum = ""+roundedNum;

        strRoundedNum = strRoundedNum.replace(".0", "");

        String[] digitstAfterDecimal = strRoundedNum.split(".");
        if (digitstAfterDecimal.length > 0) {
            String strDecimalDigits = digitstAfterDecimal[digitstAfterDecimal.length - 1];
            if (strDecimalDigits.length() > 1) {
                strRoundedNum = strRoundedNum.substring(0, strRoundedNum.length()-2);

            }
        }

        return sign+strRoundedNum+units[exp-1];

    }}
