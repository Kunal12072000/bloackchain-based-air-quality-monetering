package com.pollution.pollutionApp.frag;


import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.pollution.pollutionApp.R;
import com.pollution.pollutionApp.pojo.WSN_Data;
import com.pollution.pollutionApp.utils.AQICalculate;
import com.pollution.pollutionApp.utils.LoaderClass;
import com.pollution.pollutionApp.utils.MyUtils;
import com.pollution.pollutionApp.utils.PlacePojo;

import me.tankery.lib.circularseekbar.CircularSeekBar;

import static com.android.volley.VolleyLog.TAG;

public class DashboardFragment extends Fragment {

    private DatabaseReference dbRef;
    private ChildEventListener valueListener;
    private CircularSeekBar load;
    private TextView tvAQI, tvAQI_, w0tv, w1tv, w2tv, w3tv, w4tv, w5tv, placenamemarker, tempTV, humidityTV, dewTV, pollutantTv;
    private ProgressBar tv_loading;
    private WSN_Data placePojo;
    private LinearLayout llWindow;
    private LinearLayout llParam;
    private ScrollView sview;
    private TextView ch4Tv, co2Tv, nH3Tv, wSTv, o3Tv, o2Tv, so2Tv, pmTv;
    private int colorId;
    private FrameLayout fLay;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        load = v.findViewById(R.id.aqi_loader);
        load.setEnabled(false);
        load.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        tvAQI = v.findViewById(R.id.tv_aQI_val);
        pollutantTv = v.findViewById(R.id.pollutants);
        tvAQI_ = v.findViewById(R.id.tv_aQI);
        llWindow = v.findViewById(R.id.ll_window);
        fLay = v.findViewById(R.id.fl);
        llParam = v.findViewById(R.id.ll_3param);
        sview = v.findViewById(R.id.sv);
        ch4Tv = v.findViewById(R.id.tv_ch4);
        pmTv = v.findViewById(R.id.tv_pm);
        co2Tv = v.findViewById(R.id.tv_co2);
        nH3Tv = v.findViewById(R.id.tv_nh3);
        o3Tv = v.findViewById(R.id.tv_o3);
        wSTv = v.findViewById(R.id.tv_ws);
        so2Tv = v.findViewById(R.id.tv_so2);
        o2Tv = v.findViewById(R.id.tv_o2);
        placenamemarker = v.findViewById(R.id.placenamemarker);
        tempTV = v.findViewById(R.id.param1);
        humidityTV = v.findViewById(R.id.param2);
        dewTV = v.findViewById(R.id.param3);
        w0tv = v.findViewById(R.id.w0);
        tv_loading = v.findViewById(R.id.tv_loading);
        w1tv = v.findViewById(R.id.w1);
        w2tv = v.findViewById(R.id.w2);
        w3tv = v.findViewById(R.id.w3);
        w4tv = v.findViewById(R.id.w4);
        w5tv = v.findViewById(R.id.w5);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (DashboardFragment.this.isVisible()) {
            LoaderClass.startAnimation(getContext());
            tv_loading.setVisibility(View.GONE);
        }
        dbRef = null;
        dbRef = FirebaseDatabase.getInstance().getReference().child(MyUtils.CHILD_POLLUTIONDB).child(MyUtils.CHILD_WSNDATA);
        Query query = dbRef.limitToLast(1);
        valueListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                LoaderClass.stopAnimation();
                if (dataSnapshot.exists()) {
                    tv_loading.setVisibility(View.GONE);
                    String key = dataSnapshot.getKey();
                    Log.i(MyUtils.TAG, "onDataChange: " + dataSnapshot.toString());
                    PlacePojo.setWSNData(dataSnapshot.getValue(WSN_Data.class));
                    placePojo = PlacePojo.getWSNData();

                    tvAQI.setText(AQICalculate.aqi);
                    tvAQI_.setText("AQI");
                    tvAQI_.setVisibility(View.VISIBLE);
                    placenamemarker.setText(PlacePojo.placeName);
                    placenamemarker.setVisibility(View.VISIBLE);
                    markRange(AQICalculate.aqi);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addChildEventListener(valueListener);
    }

