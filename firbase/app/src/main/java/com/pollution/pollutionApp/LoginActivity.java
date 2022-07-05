package com.pollution.pollutionApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pollution.pollutionApp.pojo.ProfilePojo;
import com.pollution.pollutionApp.utils.LoaderClass;
import com.pollution.pollutionApp.utils.LoginSharedPref;
import com.pollution.pollutionApp.utils.MyUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pollution.pollutionApp.utils.UtilConstants;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputEditText contactET, passET;
    private FrameLayout frameLayout;
    private Button loginBtn;
    private String uName;
    private TextInputLayout tilEmail;
    private DatabaseReference dbRef;
    private ValueEventListener valueListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check for logged in or not
        if (!LoginSharedPref.getUIdContactKey(LoginActivity.this).isEmpty()) {
            //already loggedin
            Intent regIntent = new Intent(LoginActivity.this, NavigationActivity.class);
            startActivity(regIntent);
            finish();
        } else {
            setContentView(R.layout.activity_login);
            initUI();
        }
    }

    private void initUI() {
        String emailId;
        contactET = findViewById(R.id.et_cont_login);
        passET = findViewById(R.id.et_pass_log);
        loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(this);
        frameLayout = findViewById(R.id.fl_login);
        if ((emailId = getIntent().getStringExtra(MyUtils.EMAIL_ID)) != null) {
            contactET.setText(emailId);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_login) {
            validationAndLogin();
        }
        hideKeyboard(v);
    }

    private void validationAndLogin() {
        if (contactET.getText().toString().isEmpty()) {
            Snackbar.make(frameLayout, "Contact field cannot be empty", Snackbar.LENGTH_SHORT).show();
            return;
        }
        try {
            Long.parseLong(contactET.getText().toString());
        } catch (Exception ex) {
            Snackbar.make(frameLayout, "Enter a valid contact number(only digits)", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (passET.getText().toString().isEmpty()) {
            Snackbar.make(frameLayout, "Password field cannot be empty", Snackbar.LENGTH_SHORT).show();
            return;
        }
        checkLogin();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void onRegClicked(View view) {
        Intent regIntent = new Intent(LoginActivity.this, ProfileRegActivity.class);
        startActivity(regIntent);
        finish();
    }

    private void checkLogin() {
        LoaderClass.startAnimation(LoginActivity.this);
        final String email = contactET.getText().toString();
        dbRef = null;
        dbRef = FirebaseDatabase.getInstance().getReference().child(MyUtils.CHILD_REG).child(email);
        //Query query = dbRef.limitToFirst(1);
        valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(MyUtils.TAG, "onDataChange: " + dataSnapshot.toString());
                if (dataSnapshot.exists()) {
                    ProfilePojo pojo = dataSnapshot.getValue(ProfilePojo.class);
                    if (pojo.pass.equals(passET.getText().toString())) {
                        LoginSharedPref.setNameKey(LoginActivity.this, pojo.name);
                        LoginSharedPref.setUidKey(LoginActivity.this, String.valueOf(pojo.contact));
                        Intent regIntent = new Intent(LoginActivity.this, NavigationActivity.class);
                        startActivity(regIntent);
                        finish();

                    } else {
                        UtilConstants.showCustomToast("Login failed", LoginActivity.this);
                    }
                    LoaderClass.stopAnimation();
                } else {
                    UtilConstants.showCustomToast("Login failed", LoginActivity.this);
                    LoaderClass.stopAnimation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                LoaderClass.stopAnimation();
            }
        };
        dbRef.addListenerForSingleValueEvent(valueListener);
    }

    @Override
    protected void onDestroy() {
        if (dbRef != null) {
            if (valueListener != null)
                dbRef.removeEventListener(valueListener);
        }
        super.onDestroy();
    }
}

