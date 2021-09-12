package com.technorizen.omniser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityCommonCartBinding;
import com.technorizen.omniser.utils.ProjectUtil;

public class CommonCartActivity extends AppCompatActivity {

    Context mContext = CommonCartActivity.this;
    ActivityCommonCartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_common_cart);
        ProjectUtil.changeStatusBarColor(CommonCartActivity.this);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.rlAddItem.setOnClickListener(v -> {
            finish();
            // startActivity(new Intent(mContext , SetDeliveryLocationActivity.class));
        });

    }


}