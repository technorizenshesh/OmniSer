package com.technorizen.omniser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityChnagePasswordBinding;
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

public class ChnagePasswordActivity extends AppCompatActivity {

    Context mContext = ChnagePasswordActivity.this;
    ActivityChnagePasswordBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chnage_password);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        ProjectUtil.changeStatusBarColor(ChnagePasswordActivity.this);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.tvSubmit.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.etOldPass.getText().toString().trim())){
                Toast.makeText(mContext, "Please enter old passsword", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etNewPass.getText().toString().trim())){
                Toast.makeText(mContext, "Please enter new passsword", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etConfirmPass.getText().toString().trim())){
                Toast.makeText(mContext, "Please enter confirm passsword", Toast.LENGTH_SHORT).show();
            } else if(!binding.etNewPass.getText().toString().trim().equals(binding.etConfirmPass.getText().toString().trim())){
                Toast.makeText(mContext, "Password Not Matched", Toast.LENGTH_SHORT).show();
            } else {
                changePassword();
            }
        });
    }

    private void changePassword() {
        ProjectUtil.showProgressDialog(mContext,false,"Please wait...");
        Call<ResponseBody> call = ApiFactory.loadInterface().changePass(modelLogin.getResult().getId()
                ,binding.etOldPass.getText().toString().trim(),binding.etNewPass.getText().toString().trim());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if(jsonObject.getString("status").equals("1")) {
                        Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(mContext,DashboardActivity.class));
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }
        });

    }


}