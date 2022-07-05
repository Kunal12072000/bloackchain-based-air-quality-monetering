package com.pollution.pollutionApp;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pollution.pollutionApp.pojo.ProfilePojo;
import com.pollution.pollutionApp.utils.Authentication;
import com.pollution.pollutionApp.utils.LoaderClass;
import com.pollution.pollutionApp.utils.LoginSharedPref;
import com.pollution.pollutionApp.utils.MyUtils;
import com.pollution.pollutionApp.utils.UtilConstants;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import java.net.URLEncoder;

import static com.pollution.pollutionApp.utils.LoaderClass.startAnimation;
import static com.pollution.pollutionApp.utils.LoaderClass.stopAnimation;


public class OTPActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    public static final String VERIFY_PROGRESS_KEY = "VERFICATION_PROGRESS";

    private InputMethodManager imm;

    private String mVerificationId;

    private EditText smsCodeVerify1, smsCodeVerify2, smsCodeVerify3, smsCodeVerify4;
    private boolean isUserDeleted = false;
    private EditText contactNum;
    private Dialog dialogOTP;
    private Button okBtnInDialog, resendOTPBtnInDialog;
    private Dialog dialogLoader;
    private EditText precontactNum;
    private ProfilePojo pojo;
    private DatabaseReference dbRef;
    private LinearLayout linear_verify;
    private ValueEventListener duplicateListener;
    private ValueEventListener checkOrAddListener;
    private ValueEventListener alreadyRegListener;

    //indian? number
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        contactNum = findViewById(R.id.et_number);
        precontactNum = findViewById(R.id.et_pre_number);
        imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if ((pojo = (ProfilePojo) getIntent().getSerializableExtra(MyUtils.REG_OR_PROFILE_KEY)) != null) {
            contactNum.setText(String.valueOf(pojo.contact));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void afterSent() {
        if (dbRef != null) {
            if (duplicateListener != null)
                dbRef.removeEventListener(duplicateListener);
            dbRef = null;
        }

        dbRef = FirebaseDatabase.getInstance().getReference().child(MyUtils.CHILD_REG).child(contactNum.getText().toString());
        duplicateListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i(TAG, "onDataChange: REg exists");
                    UtilConstants.showCustomToast("This contact number is already registered", OTPActivity.this);
                } else {
                    oTPdialogShow();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dbRef.addListenerForSingleValueEvent(duplicateListener);
    }

    private void oTPdialogShow() {
        dialogOTP = new Dialog(OTPActivity.this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_activity_otp, null);
        dialogOTP.setContentView(dialogView);
        dialogOTP.setCancelable(false);
        okBtnInDialog = dialogView.findViewById(R.id.button_verify);
        resendOTPBtnInDialog = dialogView.findViewById(R.id.btn_resend);
        smsCodeVerify1 = dialogView.findViewById(R.id.numberAuthEdt1);
        smsCodeVerify2 = dialogView.findViewById(R.id.numberAuthEdt2);
        linear_verify = dialogView.findViewById(R.id.linear_verify);
        smsCodeVerify3 = dialogView.findViewById(R.id.numberAuthEdt3);
        smsCodeVerify4 = dialogView.findViewById(R.id.numberAuthEdt4);
        setTextWatcherForCode(smsCodeVerify1, smsCodeVerify2, smsCodeVerify3, smsCodeVerify4);
        okBtnInDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick() called " + view.getId() + " " + R.id.button_verify);
                if (verifyNumbers(smsCodeVerify1, smsCodeVerify2, smsCodeVerify3, smsCodeVerify4)) {
                    onCheck(smsCodeVerify1.getText().toString() + smsCodeVerify2.getText().toString()
                            + smsCodeVerify3.getText().toString()
                            + smsCodeVerify4.getText().toString());
                }
            }
        });
        resendOTPBtnInDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOTP();
            }
        });
        if (dialogOTP != null)
            dialogOTP.show();
    }

    private void setTextWatcherForCode(final EditText... smsCodeVerify) {
        for (int i = 0; i < smsCodeVerify.length; i++) {
            final int finalI = i;
            smsCodeVerify[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (smsCodeVerify[finalI].getText().length() == 1) {
                        if (finalI == smsCodeVerify.length - 1) {
                            hideKeyBoard(smsCodeVerify[finalI - 1]);
                        } else {
                            showSoftKeyboard(smsCodeVerify[(finalI + 1)]);
                        }
                    } else if (smsCodeVerify[finalI].getText().length() == 0) {
                        if ((finalI - 1) >= 0) {
                            showSoftKeyboard(smsCodeVerify[(finalI - 1)]);
                        }

                    }
                }
            });
        }
    }

    private void hideKeyBoard(View view) {
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void onCheck(String compareOTP) {
        if (!LoginSharedPref.getOTP(OTPActivity.this).isEmpty()) {
            if (dialogOTP.isShowing()) {
                dialogOTP.cancel();
            }
            Log.i(TAG, "onCheck: " + compareOTP + LoginSharedPref.getOTP(this));
            if (LoginSharedPref.getOTP(OTPActivity.this).equalsIgnoreCase(compareOTP)) {
                //donest not exist
                //then
                //reg
                checkOrAdd();
                //enter

            } else {
                UtilConstants.showCustomToast("OTP match failed", OTPActivity.this);
            }
        } else {
            Log.i(TAG, "onCheck: OTP not send successfully, not in spref");
        }
    }

    private void checkOrAdd() {
        if (dbRef != null) {
            if (checkOrAddListener != null)
                dbRef.removeEventListener(checkOrAddListener);
            dbRef = null;
        }
        LoaderClass.startAnimation(OTPActivity.this);
        dbRef = FirebaseDatabase.getInstance().getReference().child(MyUtils.CHILD_REG).child(contactNum.getText().toString());
        checkOrAddListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stopAnimation();
                if (dataSnapshot.exists()) {
                    Log.i(TAG, "onDataChange: REg exists");
                    UtilConstants.showCustomToast("This contact number is already registered", OTPActivity.this);
                } else {
                    pojo.contact = Long.parseLong(contactNum.getText().toString());
                    dbRef.setValue(pojo);

                    Log.i(TAG, "onDataChange: REg done");
                    Intent intent = new Intent(OTPActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OTPActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                stopAnimation();
            }
        };
        dbRef.addListenerForSingleValueEvent(checkOrAddListener);
    }

    private void checkNumberIfRegisteredAlready(final String fullNum, final String s) {
        if (dbRef != null) {
            if (alreadyRegListener != null)
                dbRef.removeEventListener(alreadyRegListener);
            dbRef = null;
        }

        dbRef = FirebaseDatabase.getInstance().getReference().child(MyUtils.CHILD_REG).child(contactNum.getText().toString());
        alreadyRegListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i(TAG, "onDataChange: REg exists");
                    UtilConstants.showCustomToast("This contact number is already registered", OTPActivity.this);
                } else {
                    getEmailReply(fullNum, s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dbRef.addListenerForSingleValueEvent(alreadyRegListener);
    }


    private boolean verifyNumbers(EditText... smsCodeVerify1) {
        if (smsCodeVerify1.length <= 0) return false;
        for (EditText editTexts : smsCodeVerify1) {
            if (TextUtils.isEmpty(editTexts.getText().toString())) {
                Snackbar.make(linear_verify, "Please Enter Verification Code", Snackbar.LENGTH_SHORT)
                        .show();
                editTexts.requestFocus();
                return false;
            }
        }
        return true;
    }

    public void onOptSendClicked(View view) {
        sendOTP();
    }

    private void sendOTP() {
        if (contactNum.getText().toString().isEmpty()) {
            UtilConstants.showCustomToastNormal("Enter your mobile number", OTPActivity.this);
            return;
        }
        clickSend();
    }

    private void clickSend() {
        Authentication auth;
        auth = new Authentication(OTPActivity.this);
        String fullNum = null;
        if (precontactNum.getText().toString().contains("+")) {
            fullNum = precontactNum.getText().toString() + contactNum.getText().toString();
        } else if (precontactNum.getText().toString().isEmpty()) {
            fullNum = contactNum.getText().toString();
        } else {
            fullNum = "+" + precontactNum.getText().toString() + contactNum.getText().toString();
        }
        Log.i(TAG, "clickSend: " + fullNum);
        String[] OTP = auth.generateOTPText(fullNum);
        LoginSharedPref.setOTP(OTPActivity.this, OTP[1]);
        checkNumberIfRegisteredAlready(fullNum, OTP[0]);
    }

    public void getEmailReply(String contactNum, String textContent) {
        try {
            //&sender_id=FSTSMS&message=This%20is%20test%20message&language=english&route=p&numbers=9773119062;
            String baseurl = MyUtils.BASEURL;
            String recepient = "&numbers=" + URLEncoder.encode(contactNum, "UTF-8");
            String body = "&message=" + URLEncoder.encode(textContent, "UTF-8");

            String furl = baseurl + body + "&language=english&route=p" + recepient;
            Log.i("TAG", "getotpReply:  " + furl);
            startAnimation(OTPActivity.this);
            StringRequest s = new StringRequest(Request.Method.GET, furl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    stopAnimation();
                    try {
                        Log.d("RESPONSE", response);
                        //true
                        if (response.contains("Success")) {
                            Toast.makeText(OTPActivity.this, "OTP sent Successfully", Toast.LENGTH_SHORT).show();
                            afterSent();
                        } else if (response.contains("successfully")) {
                            Toast.makeText(OTPActivity.this, "OTP sent Successfully", Toast.LENGTH_SHORT).show();
                            afterSent();
                        } else if (response.contains("Invalid")) {
                            Toast.makeText(OTPActivity.this, "This number is not valid. Message cannot be sent", Toast.LENGTH_SHORT).show();
                        } else if (response.contains("DND")) {
                            Toast.makeText(OTPActivity.this, "This number has DND activated. Message cannot be sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OTPActivity.this, "OTP sending failed. Try again later", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.d("RESPONSE", "EXCEPTION-" + e.getMessage());
                        stopAnimation();
                        Toast.makeText(OTPActivity.this, "Error sending OTP", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("RESPONSE", "ERROR-" + error.getMessage());
                    stopAnimation();
                    Toast.makeText(OTPActivity.this, "Error sending OTP", Toast.LENGTH_SHORT).show();
                }
            });
            s.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(OTPActivity.this);
            requestQueue.add(s);
        } catch (Exception e) {
            Log.d("RESPONSE", "Request-" + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        if (dbRef != null) {
            if (alreadyRegListener != null)
                dbRef.removeEventListener(alreadyRegListener);
            if (checkOrAddListener != null)
                dbRef.removeEventListener(checkOrAddListener);
            if (duplicateListener != null)
                dbRef.removeEventListener(duplicateListener);
        }
        super.onDestroy();
    }
}
