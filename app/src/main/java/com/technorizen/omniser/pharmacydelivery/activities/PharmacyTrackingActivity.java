package com.technorizen.omniser.pharmacydelivery.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityPharmacyTrackingBinding;

public class PharmacyTrackingActivity extends AppCompatActivity  implements OnMapReadyCallback {

    Context mContext = PharmacyTrackingActivity.this;
    ActivityPharmacyTrackingBinding binding;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_pharmacy_tracking);

        init();

    }

    private void init() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.tvOrderDetails.setOnClickListener(v -> {
            orderDetailDailog();
        });

    }

    private void orderDetailDailog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.food_order_summary_dialog);

        ImageView ivBack = dialog.findViewById(R.id.ivBack);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    dialog.dismiss();
                return false;
            }
        });

        ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

}