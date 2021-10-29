package com.technorizen.omniser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivitySearchItemsBinding;
import com.technorizen.omniser.fooddelivery.adapters.AdapterResCat;
import com.technorizen.omniser.fooddelivery.adapters.AdapterResCategoryList;
import com.technorizen.omniser.fooddelivery.adapters.AdapterResItems;
import com.technorizen.omniser.fooddelivery.adapters.AdapterResItemsSearch;
import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchItemsAct extends AppCompatActivity {

    Context mContext = SearchItemsAct.this;
    ActivitySearchItemsBinding binding;
    AdapterResItemsSearch adapterResItems;
    String resId = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String type = "";
    ArrayList<ModelResTypeItems.Result.Item_data> itemData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_items);
        sharedPref = SharedPref.getInstance(mContext);
        type = getIntent().getStringExtra("type");
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        resId = getIntent().getStringExtra("resid");
        itit();
    }

    private void itit() {

        searchItemsApiCall();

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.etSerach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                ArrayList<ModelResTypeItems.Result.Item_data> filteredData = new ArrayList<>();

                if (itemData != null) {
                    for (int i = 0; i < itemData.size(); i++) {
                        String text = itemData.get(i).getItem_name().toLowerCase();
                        if (text.contains(query)) {
                            filteredData.add(itemData.get(i));
                        }
                    }
                }

                if (filteredData.size() == 0) {
                    adapterResItems = new AdapterResItemsSearch(mContext, itemData, type);
                    binding.rvSearech.setAdapter(adapterResItems);
                    adapterResItems.notifyDataSetChanged();
                } else {
                    adapterResItems = new AdapterResItemsSearch(mContext, filteredData, type);
                    binding.rvSearech.setAdapter(adapterResItems);
                    adapterResItems.notifyDataSetChanged();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

    }

    private void searchItemsApiCall() {
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().getSerachItemsApis(resId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        Log.e("fsdfsdfds", "response = " + response);

                        JSONArray resultArray = jsonObject.getJSONArray("result");
                        JSONObject jsonObject1 = resultArray.getJSONObject(0);
                        String itemsString = jsonObject1.getString("item_data");

                        Log.e("fsdfsdfds", "itemsString = " + itemsString);

                        itemData = new Gson().fromJson(itemsString, new TypeToken<ArrayList<ModelResTypeItems.Result.Item_data>>() {
                        }.getType());

                        Log.e("fsdfsdfds", "modelResTypeItems.getResult().size() = " + itemData.size());

                        for (int i = 0; i < itemData.size(); i++) {
                            Log.e("fsdfsdfds", "modelResTypeItems = [" + i + "] = " + itemData.get(i).getItem_name());
                        }

                        LinearLayoutManager layoutManager =
                                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

                        adapterResItems = new AdapterResItemsSearch(mContext, itemData, type);
                        // binding.rvSearech.setLayoutManager(layoutManager);
                        binding.rvSearech.setAdapter(adapterResItems);

                    } else {
                        adapterResItems = new AdapterResItemsSearch(mContext, null, type);
                        binding.rvSearech.setAdapter(adapterResItems);
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exceptiondgfsdgdsg", "Exception = " + e.getMessage());
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

}