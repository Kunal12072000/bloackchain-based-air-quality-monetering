package com.pollution.pollutionApp.utils;

import android.content.Context;
import android.util.Log;

import com.pollution.pollutionApp.R;

import java.util.Random;

public class Authentication {

    private Context appContext;

    public Authentication(Context appContext) {
        this.appContext = appContext;
    }


    public String[] generateOTPText(String contactNum) {
        String code = getCode();
        Log.d("OTP", code);
        if (code == null) {
            code = "098765";
            return new String[]{"Your OTP is "+ code+"\n\n- from "+appContext.getResources().getString(R.string.app_name)
                    , code};
        } else if (contactNum.equalsIgnoreCase(MyUtils.NA))
            return new String[]{"Your OTP is "+ code+"\n\n- from "+appContext.getResources().getString(R.string.app_name)
                    , code};
        else
            return new String[]{"Your OTP is "+ code+"\n\n- from "+appContext.getResources().getString(R.string.app_name)
                    , code};
    }

    /* private String getCode() {
         SecureRandom random = new SecureRandom();
         int num = random.nextInt(10_000_000);  //6digit otp 0 - 9999999
         if (num < 999_999)
             return
             return String.valueOf(num);
     }*/
    private String getCode() {
        String digit = null;
        digit = String.valueOf(generateRandom6Digit());
        return digit;
    }

    private int generateRandom6Digit() {
        int min = 1000, max = 9999;
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

}
