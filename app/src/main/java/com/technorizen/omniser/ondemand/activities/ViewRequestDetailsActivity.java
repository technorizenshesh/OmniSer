package com.technorizen.omniser.ondemand.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.PointerIconCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.technorizen.omniser.R;
import com.technorizen.omniser.activities.DashboardActivity;
import com.technorizen.omniser.databinding.ActivityViewRequestDetailsBinding;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.ondemand.adapters.AdapterAvailableProvider;
import com.technorizen.omniser.ondemand.models.ModelAvailProviders;
import com.technorizen.omniser.ondemand.models.ModelBooking;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;
import com.technorizen.omniser.utils.UpdateSubcatInterface;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewRequestDetailsActivity extends AppCompatActivity implements UpdateSubcatInterface {

    Context mContext = ViewRequestDetailsActivity.this;
    ActivityViewRequestDetailsBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ModelAvailProviders modelAvailProviders;
    ModelBooking.Result requestDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_view_request_details);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        requestDetails = new Gson().fromJson(String.valueOf(getIntent().getSerializableExtra("data")), ModelBooking.Result.class);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.tvServiceName.setText(requestDetails.getService_details().getSub_cat_name());

        Glide.with(mContext).load(requestDetails.getService_details().getImage())
                .placeholder(R.drawable.loading)
                .apply(new RequestOptions().override(300,300))
                .into(binding.ivSreviceImg);

        binding.tvRequestAddress.setText(requestDetails.getDelivery_address());
        binding.tvRequestDateTime.setText(requestDetails.getService_time());
        binding.tvRequestDesp.setText(requestDetails.getDescription());

        Log.e("hjsdhfjhajfd","requestId = " + requestDetails.getId());

        getRequestDetails();

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRequestDetails();
            }
        });

//        binding.btConfirm.setOnClickListener(v -> {
//            finish();
//            startActivity(new Intent(mContext, DashboardActivity.class));
//        });

    }

    private void getRequestDetails() {

        Log.e("kjashfjkdhsfkjlhdjs","modelLogin.getResult().getId() = " + modelLogin.getResult().getId());
        Log.e("kjashfjkdhsfkjlhdjs","requestId = " + requestDetails.getId());

        ProjectUtil.showProgressDialog(mContext,false,"Please wait...");
        Call<ResponseBody> call = ApiFactory.loadInterface().getUserRequestDetails(requestDetails.getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("ahjsgfhjs","response = " + response);

                    if(jsonObject.getString("status").equals("1")) {

                        modelAvailProviders = new Gson().fromJson(stringResponse, ModelAvailProviders.class);

                        AdapterAvailableProvider adapterAvailableProvider = new AdapterAvailableProvider(mContext,
                                modelAvailProviders.getResult().getProvider_details(),
                                modelAvailProviders.getResult().getService_price(),
                                requestDetails.getService_details().getImage(),
                                requestDetails.getId(),requestDetails.getStatus());
                        binding.rvAvailProvider.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvAvailProvider.setAdapter(adapterAvailableProvider);

                    } else {
                        Toast.makeText(ViewRequestDetailsActivity.this, "No Providers Available", Toast.LENGTH_SHORT).show();
                        AdapterAvailableProvider adapterAvailableProvider = new AdapterAvailableProvider(mContext,null,null,null,null,requestDetails.getStatus());
                        binding.rvAvailProvider.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvAvailProvider.setAdapter(adapterAvailableProvider);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("asjhdfklhasfkl","error = " + t.getMessage());
                binding.swipLayout.setRefreshing(false);
            }

        });
    }


    @Override
    public void updateProviders() {
        getRequestDetails();
    }

}