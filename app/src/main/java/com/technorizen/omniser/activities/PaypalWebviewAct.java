package com.technorizen.omniser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.ActivityPaypalWebviewBinding;
import com.technorizen.omniser.fooddelivery.activities.FoodHomeActivity;
import com.technorizen.omniser.fooddelivery.activities.FoodPayActivity;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.ondemand.models.ModelInvoiceDetail;
import com.technorizen.omniser.taxi.adapters.AdapterTaxiHistory;
import com.technorizen.omniser.taxi.models.ModelTaxiHistory;
import com.technorizen.omniser.utils.Api;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaypalWebviewAct extends AppCompatActivity {

    Context mContext = PaypalWebviewAct.this;
    ActivityPaypalWebviewBinding binding;
    HashMap<String,String> param = new HashMap<>();
    String type = "",amount = "",id="",taxiPayType="";
    String loadPaymentURL = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ModelTaxiHistory.Result taxiData;
    ModelInvoiceDetail onDemanddata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_paypal_webview);

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        type = getIntent().getStringExtra("type");
        amount = getIntent().getStringExtra("amount");
        id = getIntent().getStringExtra("id");

        loadPaymentURL = "https://myspotbh.com/paypal/products/buy?id="+
                id+"&price="+amount+"&name="+modelLogin.getResult().getName();

        if(type.equals(AppConstant.FOOD)) {
            param = (HashMap<String, String>) getIntent().getSerializableExtra("param");
        } else if(type.equals(AppConstant.GROCERY)) {
            param = (HashMap<String, String>) getIntent().getSerializableExtra("param");
        } else if(type.equals(AppConstant.PHARMACY)) {
            param = (HashMap<String, String>) getIntent().getSerializableExtra("param");
        } else if(type.equals(AppConstant.TAXI)) {
            taxiPayType = getIntent().getStringExtra("payType");
            taxiData = (ModelTaxiHistory.Result) getIntent().getSerializableExtra("data");
        } else if(type.equals(AppConstant.ON_DEMAND)) {
            onDemanddata = (ModelInvoiceDetail) getIntent().getSerializableExtra("data");
        }

        iniit();

    }

    private void iniit() {
        binding.webView.getSettings().setJavaScriptEnabled(true); // enable javascript
        binding.webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(PaypalWebviewAct.this, description, Toast.LENGTH_SHORT).show();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("paypal_web_url", url);
                String title = binding.webView.getTitle();
                Log.d("title", title);
                String completed = "PayPal checkout - Payment complete.";
                if (url.contains("success")) {
                    Toast.makeText(PaypalWebviewAct.this, "Successful", Toast.LENGTH_SHORT).show();
                    if(type.equals(AppConstant.FOOD)) {
                        bookingApi();
                    } else if(type.equals(AppConstant.GROCERY)) {
                        bookingApi();
                    } else if(type.equals(AppConstant.PHARMACY)) {
                        bookingApi();
                    } else if(type.equals(AppConstant.ON_DEMAND)) {
                        bookOnDemandServiceApi();
                    } else if(type.equals(AppConstant.TAXI)) {
                        doPayment();
                    }
                    // bookingApi();
                }
                if (title.contains("cancel")) {
                    Toast.makeText(mContext, "Payment Cancel", Toast.LENGTH_SHORT).show();
                }

            }

        });

        binding.webView.loadUrl(loadPaymentURL);

    }

    private void bookOnDemandServiceApi() {
        ProjectUtil.showProgressDialog(mContext,false,"Please wait...");
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().bookOnDemandServiceApi(
                onDemanddata.getResult().getAccepted_provider_id(),
                onDemanddata.getResult().getId(),
                onDemanddata.getResult().getUser_id(),
                onDemanddata.getResult().getTotal_price(),
                "",
                "online");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("hjdhjkhfjksd","responseString = " + response);

                    if(jsonObject.getString("status").equals("1")) {
                        finishAffinity();
                        mContext.startActivity(new Intent(mContext, DashboardActivity.class));
                        Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
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
                Log.e("onFailure","onFailure = " + t.getMessage());
            }
        });
    }

    private void doPayment() {

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.loadInterface();

        Log.e("sfddsfdsfdsf","user_id = " + modelLogin.getResult().getId());

        HashMap<String,String> params = new HashMap<>();
        params.put("request_id",taxiData.getId());
        params.put("user_id",modelLogin.getResult().getId());
        params.put("total_amount",taxiData.getEstimate_charge_amount());
        params.put("payment_type",taxiPayType);
        params.put("payment_id","");

        Log.e("hjadkjshakjdhkjas","params = " + params);

        Call<ResponseBody> call = api.doTaxiPayment(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                ProjectUtil.pauseProgressDialog();

                Log.e("kghkljsdhkljf","response = " + response);

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("kjagsdkjgaskjd","stringResponse = " + response);
                    Log.e("kjagsdkjgaskjd","stringResponse = " + stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                        finishAffinity();
                        startActivity(new Intent(mContext,DashboardActivity.class));
                        Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("kjagsdkjgaskjd","stringResponse = " + t.getMessage());
            }

        });

    }

    private void bookingApi() {
        ProjectUtil.showProgressDialog(mContext,false,mContext.getString(R.string.please_wait));
        Call<ResponseBody> call = ApiFactory.loadInterface().foodBookingApiNEW(param);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    Log.e("responseresponse","response = " + stringResponse);
                    Log.e("responseresponse","response = " + response);

                    sharedPref.setRestaurantModel(AppConstant.RESTAURANT_DATA,null);
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {
                        //  ModelCartItems modelCartItems = new Gson().fromJson(stringResponse,ModelCartItems.class);
                        Toast.makeText(PaypalWebviewAct.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                        finish();

                        Intent intent = new Intent(mContext, FoodHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        Toast.makeText(PaypalWebviewAct.this, getString(R.string.insuffeient_amount), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    ProjectUtil.pauseProgressDialog();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("anErroranError","anError = " + t.getMessage());
            }

        });

    }

    @Override
    public void onBackPressed() {
        if(binding.webView.canGoBack()) binding.webView.goBack();
        else finish();
    }

}