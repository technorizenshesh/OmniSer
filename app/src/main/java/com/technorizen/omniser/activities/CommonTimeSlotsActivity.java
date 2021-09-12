package com.technorizen.omniser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityCommonTimeSlotsBinding;

public class CommonTimeSlotsActivity extends AppCompatActivity {

    Context mContext = CommonTimeSlotsActivity.this;
    ActivityCommonTimeSlotsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_common_time_slots);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.rlDone.setOnClickListener(v -> {
            startActivity(new Intent(mContext,CommonPaymentActivity.class));
        });

    }


}