package com.technorizen.omniser.fooddelivery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityFoodItemDetailsBinding;
import com.technorizen.omniser.utils.ProjectUtil;

public class FoodItemDetailsActivity extends AppCompatActivity {

    Context mContext = FoodItemDetailsActivity.this;
    ActivityFoodItemDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_food_item_details);
        ProjectUtil.changeStatusBarColor(FoodItemDetailsActivity.this);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnViewCart.setOnClickListener(v -> {
            startActivity(new Intent(mContext,MyFoodCartActivity.class));
            finish();
        });

    }

}