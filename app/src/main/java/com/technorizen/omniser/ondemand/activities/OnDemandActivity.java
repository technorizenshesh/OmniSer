package com.technorizen.omniser.ondemand.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.adapters.AdapterOnDemand;
import com.technorizen.omniser.databinding.ActivityOnDemandBinding;
import com.technorizen.omniser.ondemandmodels.ModelOnDemand;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.ProjectUtil;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnDemandActivity extends AppCompatActivity {

    Context mContext = OnDemandActivity.this;
    ActivityOnDemandBinding binding;
    ModelOnDemand modelOnDemand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_on_demand);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        getAllOnDemandServices();

    }

    private void getAllOnDemandServices() {
        ProjectUtil.showProgressDialog(mContext,false,"Please wait...");
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().getAllOnDemands();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("jksdfghkjasf","response = " + response);
                    Log.e("jksdfghkjasf","responseString = " + responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        modelOnDemand = new Gson().fromJson(responseString, ModelOnDemand.class);

                        AdapterOnDemand adapterOnDemand = new AdapterOnDemand(mContext,modelOnDemand.getResult());
                        binding.rvOnDemand.setLayoutManager(new GridLayoutManager(mContext,3));
                        binding.rvOnDemand.setAdapter(adapterOnDemand);

                        Toast.makeText(OnDemandActivity.this, "Success", Toast.LENGTH_SHORT).show();

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

}