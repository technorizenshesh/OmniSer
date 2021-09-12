package com.technorizen.omniser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityForgotPassBinding;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.ProjectUtil;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassActivity extends AppCompatActivity {

    Context mContext = ForgotPassActivity.this;
    ActivityForgotPassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_forgot_pass);
        ProjectUtil.changeStatusBarColor(ForgotPassActivity.this);
        init();
    }

    private void init() {

        binding.rlSubmit.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.email.getText().toString().trim())){
                Toast.makeText(mContext, "Please enter email", Toast.LENGTH_SHORT).show();
            } else {
                forgotPassApiCall();
            }
        });

    }

    private void forgotPassApiCall() {

        ProjectUtil.showProgressDialog(mContext,false,"Please wait...");
        Call<ResponseBody> call = ApiFactory.loadInterface().forgotPass(binding.email.getText().toString().trim());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        startActivity(new Intent(mContext,LoginActivity.class));
                        finish();

                        Toast.makeText(ForgotPassActivity.this, "Reset Password Link send to your mail-id", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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

}