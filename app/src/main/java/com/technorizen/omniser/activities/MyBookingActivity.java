package com.technorizen.omniser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityMyBookingBinding;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.ondemand.activities.MakeOnDemandRequestActivity;
import com.technorizen.omniser.ondemand.activities.ViewRequestDetailsActivity;
import com.technorizen.omniser.ondemand.adapters.AdapterBookings;
import com.technorizen.omniser.ondemand.models.ModelBooking;
import com.technorizen.omniser.taxi.adapters.AdapterTaxiHistory;
import com.technorizen.omniser.taxi.models.ModelTaxiHistory;
import com.technorizen.omniser.utils.Api;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookingActivity extends AppCompatActivity {

    Context mContext = MyBookingActivity.this;
    ActivityMyBookingBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ModelTaxiHistory modelTaxiHistory;
    AdapterTaxiHistory adapterTaxiHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_booking);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        ProjectUtil.changeStatusBarColor(MyBookingActivity.this);

        init();

        Log.e("hjsgdhjfgjds","user_id = " + modelLogin.getResult().getId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // makeRequestApiCall();
        getTaxiHistory();
    }

    private void init() {

        // makeRequestApiCall();

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTaxiHistory();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void getTaxiHistory() {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().getUserTaxiRequest(modelLogin.getResult().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);

                Log.e("jksdfghkjasf","response = " + response);
                Log.e("jksdfghkjasf","modelLogin.getResult().getId() = " + modelLogin.getResult().getId());

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        modelTaxiHistory = new Gson().fromJson(responseString,ModelTaxiHistory.class);

                        adapterTaxiHistory = new AdapterTaxiHistory(mContext,modelTaxiHistory.getResult(),MyBookingActivity.this::payNowClicked);
                        binding.rvTaxi.setAdapter(adapterTaxiHistory);
                        makeRequestApiCall();
                        // Toast.makeText(MyBookingActivity.this, "Success", Toast.LENGTH_SHORT).show();

                    } else {
                        makeRequestApiCall();
                        adapterTaxiHistory = new AdapterTaxiHistory(mContext,null,MyBookingActivity.this::payNowClicked);
                        binding.rvTaxi.setAdapter(adapterTaxiHistory);
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure","onFailure = " + t.getMessage());
            }

        });

    }

    public void payNowClicked(ModelTaxiHistory.Result data, String payMethod, int position) {
        if(payMethod.equalsIgnoreCase("cash") ||
                payMethod.equalsIgnoreCase("wallet")) {
            doPayment(data,data.getId(),payMethod,data.getEstimate_charge_amount(),position);
        } else {
            startActivity(new Intent(mContext, PaypalWebviewAct.class)
                    .putExtra("type",AppConstant.TAXI)
                    .putExtra("amount",data.getEstimate_charge_amount())
                    .putExtra("id",modelLogin.getResult().getId())
                    .putExtra("payType",payMethod)
                    .putExtra("data",data)
            );
        }
        // doPayment(data,data.getId(),payMethod,data.getEstimate_charge_amount(),position);
    }

    private void doPayment(ModelTaxiHistory.Result data,String requestId,String payMethod,String estimate_charge_amount,int position) {

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.loadInterface();

        Log.e("sfddsfdsfdsf","user_id = " + modelLogin.getResult().getId());

        HashMap<String,String> params = new HashMap<>();
        params.put("request_id",requestId);
        params.put("user_id",modelLogin.getResult().getId());
        params.put("total_amount",estimate_charge_amount);
        params.put("payment_type",payMethod);
        params.put("payment_id","");

        Log.e("hjadkjshakjdhkjas","params = " + params);

        Call<ResponseBody> call = api.doTaxiPayment(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);

                Log.e("kghkljsdhkljf","response = " + response);

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("kjagsdkjgaskjd","stringResponse = " + response);
                    Log.e("kjagsdkjgaskjd","stringResponse = " + stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                        modelTaxiHistory.getResult().get(position).setStatus("Paid");
                        adapterTaxiHistory = new AdapterTaxiHistory(mContext,modelTaxiHistory.getResult(),MyBookingActivity.this::payNowClicked);
                        binding.rvTaxi.setAdapter(adapterTaxiHistory);
                        Toast.makeText(mContext, "Payment Success", Toast.LENGTH_SHORT).show();
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                Log.e("kjagsdkjgaskjd","stringResponse = " + t.getMessage());
            }

        });

    }

    private void makeRequestApiCall() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().getUserRequest(modelLogin.getResult().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);

                Log.e("jksdfghkjasf","response = " + response);

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("jksdfghkjasf","responseString = " + responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelBooking modelBooking = new Gson().fromJson(responseString,ModelBooking.class);

                        AdapterBookings adapterBookings = new AdapterBookings(mContext,modelBooking.getResult());
                        binding.rvRequest.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvRequest.setAdapter(adapterBookings);

                        Toast.makeText(MyBookingActivity.this, "Success", Toast.LENGTH_SHORT).show();

                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure","onFailure = " + t.getMessage());
            }

        });

    }


}