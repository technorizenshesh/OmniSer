package com.technorizen.omniser.pharmacydelivery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.activities.DashboardActivity;
import com.technorizen.omniser.activities.PaypalWebviewAct;
import com.technorizen.omniser.databinding.ActivityPharmacyPayBinding;
import com.technorizen.omniser.fooddelivery.models.ModelCartItems;
import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;
import com.technorizen.omniser.grocerydelivery.activities.GroceryHomeActivity;
import com.technorizen.omniser.grocerydelivery.activities.GroceryPayActivity;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PharmacyPayActivity extends AppCompatActivity {

    Context mContext = PharmacyPayActivity.this;
    ActivityPharmacyPayBinding binding;
    String whichPaymentMethod = "";
    String total,storeId,dev_add,devlat,devlon,currentTime,currentDate;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    JSONArray cartItemArray;
    ModelResTypeItems modelResTypeItems;
    ArrayList<ModelResTypeItems.Result.Item_data> itemsdata = new ArrayList<>();
    String cartData,resId;
    boolean isAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_pharmacy_pay);

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        ProjectUtil.changeStatusBarColor(PharmacyPayActivity.this);

        // getResAcceptingOrderOrNot();
        getCartDetailsApi();

        currentTime = getCurrentTime();
        currentDate = getCurrentDate();

        total = getIntent().getStringExtra("total");
        storeId = getIntent().getStringExtra("storeid");
        dev_add = getIntent().getStringExtra("dev_add");
        devlat = getIntent().getStringExtra("dev_lat");
        devlon = getIntent().getStringExtra("dev_lon");

        Log.e("zksgfkjagsasd","total = " + total);
        Log.e("zksgfkjagsasd","storeId = " + storeId);
        Log.e("zksgfkjagsasd","dev_add = " + dev_add);
        Log.e("zksgfkjagsasd","devlat = " + devlat);
        Log.e("zksgfkjagsasd","devlon = " + devlon);

        init();

    }

    private void getCartDetailsApi() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().getCartItemsApi(modelLogin.getResult().getId()
                ,AppConstant.PHARMACY);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    Log.e("dgdfgdsfdsfs","response = " + stringResponse);
                    Log.e("dgdfgdsfdsfs","response = " + response);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {
                        ModelCartItems modelCartItems = new Gson().fromJson(stringResponse,ModelCartItems.class);

                        cartItemArray = new JSONObject(stringResponse).getJSONArray("result");

                        Log.e("zksgfkjagsasd","devlon = " + cartItemArray.toString());

                    } else {

                    }

                } catch (Exception e) {
                    ProjectUtil.pauseProgressDialog();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                // Log.e("anErroranError","anError = " + t.getMessage());
            }

        });

    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(new Date());
        return formattedDate;
    }

    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        String formattedDate = dateFormat.format(new Date());
        return formattedDate;
    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.rlDone.setOnClickListener(v -> {

            if(binding.cbCash.isChecked()) {
                bookingApi("cash");
            } else if(binding.cbWallet.isChecked()) {
                bookingApi("wallet");
            } else if(binding.cbOnline.isChecked()) {
                HashMap<String,String> param = new HashMap<>();
                param.put("payment_id","");
                param.put("user_id",modelLogin.getResult().getId());
                param.put("store_id",storeId);
                param.put("payment_type","online");
                param.put("total_amount",total);
                param.put("delivery_time",currentTime);
                param.put("delivery_address",dev_add);
                param.put("cart_item",cartItemArray.toString());
                param.put("delivery_lat",devlat);
                param.put("delivery_lon",devlon);
                param.put("delivery_date",currentDate);
                param.put("type",AppConstant.PHARMACY);
                startActivity(new Intent(mContext, PaypalWebviewAct.class)
                        .putExtra("type",AppConstant.PHARMACY)
                        .putExtra("amount",total)
                        .putExtra("id",modelLogin.getResult().getId())
                        .putExtra("param",param)
                );
                // bookingApi("online");
            } else {
                Toast.makeText(mContext, getString(R.string.please_select_payment_method), Toast.LENGTH_SHORT).show();
            }

//            if(isAccept) {
//            } else {
//                notAcceptOrders();
//            }

        });

        binding.cbCash.setOnClickListener(v -> {
            binding.cbOnline.setChecked(false);
            binding.cbWallet.setChecked(false);
        });

        binding.cbOnline.setOnClickListener(v -> {
            binding.cbCash.setChecked(false);
            binding.cbWallet.setChecked(false);
        });

        binding.cbWallet.setOnClickListener(v -> {
            binding.cbOnline.setChecked(false);
            binding.cbCash.setChecked(false);
        });

    }

    private void notAcceptOrders() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.not_accept_order_text));
        builder.setCancelable(true);
        builder.setPositiveButton(mContext.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void bookingApi(String payType) {
        ProjectUtil.showProgressDialog(mContext,false,mContext.getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().foodBookingApi(
                "",
                modelLogin.getResult().getId(),
                storeId,
                payType,total,currentTime,dev_add,
                cartItemArray.toString(),devlat,devlon,currentDate,AppConstant.PHARMACY
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    Log.e("responseresponse","response = " + stringResponse);
                    Log.e("responseresponse","response = " + response);

                    sharedPref.setRestaurantModel(AppConstant.RESTAURANT_DATA,null);
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {
                        //  ModelCartItems modelCartItems = new Gson().fromJson(stringResponse,ModelCartItems.class);
                        Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_SHORT).show();
                        finish();

                        Intent intent = new Intent(mContext, PharmacyHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        Toast.makeText(mContext, getString(R.string.insuffeient_amount), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    ProjectUtil.pauseProgressDialog();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("anErroranError","anError = " + t.getMessage());
            }

        });

    }
}