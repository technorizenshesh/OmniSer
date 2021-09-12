package com.technorizen.omniser.pharmacydelivery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityPharmacyCartBinding;
import com.technorizen.omniser.fooddelivery.activities.SetFoodDeliveryLocationActivity;
import com.technorizen.omniser.fooddelivery.models.ModelCartItems;
import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;
import com.technorizen.omniser.grocerydelivery.activities.MyGroceryCartActivity;
import com.technorizen.omniser.grocerydelivery.activities.SetGroceryFoodDeliveryActivity;
import com.technorizen.omniser.grocerydelivery.adapters.AdapterGroceryCartItems;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.pharmacydelivery.adapters.AdapterPharmacyCart;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;
import com.technorizen.omniser.utils.UpdateTotalPayInterface;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PharmacyCartActivity extends AppCompatActivity implements UpdateTotalPayInterface {

    Context mContext = PharmacyCartActivity.this;
    ActivityPharmacyCartBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    public static String storeId = null;
    ModelResTypeItems modelResTypeItems;
    ArrayList<ModelResTypeItems.Result.Item_data> tempSharedModelItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_pharmacy_cart);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        ProjectUtil.changeStatusBarColor(PharmacyCartActivity.this);

        init();

    }

    private void init() {

        getCartDetailsApi();

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnContinue.setOnClickListener(v -> {
            if(storeId == null) {
                Toast.makeText(mContext, getString(R.string.please_add_item), Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(mContext, SetPharmacyDeliveryActivity.class)
                        .putExtra("total",binding.itemPlusDevCharges.getText().toString()
                                .trim().replace("$",""))
                        .putExtra("storeid",storeId)
                );
            }
        });

    }

    private void getCartDetailsApi() {
        ProjectUtil.showProgressDialog(mContext,true,mContext.getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().getCartItemsApi(modelLogin.getResult().getId(),AppConstant.PHARMACY);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    Log.e("responseresponse","response = " + stringResponse);
                    Log.e("responseresponse","response = " + response);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {
                        ModelCartItems modelCartItems = new Gson().fromJson(stringResponse,ModelCartItems.class);

                        binding.itemsTotal.setText(AppConstant.DOLLER_SIGN + modelCartItems.getAll_total_price());
                        binding.itemPlusDevCharges.setText(AppConstant.DOLLER_SIGN + modelCartItems.getTotal_pay());
                        binding.deliveryfee.setText(AppConstant.DOLLER_SIGN + modelCartItems.getDelivery_charges());

                        AdapterPharmacyCart adapterCartItems = new AdapterPharmacyCart(mContext,modelCartItems.getResult());
                        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvCartItems.setAdapter(adapterCartItems);

                        Glide.with(mContext).load(modelCartItems.getImage())
                                .apply(new RequestOptions().override(300,300))
                                .placeholder(R.drawable.res_icon_gray)
                                .into(binding.ivResImage);

                        binding.tvResName.setText(modelCartItems.getStore_name());
                        binding.tvAddress.setText(modelCartItems.getStore_address() + " " + modelCartItems.getStore_lendmark_address());

                    } else {
                        binding.btnContinue.setVisibility(View.INVISIBLE);
                        binding.itemsTotal.setText(AppConstant.DOLLER_SIGN + "0");
                        binding.itemPlusDevCharges.setText(AppConstant.DOLLER_SIGN + "0");
                        binding.deliveryfee.setText(AppConstant.DOLLER_SIGN + "0");
                        // Toast.makeText(mContext, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
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

    public void hideContinue(ArrayList<ModelCartItems.Result> cartItemList) {
        if(cartItemList == null || cartItemList.size() == 0){
            binding.btnContinue.setVisibility(View.GONE);
        } else {
            binding.btnContinue.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void updateCartData(String itemTotal, String devCharge, String totalPlusDev) {
        getCartDetailsApi();
    }

    @Override
    public void getStoreId(String storeIdNew) {
        storeId = storeIdNew;
    }

}