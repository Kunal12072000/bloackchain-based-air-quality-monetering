package com.pollution.pollutionApp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginSharedPref {
    private static final String SPREF_FILE = "SPREF_LOGIN";
    private static final String NGOID_LOGIN_KEY = "uid";
    private static final String OTP_KEY = "OTP_KEY";
    private static final String INTRO_KEY = "INTRO_KEY";
    private static final String NAME_KEY = "name";


    public static String getUIdContactKey(Context context) {
        SharedPreferences sprefLogin = context.getSharedPreferences(SPREF_FILE, Context.MODE_PRIVATE);
        return sprefLogin.getString(NGOID_LOGIN_KEY, "");
    }

    public static void setUidKey(Context context, String uid) {
        SharedPreferences sprefLogin = context.getSharedPreferences(SPREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sprefLogin.edit();
        editor.putString(NGOID_LOGIN_KEY, uid);
        editor.apply();
    }

    public static void setNameKey(Context context, String name) {
        SharedPreferences sprefLogin = context.getSharedPreferences(SPREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sprefLogin.edit();
        editor.putString(NAME_KEY, name);
        editor.apply();
    }
    public static String getNameKey(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPREF_FILE,Context.MODE_PRIVATE);
        return sharedPreferences.getString(NAME_KEY, "");
    }
    public static boolean getCarouselShown(Context context) {
        SharedPreferences sPref = context.getSharedPreferences(SPREF_FILE, Context.MODE_PRIVATE);
        return sPref.getBoolean(INTRO_KEY, false);
    }
    public static void setOTP(Context context, String dname) {
        SharedPreferences sPref = context.getSharedPreferences(SPREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(OTP_KEY, dname);
        editor.apply();
    }
    public static String getOTP(Context context) {
        SharedPreferences sPref = context.getSharedPreferences(SPREF_FILE, Context.MODE_PRIVATE);
        return sPref.getString(OTP_KEY, "");
    }

    public static void setCarouselShown(Context context, boolean dname) {
        SharedPreferences sPref = context.getSharedPreferences(SPREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(INTRO_KEY, dname);
        editor.apply();
    }
}
