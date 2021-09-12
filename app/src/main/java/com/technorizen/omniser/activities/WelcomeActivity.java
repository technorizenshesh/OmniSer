package com.technorizen.omniser.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityWelcomeBinding;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

public class WelcomeActivity extends AppCompatActivity {

    Context mContext = WelcomeActivity.this;
    ActivityWelcomeBinding binding;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        ProjectUtil.changeStatusBarColor(WelcomeActivity.this);
        sharedPref = SharedPref.getInstance(mContext);
        init();
    }

    private void init() {

        if("en".equals(sharedPref.getLanguage("lan"))) {
            binding.spLang.setSelection(1);
            sharedPref.setlanguage("lan","en");
        } else if("es".equals(sharedPref.getLanguage("lan"))) {
            binding.spLang.setSelection(2);
            sharedPref.setlanguage("lan","es");
        } else {
            binding.spLang.setSelection(0);
            sharedPref.setlanguage("lan","en");
        }

        binding.spLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    if (!"en".equals(sharedPref.getLanguage("lan"))) {
                        ProjectUtil.updateResources(mContext, "en");
                        sharedPref.setlanguage("lan", "en");
                        finish();
                        startActivity(new Intent(mContext, WelcomeActivity.class));
                    }
                } else if (position == 2) {
                    if (!"es".equals(sharedPref.getLanguage("lan"))) {
                        ProjectUtil.updateResources(mContext, "es");
                        sharedPref.setlanguage("lan", "es");
                        finish();
                        startActivity(new Intent(mContext, WelcomeActivity.class));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });

        binding.rlLogin.setOnClickListener(v -> {
            if("".equals(sharedPref.getLanguage("lan"))) {
                sharedPref.setlanguage("lan","en");
            }
            startActivity(new Intent(mContext, LoginActivity.class));
            finish();
        });

        binding.rlRegister.setOnClickListener(v -> {
            if("".equals(sharedPref.getLanguage("lan"))) {
                sharedPref.setlanguage("lan","en");
            }
            startActivity(new Intent(mContext, RegisterActivity.class));
            finish();
        });

    }

    @Override
    public void onBackPressed() {
        exitAppDialog();
    }

    private void exitAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure you want to close the App?")
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
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