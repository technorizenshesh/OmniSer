package com.technorizen.omniser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.technorizen.omniser.R;
import com.technorizen.omniser.commonfragments.ProviderListFragment;
import com.technorizen.omniser.commonfragments.ProviderMapFragment;
import com.technorizen.omniser.databinding.ActivityCommonServiceProBinding;

public class CommonServiceProActivity extends AppCompatActivity {

    Context mContext = CommonServiceProActivity.this;
    ActivityCommonServiceProBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_common_service_pro);

        init();

    }

    private void init() {

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new ProviderListFragment()).commit();

        binding.btList.setOnClickListener(v -> {
            binding.btMap.setBackgroundResource(R.drawable.button_outline_black);
            binding.btList.setBackgroundResource(R.drawable.button_black);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new ProviderListFragment()).commit();
        });

        binding.btMap.setOnClickListener(v -> {
            binding.btList.setBackgroundResource(R.drawable.button_outline_black);
            binding.btMap.setBackgroundResource(R.drawable.button_black);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new ProviderMapFragment()).commit();
        });

        binding.ivFilter.setOnClickListener(v -> {
            providerFilterDialog();
        });


    }

    private void providerFilterDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.provider_filter_dialog);

        ImageView ivBack = dialog.findViewById(R.id.ivBack);
        RelativeLayout rlFilter = dialog.findViewById(R.id.rlFilter);

        ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        rlFilter.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }


}