package com.technorizen.omniser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.content.Context;
import android.os.Bundle;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityTransactionBinding;
import com.technorizen.omniser.utils.ProjectUtil;

public class TransactionActivity extends AppCompatActivity {

    Context mContext = TransactionActivity.this;
    ActivityTransactionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_transaction);
        ProjectUtil.changeStatusBarColor(TransactionActivity.this);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btAll.setOnClickListener(v -> {
            binding.btAll.setBackgroundResource(R.drawable.button_black);
            binding.btMoneyIn.setBackgroundResource(R.drawable.button_outline_black);
            binding.btMoneyOut.setBackgroundResource(R.drawable.button_outline_black);
        });

        binding.btMoneyIn.setOnClickListener(v -> {
            binding.btMoneyIn.setBackgroundResource(R.drawable.button_black);
            binding.btAll.setBackgroundResource(R.drawable.button_outline_black);
            binding.btMoneyOut.setBackgroundResource(R.drawable.button_outline_black);
        });

        binding.btMoneyOut.setOnClickListener(v -> {
            binding.btMoneyOut.setBackgroundResource(R.drawable.button_black);
            binding.btMoneyIn.setBackgroundResource(R.drawable.button_outline_black);
            binding.btAll.setBackgroundResource(R.drawable.button_outline_black);
        });

    }


}