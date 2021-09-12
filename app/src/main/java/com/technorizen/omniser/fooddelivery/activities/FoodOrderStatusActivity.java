package com.technorizen.omniser.fooddelivery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityFoodOrderStatusBinding;
import com.technorizen.omniser.fooddelivery.models.ModelMyOrders;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.utils.Api;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodOrderStatusActivity extends AppCompatActivity {

    Context mContext = FoodOrderStatusActivity.this;
    ActivityFoodOrderStatusBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String orderId = "";
    String feedbackStatus = "";
    String storeName = "";
    String storeImage = "";
    String storeId = "";
    String status = "";
    String feedback = "",storeType = "";
    ModelMyOrders.Result data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_food_order_status);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        ProjectUtil.changeStatusBarColor(FoodOrderStatusActivity.this);

        storeType = getIntent().getStringExtra(AppConstant.STORE_TYPE);
        data = (ModelMyOrders.Result) getIntent().getSerializableExtra("data");

        init();

    }

    private void init() {

        orderStatusApiCall();

        Log.e("ffashdfasd","data = " + new Gson().toJson(data));

        if(data.getOrder_place_time() != null && !(data.getOrder_place_time().equals(""))) {
            binding.tvPlacedTime.setText(data.getOrder_place_time());
        }
        if(data.getReady_time() != null && !(data.getReady_time().equals(""))) {
            binding.tvResTime.setText(data.getReady_time());
        }
        if(data.getDiv_accept_time() != null && !(data.getDiv_accept_time().equals(""))) {
            binding.tvDevAcceptTime.setText(data.getDiv_accept_time());
        }
        if(data.getDiv_pick_time() != null && !(data.getDiv_pick_time().equals(""))) {
            binding.tvDevPickupTime.setText(data.getDiv_pick_time());
        }
        if(data.getDelivered_time() != null && !(data.getDelivered_time().equals(""))) {
            binding.tvDeliveredTime.setText(data.getDelivered_time());
        }

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btFeedback.setOnClickListener(v -> {
            feedbackDialog();
        });

        binding.swiplayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrderStatus();
            }
        });

    }

    private void feedbackDialog() {

        Dialog dialog  = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.feedback_dialog);

        CircleImageView resProfile = dialog.findViewById(R.id.resProfile);
        TextView tvResName = dialog.findViewById(R.id.tvResName);
        RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
        EditText tvComment = dialog.findViewById(R.id.tvComment);
        Button btSubmit = dialog.findViewById(R.id.btSubmit);
        ImageView iv_back = dialog.findViewById(R.id.iv_back);

        iv_back.setOnClickListener(v -> {
            finish();
        });

        Glide.with(mContext).load(storeImage)
                .placeholder(R.drawable.default_profile_icon)
                .apply(new RequestOptions())
                .override(500,500)
                .into(resProfile);

        tvResName.setText(storeName);

        btSubmit.setOnClickListener(v -> {
            if(ratingBar.getRating() == 0.0f) {
                Toast.makeText(mContext, getString(R.string.please_give_rating), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(tvComment.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_add_comment), Toast.LENGTH_SHORT).show();
            } else {
                feedbackApi(dialog,ratingBar.getRating(),tvComment.getText().toString().trim());
            }

        });

        dialog.show();

    }

    private void feedbackApi(Dialog dialog,float rating,String comment) {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().feedbackApi(
                orderId,
                modelLogin.getResult().getId(),
                storeId,
                String.valueOf(rating),
                comment);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    // Log.e("responseresponse","response = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {
                        dialog.dismiss();
                        binding.btFeedback.setVisibility(View.GONE);
                        finishAffinity();
                        startActivity(new Intent(mContext,MyOrdersActivity.class)
                         .putExtra(AppConstant.STORE_TYPE,storeType)
                        );
                        Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_SHORT).show();
                    } else {
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
                // Log.e("anErroranError","anError = " + t.getMessage());
            }

        });
    }

    private void orderStatusApiCall() {

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().getOrderStatus(modelLogin.getResult().getId(),data.getOrder_id());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    // Log.e("responseresponse","response = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {

                        JSONObject jsonResult = jsonObject.getJSONObject("result");
                        String status = jsonResult.getString("status");

                        if("pending".equalsIgnoreCase(status)) {
                            binding.readyCheck.setImageResource(R.drawable.res_icon_gray);
                            binding.devAcceptCheck.setImageResource(R.drawable.dev_gray_icon);
                            binding.devPivkupCheck.setImageResource(R.drawable.scotor_gray_icon);
                            binding.deliveredCheck.setImageResource(R.drawable.check_gray_icon);
                        } else if("redy".equalsIgnoreCase(status)) {
                            binding.readyCheck.setImageResource(R.drawable.res_icon_green);
                            binding.devAcceptCheck.setImageResource(R.drawable.dev_gray_icon);
                            binding.devPivkupCheck.setImageResource(R.drawable.scotor_gray_icon);
                            binding.deliveredCheck.setImageResource(R.drawable.check_gray_icon);
                        } else if("div_accept".equalsIgnoreCase(status)) {
                            binding.readyCheck.setImageResource(R.drawable.res_icon_green);
                            binding.devAcceptCheck.setImageResource(R.drawable.dev_green_icon);
                            binding.devPivkupCheck.setImageResource(R.drawable.scotor_gray_icon);
                            binding.deliveredCheck.setImageResource(R.drawable.check_gray_icon);
                        } else if("intransist".equalsIgnoreCase(status)) {
                            binding.readyCheck.setImageResource(R.drawable.res_icon_green);
                            binding.devAcceptCheck.setImageResource(R.drawable.dev_green_icon);
                            binding.devPivkupCheck.setImageResource(R.drawable.scotor_green_icon);
                            binding.deliveredCheck.setImageResource(R.drawable.check_gray_icon);
                        } else if("delivered".equalsIgnoreCase(status)) {
                            binding.readyCheck.setImageResource(R.drawable.res_icon_green);
                            binding.devAcceptCheck.setImageResource(R.drawable.dev_green_icon);
                            binding.devPivkupCheck.setImageResource(R.drawable.scotor_green_icon);
                            binding.deliveredCheck.setImageResource(R.drawable.green_check);
                            if(data.getFeedback().equals("true")) {
                                binding.btFeedback.setVisibility(View.GONE);
                            } else {
                                binding.btFeedback.setVisibility(View.VISIBLE);
                            }

                        }

                    } else {
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
                // Log.e("anErroranError","anError = " + t.getMessage());
            }

        });

    }

    private void getOrderStatus() {

        Call<ResponseBody> call = ApiFactory.loadInterface().getOrderStatus(modelLogin.getResult().getId(),data.getOrder_id());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swiplayout.setRefreshing(false);
                try {

                    String stringResponse = response.body().string();

                    // Log.e("responseresponse","response = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {

                        JSONObject jsonResult = jsonObject.getJSONObject("result");
                        String status = jsonResult.getString("status");

                        if("pending".equalsIgnoreCase(status)) {
                            binding.readyCheck.setImageResource(R.drawable.res_icon_gray);
                            binding.devAcceptCheck.setImageResource(R.drawable.dev_gray_icon);
                            binding.devPivkupCheck.setImageResource(R.drawable.scotor_gray_icon);
                            binding.deliveredCheck.setImageResource(R.drawable.check_gray_icon);
                        } else if("redy".equalsIgnoreCase(status)) {
                            binding.readyCheck.setImageResource(R.drawable.res_icon_green);
                            binding.devAcceptCheck.setImageResource(R.drawable.dev_gray_icon);
                            binding.devPivkupCheck.setImageResource(R.drawable.scotor_gray_icon);
                            binding.deliveredCheck.setImageResource(R.drawable.check_gray_icon);
                        } else if("div_accept".equalsIgnoreCase(status)) {
                            binding.readyCheck.setImageResource(R.drawable.res_icon_green);
                            binding.devAcceptCheck.setImageResource(R.drawable.dev_green_icon);
                            binding.devPivkupCheck.setImageResource(R.drawable.scotor_gray_icon);
                            binding.deliveredCheck.setImageResource(R.drawable.check_gray_icon);
                        } else if("intransist".equalsIgnoreCase(status)) {
                            binding.readyCheck.setImageResource(R.drawable.res_icon_green);
                            binding.devAcceptCheck.setImageResource(R.drawable.dev_green_icon);
                            binding.devPivkupCheck.setImageResource(R.drawable.scotor_green_icon);
                            binding.deliveredCheck.setImageResource(R.drawable.check_gray_icon);
                        } else if("delivered".equalsIgnoreCase(status)) {
                            binding.readyCheck.setImageResource(R.drawable.res_icon_green);
                            binding.devAcceptCheck.setImageResource(R.drawable.dev_green_icon);
                            binding.devPivkupCheck.setImageResource(R.drawable.scotor_green_icon);
                            binding.deliveredCheck.setImageResource(R.drawable.green_check);
                            if(data.getFeedback().equals("true")) {
                                binding.btFeedback.setVisibility(View.GONE);
                            } else {
                                binding.btFeedback.setVisibility(View.VISIBLE);
                            }

                        }

                    } else {
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
                binding.swiplayout.setRefreshing(false);
                // Log.e("anErroranError","anError = " + t.getMessage());
            }

        });
    }

}