package com.technorizen.omniser.ondemand.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.technorizen.omniser.R;
import com.technorizen.omniser.activities.CommonServiceProActivity;
import com.technorizen.omniser.adapters.AdapterCategoryAds;
import com.technorizen.omniser.databinding.ActivityCarwashCategoriesBinding;

import java.util.ArrayList;

public class CarWashinCatActivity extends AppCompatActivity {

    Context mContext = CarWashinCatActivity.this;
    ActivityCarwashCategoriesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_carwash_categories);

        init();

    }

    private void init() {

        binding.rlNext.setOnClickListener(v -> {
            startActivity(new Intent(mContext, CommonServiceProActivity.class));
        });

        ArrayList<Integer> adsList = new ArrayList<>();
        adsList.add(R.drawable.mobile_home_slider_main_1);
        adsList.add(R.drawable.mobile_home_slider_main_2);

        binding.categorAdsSlider.setSliderAdapter(new AdapterCategoryAds(mContext,adsList));
        binding.categorAdsSlider.startAutoCycle();
        binding.categorAdsSlider.setIndicatorAnimation(IndicatorAnimationType.SLIDE);
        binding.categorAdsSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.categorAdsSlider.setScrollTimeInSec(5);
        binding.categorAdsSlider.setAutoCycle(true);
        binding.categorAdsSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);

    }


}