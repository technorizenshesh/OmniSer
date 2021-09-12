package com.technorizen.omniser.pharmacydelivery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityPharmancyItemDetailBinding;
import com.technorizen.omniser.grocerydelivery.activities.MyGroceryCartActivity;

public class PharmacyItemDetailActivity extends AppCompatActivity {

    Context mContext = PharmacyItemDetailActivity.this;
    ActivityPharmancyItemDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_pharmancy_item_detail);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnViewCart.setOnClickListener(v -> {
            startActivity(new Intent(mContext, PharmacyCartActivity.class));
            finish();
        });

    }


}