package com.technorizen.omniser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityCommonBookingDetailsBinding;
import com.technorizen.omniser.utils.ProjectUtil;

public class CommonBookingDetailsActivity extends AppCompatActivity {

    Context mContext = CommonBookingDetailsActivity.this;
    ActivityCommonBookingDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_common_booking_details);
        ProjectUtil.changeStatusBarColor(CommonBookingDetailsActivity.this);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.rlBook.setOnClickListener(v -> {
            startActivity(new Intent(mContext,SetDeliveryLocationActivity.class));
        });

    }


}