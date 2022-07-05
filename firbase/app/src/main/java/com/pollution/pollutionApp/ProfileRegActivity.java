package com.pollution.pollutionApp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pollution.pollutionApp.pojo.ProfilePojo;
import com.pollution.pollutionApp.utils.EmailValidation;
import com.pollution.pollutionApp.utils.LoaderClass;
import com.pollution.pollutionApp.utils.LoginSharedPref;
import com.pollution.pollutionApp.utils.MyUtils;
import com.google.android.material.snackbar.Snackbar;
import com.pollution.pollutionApp.utils.UtilConstants;

public class ProfileRegActivity extends AppCompatActivity implements
        View.OnClickListener {
    //profile n reg
    private EditText nameET, contET, emailET, passET, ageET, wtET, htET, genderET, allergicET;
    private TextInputLayout nameTIL, contTIL, emailTIL, passTIL, ageTIL, wtTIL, htTIL, genderTIL, tilAllergy;
    private Button btnSubmit;
    private Dialog dialogLoader;
    private LinearLayout llCom;
    private String emailId;
    private boolean isProfile;


    private LinearLayout llShowInProf;
    private DatabaseReference dbRef;
    private ValueEventListener valueListener;
    private ValueEventListener valuListenerUpdate;
    private RadioButton feamleRB, maleRB;
    private RadioGroup rGroup;
    private String whichGender;
//    private ProfilePojo profPojo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_profile_reg);
        initUI();
    }

    private void initUI() {
        nameET = findViewById(R.id.et_name_reg);
        contET = findViewById(R.id.et_contact_reg);
        emailET = findViewById(R.id.et_email_reg);
        htET = findViewById(R.id.et_ht_reg);
        allergicET = findViewById(R.id.et_allergic);
        wtET = findViewById(R.id.et_wt_reg);
        ageET = findViewById(R.id.et_age_reg);
        rGroup = findViewById(R.id.rg);
        maleRB = findViewById(R.id.rbtn_male);
        feamleRB = findViewById(R.id.rbtn_female);
        passTIL = findViewById(R.id.til_pass);
        tilAllergy = findViewById(R.id.til_allergy);
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtn_male) {
                    whichGender = "Male";
                } else if (checkedId == R.id.rbtn_female) {
                    whichGender = "Female";
                }
            }
        });

        passET = findViewById(R.id.et_pass_reg);
        btnSubmit = findViewById(R.id.btn_reg);
        btnSubmit.setOnClickListener(this);
        llCom = findViewById(R.id.ll_willing);
        llShowInProf = findViewById(R.id.ll_showinprof);
        if (getIntent().getBooleanExtra(MyUtils.IS_PROFILE, false)) {
            //show other UI visible
            //also setData
            isProfile = true;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("My Profile");
            setVisibleUI(false);
            getProfile();
        } else {
            setVisibleUI(true);
        }
    }

    private void getProfile() {
        if (dbRef != null) {
            if (valueListener != null)
                dbRef.removeEventListener(valueListener);
            dbRef = null;
        }
        dbRef = FirebaseDatabase.getInstance().getReference().child(MyUtils.CHILD_REG).child(LoginSharedPref.getUIdContactKey(ProfileRegActivity.this));
        //Query query = dbRef.limitToFirst(1);
        valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(MyUtils.TAG, "onDataChange: " + dataSnapshot.toString());

                if (dataSnapshot.exists()) {
                    ProfilePojo pojo = dataSnapshot.getValue(ProfilePojo.class);
                    setData(pojo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dbRef.addListenerForSingleValueEvent(valueListener);
    }

    private void setData(ProfilePojo profPojo) {
        //edit texts, photo, latlong, gender highlight, bg
        if (profPojo != null) {
            nameET.setText(profPojo.name);
            contET.setText(String.valueOf(profPojo.contact));
            emailET.setText(profPojo.email);
            passET.setText(profPojo.pass);
            whichGender = profPojo.gender;
            if (whichGender != null && !whichGender.isEmpty()) {
                if (whichGender.equalsIgnoreCase("male")) {
                    maleRB.setChecked(true);
                } else feamleRB.setChecked(true);
            }
            ageET.setText("" + ((profPojo.age != 0) ? profPojo.age : ""));
            htET.setText("" + ((profPojo.ht != 0) ? profPojo.ht : ""));
            wtET.setText("" + ((profPojo.wt != 0) ? profPojo.wt : ""));
            allergicET.setText(profPojo.allergic);
        }
    }


    private void setVisibleUI(boolean register) {
        if (register) {
            llShowInProf.setVisibility(View.GONE);
            tilAllergy.setVisibility(View.GONE);
            rGroup.setVisibility(View.GONE);
        } else {
            btnSubmit.setText("Update");
            passTIL.setVisibility(View.GONE);
            contET.setEnabled(false);
            findViewById(R.id.iv_logo).setVisibility(View.GONE);
            findViewById(R.id.appname_tv).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (!isProfile) {
            if (valid(false)) {
                emailId = emailET.getText().toString();
                Intent intent = new Intent(ProfileRegActivity.this, OTPActivity.class);
                intent.putExtra(MyUtils.REG_OR_PROFILE_KEY, new ProfilePojo(
                        passET.getText().toString(),
                        Long.parseLong(contET.getText().toString()),
                        nameET.getText().toString(),
                        emailId
                ));
                startActivity(intent);
                finish();
            }
        } else {
            //update prof
            if (valid(true)) {
                if (dbRef != null) {
                    if (valuListenerUpdate != null)
                        dbRef.removeEventListener(valuListenerUpdate);
                    dbRef = null;
                }
                dbRef = FirebaseDatabase.getInstance().getReference().child(MyUtils.CHILD_REG).child(LoginSharedPref.getUIdContactKey(ProfileRegActivity.this));
                LoaderClass.startAnimation(ProfileRegActivity.this);
                valuListenerUpdate = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        LoaderClass.stopAnimation();
                        if (dataSnapshot.exists()) {
                            Log.i("TAG", "onDataChange:  prof exists");
                            ProfilePojo pojo = new ProfilePojo();
                            pojo.name = nameET.getText().toString();
                            pojo.email = emailET.getText().toString();
                            pojo.contact = Long.parseLong(contET.getText().toString());
                            pojo.pass = passET.getText().toString();
                            pojo.allergic = allergicET.getText().toString();
                            pojo.gender = whichGender;
                            pojo.wt = Double.parseDouble(wtET.getText().toString());
                            pojo.ht = Double.parseDouble(htET.getText().toString());
                            pojo.age = Integer.parseInt(ageET.getText().toString());
                            dbRef.setValue(pojo);
                            UtilConstants.showCustomToastNormal("Updated profile", ProfileRegActivity.this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ProfileRegActivity.this, "Something went wrong, Update not done!", Toast.LENGTH_SHORT).show();
                        LoaderClass.stopAnimation();
                    }
                };
                dbRef.addListenerForSingleValueEvent(valuListenerUpdate);
            }
        }

    }

    private boolean valid(boolean isUpdateProf) {
        if (nameET.getText().toString().isEmpty()) {
            Snackbar.make(llCom, "Please Enter Name", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (contET.getText().toString().isEmpty()) {
            Snackbar.make(llCom, "Please Enter contact", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (emailET.getText().toString().isEmpty()) {
            Snackbar.make(llCom, "Please Enter Email Address", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        EmailValidation validation = new EmailValidation();
        if (!validation.validateEmail(emailET.getText().toString())) {
            Snackbar.make(llCom, "Please Enter a valid Email Address", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (passET.getText().toString().isEmpty() && !isProfile) {
            Snackbar.make(llCom, "Please choose password", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (isUpdateProf) {
            if (ageET.getText().toString().isEmpty()) {
                Snackbar.make(llCom, "Please Enter Age", Snackbar.LENGTH_SHORT).show();
                return false;
            } else {
                try {
                    Integer.parseInt(ageET.getText().toString());
                } catch (Exception ex) {
                    Snackbar.make(llCom, "Please Enter a valid age(in digits)", Snackbar.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (htET.getText().toString().isEmpty()) {
                Snackbar.make(llCom, "Please Enter height", Snackbar.LENGTH_SHORT).show();
                return false;
            } else {
                try {
                    Double.parseDouble(htET.getText().toString());
                } catch (Exception ex) {
                    Snackbar.make(llCom, "Please Enter a valid height(in digits)", Snackbar.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (wtET.getText().toString().isEmpty()) {
                Snackbar.make(llCom, "Please Enter weight", Snackbar.LENGTH_SHORT).show();
                return false;
            } else {
                try {
                    Double.parseDouble(wtET.getText().toString());
                } catch (Exception ex) {
                    Snackbar.make(llCom, "Please Enter a valid height(in digits)", Snackbar.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (!maleRB.isChecked() && !feamleRB.isChecked()) {
                Snackbar.make(llCom, "Please choose your gender", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            if (allergicET.getText().toString().isEmpty()) {
                Snackbar.make(llCom, "Please Enter your allergies", Snackbar.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (dbRef != null) {
            if (valueListener != null)
                dbRef.removeEventListener(valueListener);
            if (valuListenerUpdate != null)
                dbRef.removeEventListener(valuListenerUpdate);
        }
        super.onDestroy();
    }
}
