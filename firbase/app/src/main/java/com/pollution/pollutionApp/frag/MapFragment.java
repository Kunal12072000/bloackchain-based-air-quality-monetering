package com.pollution.pollutionApp.frag;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pollution.pollutionApp.R;
import com.pollution.pollutionApp.pojo.WSN_Data;
import com.pollution.pollutionApp.utils.AQICalculate;
import com.pollution.pollutionApp.utils.PlacePojo;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final long FASTEST_INTERVAL = 5_000;
    private static final long INTERVAL = 5_000;

    private static final float ZOOM_DEFAULT_LEVEL_ = 12;
    private static final int RC_LOC_TURN_ON = 5;
    private static final String TAG = "TAG";

    private GoogleMap mMap;
    private boolean isConnected;
    private Marker markerCurrent;
    private Location currentLocationObj;

    private boolean recognized, fabLocOn;
    private FloatingActionButton fab;

    private CoordinatorLayout coordinatorLay;

    private ProgressDialog pDialog;
    private LatLng latLngNearby;
    private boolean zoomOnce;
    private TextView tvInfoPollution;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_maps, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fab = v.findViewById(R.id.on_off);
        tvInfoPollution = v.findViewById(R.id.tv_info);
        //fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PlacePojo.placeLatlong, mMap.getMaxZoomLevel()));
            }
        });
        coordinatorLay = v.findViewById(R.id.coordinatorLay);
        initData();
        fabLocOn = true;
        zoomOnce = true;
        return v;
    }


    private boolean isConnectedToInternet() {
        ConnectivityManager mgr = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        return mgr.getActiveNetworkInfo() != null && mgr.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (!gApiClient.isConnecting() || !gApiClient.isConnected()) {
            gApiClient.connect();
            Log.i(TAG, "onResume: connected");
        }*/
        if (!isConnectedToInternet()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Looks like you are offline");
            builder.setCancelable(false);
            builder.setMessage("No internet connection");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called with: connectionResult = [" + connectionResult + "]");
    }

    private void setMap(LatLng latLng) {
        Log.i(TAG, "setMap: isConnected " + isConnected);
        currentLocationObj.setLatitude(latLng.latitude);
        currentLocationObj.setLongitude(latLng.longitude);
        Log.i(TAG, "setMap: " + currentLocationObj);
        MarkerOptions options = new MarkerOptions();
        options.position(PlacePojo.otherplaceLatlong);
        options.title(PlacePojo.otherplaceName+" (216)");
        options.icon(BitmapDescriptorFactory.defaultMarker());
        mMap.addMarker(options);
        MarkerOptions options2 = new MarkerOptions();
        options2.position(latLng);
        options2.title(PlacePojo.placeName+" (" + AQICalculate.aqi+")");
        options2.icon(BitmapDescriptorFactory.defaultMarker(HUE_GREEN));
        if (markerCurrent != null)
            markerCurrent.remove();
        markerCurrent = mMap.addMarker(options2);
        markerCurrent.showInfoWindow();
        if (fabLocOn) {
            if (zoomOnce) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_DEFAULT_LEVEL_));
                zoomOnce = false;
            }
        }
        WSN_Data pojo;
        Log.i(TAG, "setMap: " + PlacePojo.getWSNData());
        if ((pojo = PlacePojo.getWSNData()) != null) {
            double o2 = 0, so2 = 0, c02 = 0, nh3 = 0, ch4 = 0;
            if (!pojo.O2.isEmpty())
                o2 = Double.parseDouble(pojo.O2.replace("%", "").trim());
            if (!pojo.CO2.isEmpty())
                c02 = Double.parseDouble(pojo.CO2.replace("%", "").trim());
            if (!pojo.SO2.isEmpty())
                so2 = Double.parseDouble(pojo.SO2.replace("%", "").trim());
            if (!pojo.NH3.isEmpty())
                nh3 = Double.parseDouble(pojo.NH3.replace("%", "").trim());
            if (!pojo.CH4.isEmpty())
                ch4 = Double.parseDouble(pojo.CH4.replace("%", "").trim());

            StringBuilder builder = new StringBuilder();
            builder.append("AQI ").append(AQICalculate.aqi).append("\n")
                    .append("\u2022 ")
                    .append("Temperature ".toUpperCase()).append(pojo.Temperature)
                    .append("\n\u2022 ").append("Humidity ".toUpperCase()).append(pojo.Humidity)
                    .append("\n\u2022 ").append("Dew Point ".toUpperCase()).append(pojo.Dew)
                    .append("\n\u2022 ").append("O2 ".toUpperCase()).append(String.format("%.3f ", o2))
                    .append("\t\u2022 ").append("CO2 ".toUpperCase()).append(String.format("%.3f ",c02))
                    .append("\n\u2022 ").append("SO2 ".toUpperCase()).append(String.format("%.3f ",so2))
                    .append("\t\u2022 ").append("NH3 ".toUpperCase()).append(String.format("%.3f ",nh3))
                    .append("\n\u2022 ").append("CH4 ".toUpperCase()).append(String.format("%.3f ",ch4));
            tvInfoPollution.setText(builder.toString());
            tvInfoPollution.setVisibility(View.VISIBLE);
        } else {
            tvInfoPollution.setVisibility(View.GONE);
        }
    }

    private void initData() {
        currentLocationObj = new Location("");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMap(PlacePojo.placeLatlong);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        isConnected = true;
        Log.i(TAG, "onConnected: " + isConnected);
    }

    @Override
    public void onConnectionSuspended(int i) {
        isConnected = false;
        Log.i(TAG, "onConnectionSuspended: " + isConnected);
    }
}
