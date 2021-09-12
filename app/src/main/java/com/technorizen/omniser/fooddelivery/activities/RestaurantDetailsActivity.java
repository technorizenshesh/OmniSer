package com.technorizen.omniser.fooddelivery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.activities.SearchItemsAct;
import com.technorizen.omniser.databinding.ActivityRestaurantDetailsBinding;
import com.technorizen.omniser.fooddelivery.adapters.AdapterResCat;
import com.technorizen.omniser.fooddelivery.adapters.AdapterResCategoryList;
import com.technorizen.omniser.fooddelivery.models.ModelResDetails;
import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;
import com.technorizen.omniser.utils.UpdateFoodItemsModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailsActivity extends AppCompatActivity
        implements UpdateFoodItemsModel {

    public static HashMap<String,ModelResTypeItems.Result.Item_data> cartHash = new HashMap<>();
    Context mContext = RestaurantDetailsActivity.this;
    ActivityRestaurantDetailsBinding binding;
    String resId = "";
    ModelResDetails modelResDetails;
    SharedPref sharedPref;
    AdapterResCategoryList categoryList;
    boolean checkAllZeroOrNot = false;
    ModelLogin modelLogin;
    AdapterResCat adapterResCatt;
    ModelResTypeItems modelResTypeItems,modelResTypeItemsTemp;
    boolean isSearchVisible, isResLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant_details);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        ProjectUtil.changeStatusBarColor(RestaurantDetailsActivity.this);

        try {
            resId = getIntent().getStringExtra("resid");
        } catch (Exception e) {
        }

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.ivLike.setOnClickListener(v -> {
            if (isResLike) {
                setLikeApi("false");
            } else {
                setLikeApi("true");
            }
        });

        binding.ivSearch.setOnClickListener(v -> {
            startActivity(new Intent(mContext, SearchItemsAct.class)
                    .putExtra("resid", resId)
            );
//            binding.etSearch.setText("");
//            if(isSearchVisible) {
//                isSearchVisible = false;
//                binding.cvSearchView.setVisibility(View.GONE);
//                binding.rvResCategory.setVisibility(View.VISIBLE);
//            } else {
//                isSearchVisible = true;
//                binding.cvSearchView.setVisibility(View.VISIBLE);
//                binding.rvResCategory.setVisibility(View.GONE);
//            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.length() == 0) {
                    if (modelResTypeItems != null) {
                        AdapterResCat adapterResCatt = new AdapterResCat(mContext, modelResTypeItems.getResult());
                        binding.rvResItems.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvResItems.setAdapter(adapterResCatt);
                    }
                }

                try {

                    ArrayList<ModelResTypeItems.Result> filteredItemsList;
                    HashMap<String, ModelResTypeItems.Result> hashTemp = new HashMap<>();

                    for (int i = 0; i < modelResTypeItems.getResult().size(); i++) {
                        for (int j = 0; j < modelResTypeItems.getResult().get(i).getItem_data().size(); j++) {

                            String text = modelResTypeItems.getResult().get(i).getItem_data()
                                    .get(j).getItem_name().toLowerCase();

                            if (text.contains(query)) {
                                hashTemp.put(modelResTypeItems.getResult().get(i).getId(), modelResTypeItems.getResult().get(i));
                                break;
                            }
                        }
                    }

                    filteredItemsList = new ArrayList<>(hashTemp.values());
                    AdapterResCat adapterResCatt = new AdapterResCat(mContext, filteredItemsList);
                    binding.rvResItems.setLayoutManager(new LinearLayoutManager(mContext));
                    binding.rvResItems.setAdapter(adapterResCatt);

                } catch (Exception e) {
                    AdapterResCat adapterResCatt = new AdapterResCat(mContext, modelResTypeItems.getResult());
                    binding.rvResItems.setLayoutManager(new LinearLayoutManager(mContext));
                    binding.rvResItems.setAdapter(adapterResCatt);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

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
            startActivity(new Intent(mContext, MyFoodCartActivity.class));
        });

    }

    private void setLikeApi(String status) {

        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().setLike(resId, status, modelLogin.getResult().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        if (status.equals("true")) {
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
                    Log.e("Exceptiondgfsdgdsg", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure", "onFailure = " + t.getMessage());
            }

        });
    }

    private class DataFetcher extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.e("kgshgdhfghj", "query = " + params.length);
            if (params[0].length() == 0) {
                if (modelResTypeItems != null) {
                    AdapterResCat adapterResCatt = new AdapterResCat(mContext, modelResTypeItems.getResult());
                    binding.rvResItems.setLayoutManager(new LinearLayoutManager(mContext));
                    binding.rvResItems.setAdapter(adapterResCatt);
                }
                return "";
            }

            try {

                ArrayList<ModelResTypeItems.Result> filteredItemsList =
                        new ArrayList<ModelResTypeItems.Result>();

                for (int i = 0; i < modelResTypeItems.getResult().size(); i++) {
                    for (int j = 0; j < modelResTypeItems.getResult().get(i).getItem_data().size(); j++) {

                        String text = modelResTypeItems.getResult().get(i).getItem_data()
                                .get(j).getItem_name().toLowerCase();

                        if (text.contains(params[0])) {
                            Log.e("asdfdasfasf", "getItem_data = i = " + i);
                            Log.e("asdfdasfasf", "text = text = " + text);
                            Log.e("asdfdasfasf", "query = query = " + params[0].toString());

                            if (filteredItemsList.size() != 0) {
                                for (int k = 0; k < filteredItemsList.size(); k++) {
                                    if (!(filteredItemsList.get(i).getId()
                                            .equals(modelResTypeItems.getResult().get(i)))) {
                                        filteredItemsList.add(modelResTypeItems.getResult().get(i));
                                    }
                                }
                            } else {
                                filteredItemsList.add(modelResTypeItems.getResult().get(i));
                            }

                        }
                    }
                }

                AdapterResCat adapterResCatt = new AdapterResCat(mContext, filteredItemsList);
                binding.rvResItems.setLayoutManager(new LinearLayoutManager(mContext));
                binding.rvResItems.setAdapter(adapterResCatt);

            } catch (Exception e) {
                AdapterResCat adapterResCatt = new AdapterResCat(mContext, modelResTypeItems.getResult());
                binding.rvResItems.setLayoutManager(new LinearLayoutManager(mContext));
                binding.rvResItems.setAdapter(adapterResCatt);
            }
            return "";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getCardCount();

        if (sharedPref.getCartHash(AppConstant.CARTHASH) != null) {
            cartHash = sharedPref.getCartHash(AppConstant.CARTHASH);
            if (cartHash != null) {
                binding.rlCheckout.setVisibility(View.VISIBLE);
                binding.tvCartCount.setText(String.valueOf(cartHash.size()));

                Log.e("cardhash", new Gson().toJson(cartHash));

            } else {
                binding.rlCheckout.setVisibility(View.GONE);
            }
        }

        getRestaurantDetails();

    }

    private void getCardCount() {

        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().getCartCountApi(modelLogin.getResult().getId(), AppConstant.FOOD);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("asdfasf", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        if (!"0".equals(jsonObject.getString("result"))) {
                            binding.rlCheckout.setVisibility(View.VISIBLE);
                            binding.tvCartCount.setText(jsonObject.getString("result"));
                            Log.e("asdfasf", " VISIBLE ");
                        } else {
                            Log.e("asdfasf", " GONE ");
                            binding.rlCheckout.setVisibility(View.GONE);
                        }

                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure", "onFailure = " + t.getMessage());
            }

        });

    }

    private void getRestaurantDetails() {
        checkAllZeroOrNot = false;
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().getRestaurantDetails(resId, modelLogin.getResult().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        Log.e("kjkaskjdjsad", "response = " + response);

                        modelResDetails = new Gson().fromJson(responseString, ModelResDetails.class);

                        if (modelResDetails.getResult().getSetLike().equals("true")) {
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
                                        .override(500, 500))
                                .into(binding.ivResImage);

                        binding.tvResName.setText(modelResDetails.getResult().getName());
                        binding.tvAddress.setText(modelResDetails.getResult().getAddress());
                        binding.tvTime.setText(modelResDetails.getResult().getOpen_time() + " " +
                                getString(R.string.to) + " " + modelResDetails.getResult().getClose_time());

                        getAllItemsWithCategory();

                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure", "onFailure = " + t.getMessage());
            }

        });

    }

    private void getAllItemsWithCategory() {
        checkAllZeroOrNot = false;
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Call<ResponseBody> call = null;

        Log.e("asdasdasf", "resId = " + resId);
        Log.e("asdasdasf", "modelLogin.getResult().getId() = " + modelLogin.getResult().getId());

        call = ApiFactory.loadInterface().getResItemsWithType(resId, modelLogin.getResult().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        modelResTypeItems = new Gson().fromJson(responseString, ModelResTypeItems.class);

                        binding.rvResItems.setVisibility(View.VISIBLE);
                        binding.rvResCategory.setVisibility(View.VISIBLE);
                        isSearchVisible = false;
                        binding.cvSearchView.setVisibility(View.GONE);

                        Log.e("kjkhkhhklklasd", "response = " + response);
                        Log.e("kjkhkhhklklasd", "responseString = " + responseString);

                        categoryList = new AdapterResCategoryList(mContext, modelResTypeItems.getResult());
                        binding.rvResCategory.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                        binding.rvResCategory.setAdapter(categoryList);

                        Log.e("fsdfsdfds", "modelResTypeItems.getResult().size() = " + modelResTypeItems.getResult().size());

                        AdapterResCat adapterResCatt = new AdapterResCat(mContext, modelResTypeItems.getResult());
                        binding.rvResItems.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvResItems.setAdapter(adapterResCatt);

                    } else {
                        adapterResCatt = new AdapterResCat(mContext, null);
                        binding.rvResItems.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvResItems.setAdapter(adapterResCatt);
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exceptiondgfsdgdsg", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure", "onFailure = " + t.getMessage());
            }

        });

    }

    public void scrollItemToPosition(int position) {
        Log.e("fdsfdsfds", "position = " + position);
        binding.rvResItems.scrollToPosition(position);
    }

    public void updateData(String cartCount) {
//      if(modelResTypeItems.getResult() != null) {
//          AdapterResCat adapterResCatt = new AdapterResCat(mContext,modelResTypeItems.getResult());
//          binding.rvResItems.setLayoutManager(new LinearLayoutManager(mContext));
//          binding.rvResItems.setAdapter(adapterResCatt);
//      }
        if (!cartCount.equals("0")) {
            binding.rlCheckout.setVisibility(View.VISIBLE);
            binding.tvCartCount.setText(cartCount);
        } else {
            binding.rlCheckout.setVisibility(View.GONE);
        }
    }

    @Override
    public void updatedResItem(ArrayList<ModelResTypeItems.Result.Item_data> dataList, int itemPosition) {
        try {
            ModelResTypeItems modelTemp = sharedPref.getRestaurantModel(AppConstant.RESTAURANT_DATA);

            modelTemp.getResult().get(itemPosition).setItem_data(dataList);

            sharedPref.setRestaurantModel(AppConstant.RESTAURANT_DATA, modelTemp);

            checkAllZeroOrNot = false;
            for (int i = 0; i < modelTemp.getResult().size(); i++) {
                for (int j = 0; j < modelTemp.getResult().get(i).getItem_data().size(); j++) {
                    if (!modelTemp.getResult().get(i).getItem_data().get(j).getItem_quantity().equals("0")) {
                        checkAllZeroOrNot = true;
                    }
                    Log.e("updated_list", modelTemp.getResult().get(i).getItem_data().get(j).getItem_name());
                    Log.e("updated_list", modelTemp.getResult().get(i).getItem_data().get(j).getItem_quantity());
                }
            }

            if (checkAllZeroOrNot) {
                binding.rlViewCart.setVisibility(View.VISIBLE);
            } else {
                binding.rlViewCart.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            ModelResTypeItems modelTemp = modelResTypeItems;

            modelTemp.getResult().get(itemPosition).setItem_data(dataList);

            sharedPref.setRestaurantModel(AppConstant.RESTAURANT_DATA, modelTemp);

            checkAllZeroOrNot = false;
            for (int i = 0; i < modelTemp.getResult().size(); i++) {
                for (int j = 0; j < modelTemp.getResult().get(i).getItem_data().size(); j++) {
                    if (!modelTemp.getResult().get(i).getItem_data().get(j).getItem_quantity().equals("0")) {
                        checkAllZeroOrNot = true;
                    }
                    Log.e("updated_list", modelTemp.getResult().get(i).getItem_data().get(j).getItem_name());
                    Log.e("updated_list", modelTemp.getResult().get(i).getItem_data().get(j).getItem_quantity());
                }
            }

            if (checkAllZeroOrNot) {
                binding.rlViewCart.setVisibility(View.VISIBLE);
            } else {
                binding.rlViewCart.setVisibility(View.GONE);
            }


        }

    }

}