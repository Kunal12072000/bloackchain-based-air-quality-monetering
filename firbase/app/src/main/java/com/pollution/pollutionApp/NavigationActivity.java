package com.pollution.pollutionApp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;


import com.pollution.pollutionApp.frag.DashboardFragment;
import com.pollution.pollutionApp.frag.MapFragment;
import com.pollution.pollutionApp.utils.LoginSharedPref;
import com.pollution.pollutionApp.utils.MyUtils;
import com.pollution.pollutionApp.utils.UtilConstants;
import com.google.android.material.navigation.NavigationView;

import static com.pollution.pollutionApp.utils.MyUtils.DASH_TITLE;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private int fragLoadedId = -1;
    private LinearLayout profileLL;
    private boolean notLoggedIn;
    private DrawerLayout drawer;
    private View viewHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (LoginSharedPref.getUIdContactKey(NavigationActivity.this).isEmpty()) {
            notLoggedIn = true;
        }

//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        viewHeader = navigationView.getHeaderView(0);
        viewHeader.setOnClickListener(this);
        profileLL = (LinearLayout) navigationView.getHeaderView(0);
        if (!LoginSharedPref.getUIdContactKey(this).equals("")) {
            TextView userName = profileLL.findViewById(R.id.tv_admin_name);
            userName.setText("Hi " + LoginSharedPref.getNameKey(this) + "!");
        }
        //profile section in user app only
        navigationView.setNavigationItemSelectedListener(this);
        loadFragInit();
    }

    private void loadFragInit() {
        Fragment fragment;
        Log.i("TAG", "loadFragInit: " + getIntent());
            Log.i("TAG", "loadFragInit: ");
            fragment = new DashboardFragment();
            setFragmentWithTitle(DASH_TITLE, fragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isConnectedToInternet()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
            builder.setTitle("Looks like you are offline");
            builder.setCancelable(false);
            builder.setMessage("No internet connection");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        if (id == R.id.nav_Home) {
            fragment = new DashboardFragment();
            setFragmentWithTitle(DASH_TITLE, fragment);
        } else if (id == R.id.nav_map) {
            fragment = new MapFragment();
            setFragmentWithTitle(UtilConstants.MAP_TITLE, fragment);
        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder ad = new AlertDialog.Builder(NavigationActivity.this);
            ad.setTitle("Confirm Sign out");
            ad.setMessage("You are logging out as " + LoginSharedPref.getNameKey(NavigationActivity.this));
            ad.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            ad.setPositiveButton("logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    logout();
                }
            });
            ad.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        LoginSharedPref.setUidKey(NavigationActivity.this, "");
        LoginSharedPref.setNameKey(NavigationActivity.this, "");
        LoginSharedPref.setOTP(NavigationActivity.this, "");
        Intent intent = new Intent(NavigationActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    //disable fab in vendor nav activity for prod
    private void setFragmentWithTitle(String title, Fragment fragment) {
        getSupportActionBar().setTitle(title);
        /*if (title.equals(MYSENTREQ_TITLE)) {
            fragLoadedId = 1;
            if (fab.isOrWillBeHidden()) fab.show(); //second tab for other hide fab
        } else {
            fab.hide();
        }*/
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_nav, fragment).commit();
        Log.i("TAG", "setFragmentWithTitle: " + fragment.getClass().getSimpleName());
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == viewHeader.getId()) {
            Log.i("TAG", "onClick: " + viewHeader.getId());
            onBackPressed();
            goToProfile();
        }
    }

    private void goToProfile() {
        //my account wall or --> probably details update view
        Intent intent = new Intent(NavigationActivity.this, ProfileRegActivity.class);
        intent.putExtra(MyUtils.IS_PROFILE, true);
        startActivity(intent);
    }
    private boolean isConnectedToInternet() {
        ConnectivityManager mgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return mgr.getActiveNetworkInfo() != null && mgr.getActiveNetworkInfo().isConnected();
    }

}
