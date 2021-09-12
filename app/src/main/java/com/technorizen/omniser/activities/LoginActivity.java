package com.technorizen.omniser.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityLoginBinding;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Context mContext = LoginActivity.this;
    ActivityLoginBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String registerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        sharedPref = SharedPref.getInstance(mContext);
        ProjectUtil.changeStatusBarColor(LoginActivity.this);

        FirebaseInstanceId
                .getInstance()
                .getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                if (!task.isSuccessful()) {
                    return;
                }

                // Get the Instance ID token //
                String token = task.getResult().getToken();
                registerId = token;

            }

        });

        init();

    }

    private void init() {

        binding.tvForgotPass.setOnClickListener(v -> {
            startActivity(new Intent(mContext,ForgotPassActivity.class));
        });

        binding.rlRegister.setOnClickListener(v -> {
            startActivity(new Intent(mContext,RegisterActivity.class));
        });

        binding.rlLogin.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.email.toString().trim())) {
                Toast.makeText(mContext, "Please enter email", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.password.toString().trim())) {
                Toast.makeText(mContext, "Please enter password", Toast.LENGTH_SHORT).show();
            } else {
                loginApiCall();
                // startActivity(new Intent(mContext,DashBoardActivity.class));
            }
        });

    }

    private void loginApiCall() {

        ProjectUtil.showProgressDialog(mContext,false,"Please wait...");
        Call<ResponseBody> call = null;

        Log.e("sdfsdfdsfdfds","registerId = " + registerId);

        if(binding.email.getText().toString().trim().contains("@")) {
            call = ApiFactory.loadInterface().login(binding.email.getText().toString()
                    ,binding.password.getText().toString().trim(),"","","","User",registerId);
        } else {
            call = ApiFactory.loadInterface().login(""
                    ,binding.password.getText().toString().trim(),binding.email.getText().toString(),"","","User",registerId);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);
                        
                        if("User".equals(modelLogin.getResult().getType())) {
                            sharedPref.setBooleanValue(AppConstant.IS_REGISTER,true);
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS,modelLogin);

                            startActivity(new Intent(mContext,DashboardActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure","onFailure = " + t.getMessage());
            }
        });

    }

    @Override
    public void onBackPressed() {
        exitAppDialog();
    }

    private void exitAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure you want to close the App?")
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}