    private void markRange(String aqi) {
        if (aqi.isEmpty()) return;
        int range = AQICalculate.getRange(Integer.parseInt(aqi));
        Log.i(TAG, "markRange: " + range);
        switch (range) {
            case 0:
                colorId = R.color.good_meter;
                moveWindow(w0tv);
                break;
            case 1:
                colorId = R.color.moderate_meter_fluor;
                moveWindow(w1tv);
                break;
            case 2:
                colorId = R.color.unheal_sensi_meter;
                moveWindow(w2tv);
                break;
            case 3:
                colorId = R.color.unheal_meter;
                moveWindow(w3tv);
                break;
            case 4:
                colorId = R.color.v_unheal_meter;
                moveWindow(w4tv);
                break;
            case 5:
                colorId = R.color.hazardo_meter;
                moveWindow(w5tv);
                break;
        }
        load.setCircleColor(Color.LTGRAY);
        load.setCircleStrokeWidth(20);
        load.setVisibility(View.VISIBLE);
        fLay.setVisibility(View.VISIBLE);
        load.setProgress(getPercent(AQICalculate.aqi));
        load.setCircleProgressColor(ContextCompat.getColor(getContext(), colorId));
    }

    private float getPercent(String aqi) {
        return (float) (Integer.parseInt(aqi) / 5.0);
    }

    private void moveWindow(TextView tv) {
        if (tv == w0tv)
            tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.whiteborder0));
        else if (tv == w1tv)
            tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.whiteborder1));
        else if (tv == w2tv)
            tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.whiteborder2));
        else if (tv == w3tv)
            tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.whiteborder3));
        else if (tv == w4tv)
            tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.whiteborder4));
        else if (tv == w5tv)
            tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.whiteborder5));
        llWindow.setVisibility(View.VISIBLE);
        pollutantTv.setVisibility(View.VISIBLE);
        WSN_Data pojo;
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

            SpannableString spanTemp = new SpannableString("Temperature\n\n" + pojo.Temperature);
            spanTemp.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), colorId)), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tempTV.setText(spanTemp);
            tempTV.setHintTextColor(ContextCompat.getColor(getContext(), R.color.splash));
            //color not being set for hum and dew, why?
            SpannableString spanHum = new SpannableString("Humidity\n\n" + pojo.Humidity);
            spanHum.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), colorId)), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            humidityTV.setText(spanHum);
            humidityTV.setHintTextColor(ContextCompat.getColor(getContext(), R.color.splash));
            SpannableString spanDew = new SpannableString("Dew Point\n\n" + pojo.Dew);
            spanDew.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), colorId)), 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            dewTV.setText(spanDew);
            dewTV.setHintTextColor(ContextCompat.getColor(getContext(), R.color.splash));
            llParam.setVisibility(View.VISIBLE);
            ch4Tv.setText("CH4\n\n" + String.format("%.3f ", ch4));
            so2Tv.setText("SO2\n\n" + String.format("%.3f ", so2));
            o2Tv.setText("O2\n\n" + String.format("%.3f ", o2));
            o3Tv.setText("O3\n\n");
            nH3Tv.setText("NH3\n\n" + String.format("%.3f ", nh3));
            wSTv.setText("Wind\n\n");
            pmTv.setText("PM2.5\n\n");
            co2Tv.setText("CO2\n\n" + String.format("%.3f ", c02));
            sview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDetach() {
        if (valueListener != null && dbRef!=null) {
            dbRef.removeEventListener(valueListener);
            valueListener = null;
            dbRef = null;
        }
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        if (valueListener != null && dbRef!=null) {
            dbRef.removeEventListener(valueListener);
            valueListener = null;
            dbRef = null;
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mi_share) {
            sendAQI();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendAQI() {
        WSN_Data pojo;
        Log.i("TAG", "setMap: " + PlacePojo.getWSNData());
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

            StringBuilder formattedText = new StringBuilder();
            formattedText
                    .append("\t" + PlacePojo.placeName).append("\n")
                    .append("\tAQI ").append(AQICalculate.aqi).append("\n")
                    .append("\u2022 ")
                    .append("Temperature ".toUpperCase()).append(pojo.Temperature)
                    .append("\n\u2022 ").append("Humidity ".toUpperCase()).append(pojo.Humidity)
                    .append("\n\u2022 ").append("Dew Point ".toUpperCase()).append(pojo.Dew)
                    .append("\n\u2022 ").append("O2 ".toUpperCase()).append(String.format("%.3f ", o2))
                    .append("\n\u2022 ").append("CO2 ".toUpperCase()).append(String.format("%.3f ", c02))
                    .append("\n\u2022 ").append("SO2 ".toUpperCase()).append(String.format("%.3f ", so2))
                    .append("\n\u2022 ").append("NH3 ".toUpperCase()).append(String.format("%.3f ", nh3))
                    .append("\n\u2022 ").append("CH4 ".toUpperCase()).append(String.format("%.3f ", ch4));
            ShareCompat.IntentBuilder.from(getActivity()).setText(formattedText).setType("text/plain")
                    .setSubject(PlacePojo.placeName + " AQI details")
                    .setChooserTitle("Share AQI with details")
                    .startChooser();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
