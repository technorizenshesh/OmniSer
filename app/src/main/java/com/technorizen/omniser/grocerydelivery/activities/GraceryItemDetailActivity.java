package com.technorizen.omniser.grocerydelivery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityGraceryItemDetailBinding;
import com.technorizen.omniser.fooddelivery.activities.MyFoodCartActivity;
import com.technorizen.omniser.fooddelivery.activities.TrackFoodOrderActivity;
import com.technorizen.omniser.utils.ProjectUtil;

public class GraceryItemDetailActivity extends AppCompatActivity {

    Context mContext = GraceryItemDetailActivity.this;
    ActivityGraceryItemDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_gracery_item_detail);
        ProjectUtil.changeStatusBarColor(GraceryItemDetailActivity.this);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnViewCart.setOnClickListener(v -> {
            startActivity(new Intent(mContext, MyGroceryCartActivity.class));
            finish();
        });

    }


}