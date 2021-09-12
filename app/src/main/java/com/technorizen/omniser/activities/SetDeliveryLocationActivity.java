package com.technorizen.omniser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityCommonAddDeliveryAddressBinding;
import com.technorizen.omniser.utils.ProjectUtil;

public class SetDeliveryLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    Context mContext = SetDeliveryLocationActivity.this;
    ActivityCommonAddDeliveryAddressBinding binding;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_common_add_delivery_address);
        ProjectUtil.changeStatusBarColor(SetDeliveryLocationActivity.this);

        init();

    }

    private void init() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.rlContinue.setOnClickListener(v -> {
            startActivity(new Intent(mContext,CommonTimeSlotsActivity.class));
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


}