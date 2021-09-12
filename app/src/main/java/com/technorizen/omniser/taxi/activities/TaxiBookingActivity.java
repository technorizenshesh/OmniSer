package com.technorizen.omniser.taxi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.skyfishjy.library.RippleBackground;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityTaxiBookingBinding;

public class TaxiBookingActivity extends AppCompatActivity implements OnMapReadyCallback {

    Context mContext = TaxiBookingActivity.this;
    GoogleMap mMap;
    ActivityTaxiBookingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_taxi_booking);

        init();

    }

    private void init() {

        binding.llAddWallet.setOnClickListener(v -> {
            addWalletAmountDialog();
        });

        binding.btBook.setOnClickListener(v -> {
            openDriverSearchDialog();
        });

    }

    private void openDriverSearchDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.search_animation_dialog);
        dialog.setCancelable(true);

        RippleBackground rippleAnim = dialog.findViewById(R.id.rippleAnim);
        ImageView imageView = dialog.findViewById(R.id.centerImage);

        rippleAnim.startRippleAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rippleAnim.stopRippleAnimation();
                dialog.dismiss();
                // startActivity(new Intent(mContext,TaxiTrackingActivity.class));
            }
        },5000);

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        dialog.show();

    }

    private void addWalletAmountDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.wallet_balance_dialog);

        TextView yes = dialog.findViewById(R.id.yes);
        TextView no = dialog.findViewById(R.id.no);

        yes.setOnClickListener(v -> {
            dialog.dismiss();
        });

        no.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        dialog.show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

}