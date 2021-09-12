package com.technorizen.omniser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityReviewsBinding;
import com.technorizen.omniser.utils.ProjectUtil;

public class ReviewsActivity extends AppCompatActivity {

    Context mContext = ReviewsActivity.this;
    ActivityReviewsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_reviews);
        ProjectUtil.changeStatusBarColor(ReviewsActivity.this);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }


}