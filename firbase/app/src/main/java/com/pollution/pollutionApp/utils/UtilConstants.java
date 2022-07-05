package com.pollution.pollutionApp.utils;


import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pollution.pollutionApp.R;


public class UtilConstants {

    public static final String MAP_TITLE = "Pollution Map";

    public static void showCustomToast(String toastText, AppCompatActivity context) {
        LayoutInflater inflater = context.getLayoutInflater();

        View layout = inflater.inflate(R.layout.custom_toast, null);
        TextView text = layout.findViewById(R.id.text);
        text.setText(toastText);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void showCustomToastNormal(String toastText, Activity context) {
        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_normal, null);
        TextView text = layout.findViewById(R.id.text);
        text.setText(toastText);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

}
