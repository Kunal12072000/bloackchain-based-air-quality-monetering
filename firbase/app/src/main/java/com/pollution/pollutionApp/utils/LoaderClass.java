package com.pollution.pollutionApp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.pollution.pollutionApp.R;


public class LoaderClass {
    private static Dialog dialogLoader;
    private static Context context;
    public static void stopAnimation() {
        if (dialogLoader != null&& dialogLoader.isShowing())
            dialogLoader.cancel();
    }

    public static void startAnimation(Context context) {
        LoaderClass.context = context;
        if (dialogLoader!=null && dialogLoader.isShowing())dialogLoader.cancel();
        dialogLoader = new Dialog(LoaderClass.context, R.style.AppTheme_NoActionBar);
        dialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D000000")));
        final View view = ((Activity) LoaderClass.context).getLayoutInflater().inflate(R.layout.custom_dialog_loader, null);
        LottieAnimationView animationView = view.findViewById(R.id.loader);
        animationView.playAnimation();
        dialogLoader.setContentView(view);
        dialogLoader.setCancelable(false);
        dialogLoader.show();
    }

}