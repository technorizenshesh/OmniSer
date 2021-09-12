package com.technorizen.omniser.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityMoreBinding;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

public class MyProfileActivity extends AppCompatActivity {

    Context mContext = MyProfileActivity.this;
    ActivityMoreBinding binding;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_more);
        sharedPref = SharedPref.getInstance(mContext);
        ProjectUtil.changeStatusBarColor(MyProfileActivity.this);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.myWallet.setOnClickListener(v -> {
            startActivity(new Intent(mContext,WalletActivity.class));
        });

        binding.myProfile.setOnClickListener(v -> {
            startActivity(new Intent(mContext,UpdateProfileActivity.class));
        });

        binding.cvChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(mContext,ChnagePasswordActivity.class));
        });

        binding.manageReview.setOnClickListener(v -> {
            startActivity(new Intent(mContext,ReviewsActivity.class));
        });

        binding.tvLogout.setOnClickListener(v -> {
            logoutAppDialog();
        });

    }

    private void logoutAppDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPref.clearAllPreferences();
                        Intent loginscreen = new Intent(mContext, LoginActivity.class);
                        loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NotificationManager nManager = ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE));
                        nManager.cancelAll();
                        startActivity(loginscreen);
                        finishAffinity();
                    }
                }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}