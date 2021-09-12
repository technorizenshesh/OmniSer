package com.technorizen.omniser.fooddelivery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityFoodOrderHistoryBinding;
import com.technorizen.omniser.fooddelivery.adapters.AdapterMyOrders;
import com.technorizen.omniser.fooddelivery.models.ModelMyOrders;
import com.technorizen.omniser.grocerydelivery.activities.GroceryHomeActivity;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.pharmacydelivery.activities.PharmacyHomeActivity;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodOrderHistoryActivity extends AppCompatActivity {

    Context mContext = FoodOrderHistoryActivity.this;
    ActivityFoodOrderHistoryBinding binding;
    boolean isHistory;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String storeType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_food_order_history);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        ProjectUtil.changeStatusBarColor(FoodOrderHistoryActivity.this);
        storeType = getIntent().getStringExtra(AppConstant.STORE_TYPE);

        init();

    }

    private void init() {

            getMyOrders();

            binding.ivBack.setOnClickListener(v -> {
                if(AppConstant.GROCERY.equals(storeType)) {
                    startActivity(new Intent(mContext, GroceryHomeActivity.class));
                } else if(AppConstant.PHARMACY.equals(storeType)) {
                    startActivity(new Intent(mContext, PharmacyHomeActivity.class));
                } else {
                    startActivity(new Intent(mContext,FoodHomeActivity.class));
                }
            });

            binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                   getMyOrders();
                }
            });

    }

    private void getMyOrders() {

        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().getMyOrders(modelLogin.getResult().getId(),"","delivered");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {

                    String stringResponse = response.body().string();

                    Log.e("getMyOrder","response = " + stringResponse);
                    Log.e("getMyOrder","response = " + response);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {

                        ModelMyOrders modelMyOrders = new Gson().fromJson(stringResponse, ModelMyOrders.class);

                        AdapterMyOrders adapterMyOrders = new AdapterMyOrders(mContext,modelMyOrders.getResult(),true,storeType);
                        binding.rvMyHistory.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvMyHistory.setAdapter(adapterMyOrders);

                    } else {
                        AdapterMyOrders adapterMyOrders = new AdapterMyOrders(mContext,null,true,storeType);
                        binding.rvMyHistory.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvMyHistory.setAdapter(adapterMyOrders);
                        Toast.makeText(mContext, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    ProjectUtil.pauseProgressDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
            }
        });

    }

    private void itemDetailDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.food_order_summary_dialog);

        ImageView ivBack = dialog.findViewById(R.id.ivBack);

        ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }


}