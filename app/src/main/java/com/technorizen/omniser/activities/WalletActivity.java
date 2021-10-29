package com.technorizen.omniser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityWalletBinding;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.ondemand.adapters.AdapterTransactions;
import com.technorizen.omniser.ondemand.models.ModelTransactions;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletActivity extends AppCompatActivity {

    Context mContext = WalletActivity.this;
    ActivityWalletBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    double walletTmpAmt = 0.0;
    String type = "";
    String walletFinalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_wallet);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        ProjectUtil.changeStatusBarColor(WalletActivity.this);
        init();
    }

    private void init() {

        getWalletAmount();
        getAllTransactions();

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWalletAmount();
                getAllTransactions();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.cvAddMoney.setOnClickListener(v -> {
            addMoneyDialog();
        });

        binding.cvTransfer.setOnClickListener(v -> {
            tranferMOneyDialog();
        });

    }

    private void addWalletAmountApiCall(Dialog dialog,String amt) {

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().addWalletAmount(
                modelLogin.getResult().getId(),amt);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();

                Log.e("kljsdhfkhdklsf","response = " + response);

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        dialog.dismiss();
                        String walletString = String.format("%.2f",Double.parseDouble(jsonObject.getString("result")));

                        walletFinalAmount = walletString;

                        binding.tvWalletAmount.setText("$" + walletFinalAmount);

                        Toast.makeText(WalletActivity.this, "Success", Toast.LENGTH_SHORT).show();

                    } else {

                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                // getServiceTypes();
                Log.e("onFailure","onFailure = " + t.getMessage());
            }
        });

    }

    private void getAllTransactions() {

        ProjectUtil.showProgressDialog(mContext,true,"Please wait...");
        Call<ResponseBody> call = ApiFactory.loadInterface().getAllTransactions(modelLogin.getResult().getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);

                Log.e("kljsdhfkhdklsf","response = " + response);

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelTransactions modelTransactions = new Gson().fromJson(responseString,ModelTransactions.class);

                        AdapterTransactions adapterTransactions = new AdapterTransactions(mContext,modelTransactions.getResult());
                        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvTransactions.setAdapter(adapterTransactions);

                        // Toast.makeText(WalletActivity.this, "Success", Toast.LENGTH_SHORT).show();

                    } else {

                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                // getServiceTypes();
                Log.e("onFailure","onFailure = " + t.getMessage());
            }
        });
    }

    private void sendMoneyAPiCall(Dialog dialog,String email,String amt) {

        ProjectUtil.showProgressDialog(mContext,false,"Please wait...");
        Call<ResponseBody> call = ApiFactory.loadInterface().sendMoneyApi(modelLogin.getResult().getId()
                ,email,amt);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();

                Log.e("kljsdhfkhdklsf","response = " + response);

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        dialog.dismiss();

                        String walletString = String.format("%.2f",Double.parseDouble(jsonObject.getString("remaining_amount")));

                        walletFinalAmount = walletString;

                        binding.tvWalletAmount.setText("$" + walletFinalAmount);

                        getAllTransactions();

                        Toast.makeText(WalletActivity.this, "Success", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(WalletActivity.this, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                // getServiceTypes();
                Log.e("onFailure","onFailure = " + t.getMessage());
            }
        });

    }

    private void getWalletAmount() {

        // ProjectUtil.showProgressDialog(mContext,false,"Please wait...");
        Call<ResponseBody> call = ApiFactory.loadInterface().getWalletAmount(
                modelLogin.getResult().getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                Log.e("kljsdhfkhdklsf","response = " + response);

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        JSONObject jsonResult = jsonObject.getJSONObject("result");

                        binding.tvWalletAmount.setText("$" + jsonResult.getString("wallet_amount"));

                        String walletString = String.format("%.2f",Double.parseDouble(jsonResult.getString("wallet_amount")));

                        walletFinalAmount = walletString;

                        // Toast.makeText(WalletActivity.this, "Success", Toast.LENGTH_SHORT).show();

                    } else {

                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                // getServiceTypes();
                Log.e("onFailure","onFailure = " + t.getMessage());
            }
        });

    }

    private void addMoneyDialog() {

        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_money_dialog);

        ProjectUtil.changeDialogStatusBar(dialog,mContext);

        ImageView ivMinus = dialog.findViewById(R.id.ivMinus);
        EditText etMoney = dialog.findViewById(R.id.etMoney);
        ImageView ivPlus = dialog.findViewById(R.id.ivPlus);
        TextView tv699 = dialog.findViewById(R.id.tv699);
        TextView tv799 = dialog.findViewById(R.id.tv799);
        TextView tv899 = dialog.findViewById(R.id.tv899);
        Button btDone = dialog.findViewById(R.id.btDone);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    dialog.dismiss();
                return false;
            }
        });

        ivMinus.setOnClickListener(v -> {
            if(!(etMoney.getText().toString().trim().equals("") || etMoney.getText().toString().trim().equals("0"))) {
                walletTmpAmt = Double.parseDouble(etMoney.getText().toString().trim()) - 1;
                etMoney.setText(String.valueOf(walletTmpAmt));
            }
        });

        ivPlus.setOnClickListener(v -> {
            if(TextUtils.isEmpty(etMoney.getText().toString().trim())) {
                etMoney.setText("0");
                walletTmpAmt = Double.parseDouble(etMoney.getText().toString().trim()) + 1;
                etMoney.setText(String.valueOf(walletTmpAmt));
            } else {
                walletTmpAmt = Double.parseDouble(etMoney.getText().toString().trim()) + 1;
                etMoney.setText(String.valueOf(walletTmpAmt));
            }
        });

        tv699.setOnClickListener(v -> {
            etMoney.setText("699");
            walletTmpAmt = Double.parseDouble(etMoney.getText().toString().trim());
            etMoney.setText(String.valueOf(walletTmpAmt));
        });

        tv799.setOnClickListener(v -> {
            etMoney.setText("799");
            walletTmpAmt = Double.parseDouble(etMoney.getText().toString().trim());
            etMoney.setText(String.valueOf(walletTmpAmt));
        });

        tv899.setOnClickListener(v -> {
            etMoney.setText("899");
            walletTmpAmt = Double.parseDouble(etMoney.getText().toString().trim());
            etMoney.setText(String.valueOf(walletTmpAmt));
        });

        btDone.setOnClickListener(v -> {
            if(TextUtils.isEmpty(etMoney.getText().toString().trim())) {
                Toast.makeText(mContext, "Please enter amount", Toast.LENGTH_SHORT).show();
            } else if(walletTmpAmt == 0.0) {
                Toast.makeText(mContext, "Please enter valid amount", Toast.LENGTH_SHORT).show();
            } else {
                // dialog.dismiss();
                Log.e("walletAmount","amount = " + walletTmpAmt);
                startActivity(new Intent(mContext, PaypalWebviewAct.class)
                        .putExtra("type",AppConstant.WALLET)
                        .putExtra("amount",walletTmpAmt+"")
                        .putExtra("id",modelLogin.getResult().getId())
                );
                finish();
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();

    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void tranferMOneyDialog() {

        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.send_money_dialog);

        RadioButton rbUser = dialog.findViewById(R.id.rbUser);
        RadioButton rbProvider = dialog.findViewById(R.id.rbProvider);
        EditText etEmail = dialog.findViewById(R.id.etEmail);
        EditText etEnterAmount = dialog.findViewById(R.id.etEnterAmount);
        Button btDone = dialog.findViewById(R.id.btDone);

        btDone.setOnClickListener(v -> {

            if(TextUtils.isEmpty(etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, "Please enter email address", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(etEnterAmount.getText().toString().trim())) {
                Toast.makeText(mContext, "Please enter amount", Toast.LENGTH_SHORT).show();
            } else if(!isValidEmail(etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, "Invalid Email", Toast.LENGTH_SHORT).show();
            } else {
                sendMoneyAPiCall(dialog,etEmail.getText().toString().trim()
                        ,etEnterAmount.getText().toString().trim());
            }

        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();
    }

}