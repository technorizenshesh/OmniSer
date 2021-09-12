package com.technorizen.omniser.grocerydelivery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityGraceryShopDetailBinding;
import com.technorizen.omniser.fooddelivery.activities.FoodItemDetailsActivity;
import com.technorizen.omniser.fooddelivery.activities.MyFoodCartActivity;
import com.technorizen.omniser.fooddelivery.adapters.AdapterResCat;
import com.technorizen.omniser.fooddelivery.models.ModelResDetails;
import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;
import com.technorizen.omniser.grocerydelivery.adapters.AdapterGrceryCategoryList;
import com.technorizen.omniser.grocerydelivery.adapters.AdapterGroceryCat;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.pharmacydelivery.adapters.AdapterPharmacyCategoryList;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraceryShopDetailActivity extends AppCompatActivity {

    public static HashMap<String, ModelResTypeItems.Result.Item_data> cartHash = new HashMap<>();
    Context mContext = GraceryShopDetailActivity.this;
    ActivityGraceryShopDetailBinding binding;
    String grosId = "";
    ModelResDetails modelResDetails;
    SharedPref sharedPref;
    boolean checkAllZeroOrNot = false;
    ModelLogin modelLogin;
    ModelResTypeItems modelResTypeItems,modelResTypeItemsTemp;
    boolean isSearchVisible,isResLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_gracery_shop_detail);
        ProjectUtil.changeStatusBarColor(GraceryShopDetailActivity.this);

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        try {
            grosId = getIntent().getStringExtra("resid");
        } catch (Exception e) {}

        Log.e("dsfsdfds","grosId = " + grosId);
        // Toast.makeText(mContext, "grosId = " + grosId, Toast.LENGTH_SHORT).show();

        init();

        getRestaurantDetails();
        getAllItemsWithCategory();

    }

    private void init() {

        binding.ivLike.setOnClickListener(v -> {
            if(isResLike) {
                setLikeApi("false");
            } else {
                setLikeApi("true");
            }
        });

        binding.ivSearch.setOnClickListener(v -> {
            binding.etSearch.setText("");
            if(isSearchVisible) {
                isSearchVisible = false;
                binding.cvSearchView.setVisibility(View.GONE);
                binding.rvResCategory.setVisibility(View.VISIBLE);
            } else {
                isSearchVisible = true;
                binding.cvSearchView.setVisibility(View.VISIBLE);
                binding.rvResCategory.setVisibility(View.GONE);
            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if(query.length() == 0) {
                    if(modelResTypeItems != null) {
                        AdapterGroceryCat adapterResCatt = new AdapterGroceryCat(mContext,modelResTypeItems.getResult());
                        binding.rvResItems.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvResItems.setAdapter(adapterResCatt);
                    }
                }

                try {

                    ArrayList<ModelResTypeItems.Result> filteredItemsList;
                    HashMap<String,ModelResTypeItems.Result> hashTemp = new HashMap<>();

                    for (int i=0;i<modelResTypeItems.getResult().size();i++) {
                        for (int j=0;j<modelResTypeItems.getResult().get(i).getItem_data().size();j++) {

                            String text = modelResTypeItems.getResult().get(i).getItem_data()
                                    .get(j).getItem_name().toLowerCase();

                            if(text.contains(query)) {
                                hashTemp.put(modelResTypeItems.getResult().get(i).getId(),modelResTypeItems.getResult().get(i));
                                break;
                            }
                        }
                    }

                    filteredItemsList = new ArrayList<>(hashTemp.values());
                    AdapterGroceryCat adapterResCatt = new AdapterGroceryCat(mContext,filteredItemsList);
                    binding.rvResItems.setLayoutManager(new LinearLayoutManager(mContext));
                    binding.rvResItems.setAdapter(adapterResCatt);

                } catch (Exception e) {
                    AdapterGroceryCat adapterResCatt = new AdapterGroceryCat(mContext,null);
                    binding.rvResItems.setLayoutManager(new LinearLayoutManager(mContext));
                    binding.rvResItems.setAdapter(adapterResCatt);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}

        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllItemsWithCategory();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.rlCheckout.setOnClickListener(v -> {
            startActivity(new Intent(mContext,MyGroceryCartActivity.class));
        });


    }

    public void scrollItemToPosition(int position) {
        Log.e("fdsfdsfds","position = " + position);
        binding.rvResItems.scrollToPosition(position);
    }

    private void setLikeApi(String status) {

        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().setLike(grosId,status,modelLogin.getResult().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        if(status.equals("true")) {
                            isResLike = true;
                            binding.ivLike.setImageResource(R.drawable.ic_heart_redicon);
                        } else {
                            isResLike = false;
                            binding.ivLike.setImageResource(R.drawable.ic_heart_grayicon);
                        }
                    } else {

                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exceptiondgfsdgdsg","Exception = " + e.getMessage());
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

    @Override
    protected void onResume() {
        super.onResume();

        getCardCount();

        if(sharedPref.getCartHash(AppConstant.CARTHASH) != null) {
            cartHash = sharedPref.getCartHash(AppConstant.CARTHASH);
            if(cartHash != null) {
                binding.rlCheckout.setVisibility(View.VISIBLE);
                binding.tvCartCount.setText(String.valueOf(cartHash.size()));

                Log.e("cardhash",new Gson().toJson(cartHash));

            } else {
                binding.rlCheckout.setVisibility(View.GONE);
            }
        }

    }

    private void getCardCount() {

        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().getCartCountApi(modelLogin.getResult().getId(),AppConstant.GROCERY);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("asdfasf","responseString = " + responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        if(!"0".equals(jsonObject.getString("result"))) {
                            binding.rlCheckout.setVisibility(View.VISIBLE);
                            binding.tvCartCount.setText(jsonObject.getString("result"));
                            Log.e("asdfasf"," VISIBLE ");
                        } else {
                            Log.e("asdfasf"," GONE ");
                            binding.rlCheckout.setVisibility(View.GONE);
                        }

                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
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

    private void getRestaurantDetails() {
        checkAllZeroOrNot = false;
        // ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().getGroceryShopDetails(grosId,modelLogin.getResult().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        Log.e("kjkaskjdjsad","response = " + response);

                        modelResDetails = new Gson().fromJson(responseString, ModelResDetails.class);

                        if(modelResDetails.getResult().getSetLike().equals("true")) {
                            isResLike = true;
                            binding.ivLike.setImageResource(R.drawable.ic_heart_redicon);
                        } else {
                            isResLike = false;
                            binding.ivLike.setImageResource(R.drawable.ic_heart_grayicon);
                        }

                        Glide.with(mContext)
                                .load(modelResDetails.getResult().getImage())
                                .placeholder(R.drawable.loading)
                                .error(R.drawable.loading)
                                .apply(new RequestOptions()
                                        .override(500,500))
                                .into(binding.ivResImage);

                        binding.tvResName.setText(modelResDetails.getResult().getName());
                        binding.tvAddress.setText(modelResDetails.getResult().getAddress());
                        binding.tvTime.setText(modelResDetails.getResult().getOpen_time() + " " +
                                getString(R.string.to) + " " + modelResDetails.getResult().getClose_time());

                        // getAllItemsWithCategory();

                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void getAllItemsWithCategory() {
        checkAllZeroOrNot = false;
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().getResItemsWithType(grosId,modelLogin.getResult().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        modelResTypeItems = new Gson().fromJson(responseString, ModelResTypeItems.class);

                        Log.e("kjkhkhhklklasd","response = " + response);
                        Log.e("kjkhkhhklklasd","responseString = " + responseString);

                        AdapterGrceryCategoryList adapterResCatt1 = new AdapterGrceryCategoryList(mContext,modelResTypeItems.getResult());
                        binding.rvResCategory.setAdapter(adapterResCatt1);

                        AdapterGroceryCat adapterResCatt = new AdapterGroceryCat(mContext,modelResTypeItems.getResult());
                        binding.rvResItems.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvResItems.setAdapter(adapterResCatt);

                    } else {
                        AdapterGroceryCat adapterResCatt = new AdapterGroceryCat(mContext,null);
                        binding.rvResItems.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvResItems.setAdapter(adapterResCatt);
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exceptiondgfsdgdsg","Exception = " + e.getMessage());
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

    public void updateData(String cartCount) {
        if(!cartCount.equals("0")) {
            binding.rlCheckout.setVisibility(View.VISIBLE);
            binding.tvCartCount.setText(cartCount);
        } else {
            binding.rlCheckout.setVisibility(View.GONE);
        }
    }

}