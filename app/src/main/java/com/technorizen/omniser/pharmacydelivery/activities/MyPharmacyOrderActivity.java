package com.technorizen.omniser.pharmacydelivery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityMyPharmacyOrderBinding;
import com.technorizen.omniser.fooddelivery.activities.FoodHomeActivity;
import com.technorizen.omniser.fooddelivery.activities.MyOrdersActivity;
import com.technorizen.omniser.fooddelivery.adapters.AdapterMyOrders;
import com.technorizen.omniser.fooddelivery.models.ModelMyOrders;
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

public class MyPharmacyOrderActivity extends AppCompatActivity {

    Context mContext = MyPharmacyOrderActivity.this;
    ActivityMyPharmacyOrderBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_pharmacy_order);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        ProjectUtil.changeStatusBarColor(MyPharmacyOrderActivity.this);

        init();

    }

    private void init() {
        //getMyOrders();

        binding.ivBack.setOnClickListener(v -> {
            startActivity(new Intent(mContext, FoodHomeActivity.class));
        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // getMyOrders();
            }
        });

    }

//    private void getMyOrders() {
//
//        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
//        Call<ResponseBody> call = ApiFactory.loadInterface().getMyOrders(modelLogin.getResult().getId(),"");
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                ProjectUtil.pauseProgressDialog();
//                binding.swipLayout.setRefreshing(false);
//                try {
//
//                    String stringResponse = response.body().string();
//
//                    Log.e("getMyOrder","response = " + stringResponse);
//                    Log.e("getMyOrder","response = " + response);
//
//                    JSONObject jsonObject = new JSONObject(stringResponse);
//                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {
//
//                        ModelMyOrders modelMyOrders = new Gson().fromJson(stringResponse, ModelMyOrders.class);
//
//                        AdapterMyOrders adapterMyOrders = new AdapterMyOrders(mContext,modelMyOrders.getResult(),false);
//                        binding.rvMyOrders.setLayoutManager(new LinearLayoutManager(mContext));
//                        binding.rvMyOrders.setAdapter(adapterMyOrders);
//
//                    } else {
//                        AdapterMyOrders adapterMyOrders = new AdapterMyOrders(mContext,null,false);
//                        binding.rvMyOrders.setLayoutManager(new LinearLayoutManager(mContext));
//                        binding.rvMyOrders.setAdapter(adapterMyOrders);
//                        Toast.makeText(mContext, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (Exception e) {
//                    ProjectUtil.pauseProgressDialog();
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                ProjectUtil.pauseProgressDialog();
//                binding.swipLayout.setRefreshing(false);
//            }
//        });
//
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext,FoodHomeActivity.class));
    }


}