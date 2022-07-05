package com.pollution.pollutionApp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


import com.pollution.pollutionApp.utils.LoginSharedPref;

public class SplashScreenActivity extends AppCompatActivity {
    private FrameLayout relativeLayout;
    private Handler handler;
    private ImageView ivSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        initUI();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!LoginSharedPref.getCarouselShown(SplashScreenActivity.this)) {
                    //to carousel
                    goToLogin();
                } else {
                    //to dashboard
                    goToDashBoard();
                }
            }
        }, 2000);    //3000 test
    }

    private void initUI() {
        relativeLayout = findViewById(R.id.cLayout);
    }


    private void goToDashBoard() {
        Intent intentDashboard = new Intent(SplashScreenActivity.this, NavigationActivity.class);
        startActivity(intentDashboard);
        finish();
    }

    private void goToLogin() {
        Intent intentDashboard = new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivity(intentDashboard);
        finish();
    }
}

