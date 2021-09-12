package com.technorizen.omniser.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityVerifyOtpBinding;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class VerifyOtpActivity extends AppCompatActivity {

    Context mContext = VerifyOtpActivity.this;
    ActivityVerifyOtpBinding binding;
    String mobile="";
    HashMap<String,String> paramHash;
    HashMap<String, File> fileHashMap;
    String id;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_verify_otp);
        sharedPref = SharedPref.getInstance(mContext);
        ProjectUtil.changeStatusBarColor(VerifyOtpActivity.this);

        // FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        paramHash = new HashMap<>();
        fileHashMap = new HashMap<>();

        paramHash = (HashMap<String, String>) getIntent().getSerializableExtra("resgisterHashmap");
        fileHashMap = (HashMap<String, File>) getIntent().getSerializableExtra("resgisterfileHashmap");
        mobile = getIntent().getStringExtra("mobile");

        init();

        sendVerificationCode();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 1) {
                    binding.et2.setText("");
                    binding.et2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}


        });

        binding.et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 1) {
                    binding.et3.setText("");
                    binding.et3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}


        });

        binding.et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 1) {
                    binding.et4.setText("");
                    binding.et4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}


        });

        binding.et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 1) {
                    binding.et5.setText("");
                    binding.et5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}


        });

        binding.et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 1) {
                    binding.et6.setText("");
                    binding.et6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}

        });

        binding.rlVerify.setOnClickListener(v -> {

            if(TextUtils.isEmpty(binding.et1.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_com_otp), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.et2.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_com_otp), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.et3.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_com_otp), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.et4.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_com_otp), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.et5.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_com_otp), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.et6.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_com_otp), Toast.LENGTH_SHORT).show();
            } else {
                String finalOtp =
                        binding.et1.getText().toString().trim() +
                        binding.et2.getText().toString().trim() +
                        binding.et3.getText().toString().trim() +
                        binding.et4.getText().toString().trim() +
                        binding.et5.getText().toString().trim() +
                        binding.et6.getText().toString().trim();

                ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, finalOtp);
                signInWithPhoneAuthCredential(credential);

            }

        });

        binding.tvResend.setOnClickListener(v -> {
            sendVerificationCode();
        });

    }

    private void sendVerificationCode() {

        binding.tvVerifyText.setText("We have send you an SMS on " + mobile + " with 6 digit verification code.");

        new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tvResend.setText("" + millisUntilFinished/1000);
                binding.tvResend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                binding.tvResend.setText(mContext.getString(R.string.resend));
                binding.tvResend.setEnabled(true);
            }
        }.start();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile.replace(" ",""), // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        VerifyOtpActivity.this.id = id;
                        Toast.makeText(mContext, "Please enter 6 digit verification code", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        ProjectUtil.pauseProgressDialog();
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        ProjectUtil.pauseProgressDialog();
                        Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });        // OnVerificationStateChangedCallbacks

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ProjectUtil.pauseProgressDialog();
                            // Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = task.getResult().getUser();
                            signUpApiCall();

                        } else {
                            ProjectUtil.pauseProgressDialog();
                            Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {}

                        }
                    }
                });

    }

    private void signUpApiCall() {

        OkHttpClient okHttpClient = new OkHttpClient()
                .newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        AndroidNetworking.upload(AppConstant.BASE_URL + "signup")
                .addMultipartParameter(paramHash)
                .addMultipartFile(fileHashMap)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        ProjectUtil.pauseProgressDialog();

                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getString("status").equals("1")) {

                                Log.e("asfddasfasdf","response = " + response);

                                modelLogin = new Gson().fromJson(response, ModelLogin.class);

                                sharedPref.setBooleanValue(AppConstant.IS_REGISTER,true);
                                sharedPref.setUserDetails(AppConstant.USER_DETAILS,modelLogin);

                                startActivity(new Intent(mContext,DashboardActivity.class));
                                finish();
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(mContext, "Email Already Exist", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtil.pauseProgressDialog();
                    }

                });
    }


}