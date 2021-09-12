package com.technorizen.omniser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityCommonPaymentBinding;

public class CommonPaymentActivity extends AppCompatActivity {

    Context mContext = CommonPaymentActivity.this;
    ActivityCommonPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_common_payment);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.rlDone.setOnClickListener(v -> {
            startActivity(new Intent(mContext,DashboardActivity.class));
            finish();
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


}