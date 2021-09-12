package com.technorizen.omniser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.technorizen.omniser.R;
import com.technorizen.omniser.commonfragments.ProviderGalleryFragment;
import com.technorizen.omniser.commonfragments.ProviderReviewsFragment;
import com.technorizen.omniser.commonfragments.ProviderServiesFragment;
import com.technorizen.omniser.databinding.ActivityCommonProviderDetailBinding;

public class CommonProviderDetailActivity extends AppCompatActivity {

    Context mContext = CommonProviderDetailActivity.this;
    ActivityCommonProviderDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_common_provider_detail);

        init();

    }

    private void init() {

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new ProviderServiesFragment()).commit();

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.llCheckout.setOnClickListener(v -> {
            startActivity(new Intent(mContext,CommonBookingDetailsActivity.class));
        });

        binding.btService.setOnClickListener(v -> {

            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new ProviderServiesFragment()).commit();

            binding.btService.setBackgroundResource(R.drawable.button_black);

            binding.btGallery.setBackgroundResource(R.drawable.button_outline_black);

            binding.btReview.setBackgroundResource(R.drawable.button_outline_black);

        });

        binding.btGallery.setOnClickListener(v -> {

            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new ProviderGalleryFragment()).commit();

            binding.btGallery.setBackgroundResource(R.drawable.button_black);

            binding.btService.setBackgroundResource(R.drawable.button_outline_black);

            binding.btReview.setBackgroundResource(R.drawable.button_outline_black);

        });

        binding.btReview.setOnClickListener(v -> {

            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new ProviderReviewsFragment()).commit();

            binding.btReview.setBackgroundResource(R.drawable.button_black);

            binding.btGallery.setBackgroundResource(R.drawable.button_outline_black);

            binding.btService.setBackgroundResource(R.drawable.button_outline_black);

        });

    }


}