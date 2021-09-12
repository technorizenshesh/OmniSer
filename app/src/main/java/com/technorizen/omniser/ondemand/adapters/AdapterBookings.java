package com.technorizen.omniser.ondemand.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.braintreepayments.cardform.view.CardForm;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.technorizen.omniser.R;
import com.technorizen.omniser.activities.DashboardActivity;
import com.technorizen.omniser.activities.MyBookingActivity;
import com.technorizen.omniser.activities.PaypalWebviewAct;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.ondemand.activities.MakeOnDemandRequestActivity;
import com.technorizen.omniser.ondemand.activities.ViewRequestDetailsActivity;
import com.technorizen.omniser.ondemand.models.ModelBooking;
import com.technorizen.omniser.ondemand.models.ModelInvoiceDetail;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterBookings extends RecyclerView.Adapter<AdapterBookings.BookingAdapaterViewHolder> {

    Context mContext;
    ArrayList<ModelBooking.Result> bookinglist;
    RecyclerView recyclerView;
    ModelLogin modelLogin;
    SharedPref sharedPref;

    public AdapterBookings(Context mContext, ArrayList<ModelBooking.Result> bookinglist) {
        this.mContext = mContext;
        this.bookinglist = bookinglist;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public BookingAdapaterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_booking,parent,false);
        return new BookingAdapaterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingAdapaterViewHolder holder, int position) {

        ModelBooking.Result data = bookinglist.get(position);

        Picasso.get().load(data.getService_details().getImage()).into(holder.ivServiceImg);
        holder.rquestId.setText("Request Id : " + data.getOrder_id());
        holder.tvAddress.setText(data.getDelivery_address());
        holder.tvTime.setText(data.getService_time());
        holder.tvServiceName.setText(data.getService_details().getSub_cat_name());

        if("pending".equals(data.getStatus())) {
            holder.tvRequestStatus.setText("Pending");
        } else if("hire".equals(data.getStatus())) {
            holder.tvRequestStatus.setText("Hired!");
        } else if("start".equals(data.getStatus())) {
            holder.tvRequestStatus.setText("Started");
        } else if("complete".equals(data.getStatus())) {
            holder.tvRequestStatus.setText("Completed");
        } else if("unpaid".equals(data.getStatus())) {
            holder.tvViewDetails.setText(mContext.getString(R.string.see_invoice));
            holder.tvRequestStatus.setText("Unpaid");
        } else if("paid".equals(data.getStatus())) {
            holder.tvRequestStatus.setText("Paid");
        }

        holder.tvViewDetails.setOnClickListener(v -> {
            if("pending".equals(data.getStatus())) {
                mContext.startActivity(new Intent(mContext, ViewRequestDetailsActivity.class)
                        .putExtra("data",new Gson().toJson(data))
                );
            } else if("unpaid".equals(data.getStatus())) {
                getInvoiceDetails(data.getAccepted_provider_id(),data.getId(),position);
            }
        });

    }

    private void getInvoiceDetails(String providerId,String requestId,int position) {
        ProjectUtil.showProgressDialog(mContext,false,"Please wait...");
        Call<ResponseBody> call = ApiFactory.loadInterface().getInvoiceDetails(
                providerId
                ,requestId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("gdfsfsfss","response = " + response);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelInvoiceDetail modelInvoiceDetail = new Gson().fromJson(responseString,ModelInvoiceDetail.class);
                        // bookinglist.remove(position);
                        // notifyDataSetChanged();
                        showInvoiceDialog(modelInvoiceDetail,requestId);

                        Toast.makeText(mContext, mContext.getString(R.string.success), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void showInvoiceDialog(ModelInvoiceDetail modelInvoiceDetail, String requestId) {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.request_payment_dialog);

        TextView tvRequestAddress = dialog.findViewById(R.id.tvRequestAddress);
        TextView tvRequestDesp = dialog.findViewById(R.id.tvRequestDesp);
        TextView tvHoursWorked = dialog.findViewById(R.id.tvHoursWorked);
        TextView tvRatePerHours = dialog.findViewById(R.id.tvRatePerHours);
        TextView tvTotalWorkedPrice = dialog.findViewById(R.id.tvTotalWorkedPrice);
        TextView tvItemTotalPrice = dialog.findViewById(R.id.tvItemTotalPrice);
        TextView tvTotalPrice = dialog.findViewById(R.id.tvTotalPrice);
        TextView itemsUsed = dialog.findViewById(R.id.itemsUsed);
        Button btPayNow = dialog.findViewById(R.id.btPayNow);
        RecyclerView recyclerView = dialog.findViewById(R.id.rvItemUsed);
        RadioButton cbCardPayment = dialog.findViewById(R.id.cbCardPayment);
        RadioButton cbWallet = dialog.findViewById(R.id.cbWallet);
       // RadioButton cbCash = dialog.findViewById(R.id.cbCash);

//        Log.e("kjasgdfkjaskf","model_items = " +
//                new Gson().toJson(modelInvoiceDetail.getResult().getItem_arrays()));

        if(modelInvoiceDetail.getResult().getItem_arrays() == null ||
                modelInvoiceDetail.getResult().getItem_arrays().size() == 0) {
            itemsUsed.setText(mContext.getString(R.string.no_item_used));
        }

        AdapterAddItems adapterAddItems = new AdapterAddItems(mContext,modelInvoiceDetail.getResult().getItem_arrays());
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapterAddItems);

        tvRequestAddress.setText(modelInvoiceDetail.getResult().getDelivery_address());
        tvRequestDesp.setText(modelInvoiceDetail.getResult().getDescription());
        tvHoursWorked.setText(modelInvoiceDetail.getResult().getWorked_time());

        if(modelInvoiceDetail.getResult().getItem_total_price() == null ||
                modelInvoiceDetail.getResult().getItem_total_price().equals("")) {
            tvItemTotalPrice.setText(mContext.getString(R.string.doller) + "0");
        } else {
            tvItemTotalPrice.setText(mContext.getString(R.string.doller) + modelInvoiceDetail.getResult().getItem_total_price());
        }

        tvRatePerHours.setText(mContext.getString(R.string.doller) + modelInvoiceDetail.getResult().getService_price());
        tvTotalWorkedPrice.setText(mContext.getString(R.string.doller) + modelInvoiceDetail.getResult().getWorked_price());
        tvTotalPrice.setText(mContext.getString(R.string.doller) + modelInvoiceDetail.getResult().getTotal_price());

        btPayNow.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, PaypalWebviewAct.class)
                    .putExtra("type",AppConstant.ON_DEMAND)
                    .putExtra("amount",modelInvoiceDetail.getResult().getTotal_price())
                    .putExtra("id",modelLogin.getResult().getId())
                    .putExtra("data",modelInvoiceDetail)
            );
              bookOnDemandServiceApi(modelInvoiceDetail,requestId,"online");
//            if(cbCardPayment.isChecked()) {
//                // openPaymentDialog(modelInvoiceDetail,requestId);
//            } else if(cbWallet.isChecked()) {
//                bookOnDemandServiceApi(modelInvoiceDetail,requestId,"wallet");
//            } /*else if(cbCash.isChecked()) {
//                bookOnDemandServiceApi(modelInvoiceDetail,requestId,"cash");
//            }*/ else {
//                Toast.makeText(mContext, mContext.getString(R.string.please_chosse_payment_method), Toast.LENGTH_SHORT).show();
//            }
            // generateInvoiceApi(modelInvoiceDetail,requestId,dialog);
        });

        dialog.show();

    }

    private void openPaymentDialog(ModelInvoiceDetail modelInvoiceDetail, String requestId) {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.ondemand_payment_dialog);

        CardForm card_form = dialog.findViewById(R.id.card_form);
        Button btPayNow = dialog.findViewById(R.id.btPayNow);

        card_form.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .mobileNumberExplanation("SMS is required on this number")
                .setup(((AppCompatActivity)mContext));

        card_form.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        btPayNow.setOnClickListener(v -> {
            if(card_form.isValid()) {
                Card.Builder card = new Card.Builder(card_form.getCardNumber(),
                        Integer.valueOf(card_form.getExpirationMonth()),
                        Integer.valueOf(card_form.getExpirationYear()),
                        card_form.getCvv());

            //                  Stripe stripe = new Stripe(mContext, "pk_test_bSLVSktslgDu3X7dnk45qd7T003tWG0Rqz");
//                      stripe.createCardToken(
//                        card.build(), new ApiResultCallback<Token>() {
//                            @Override
//                            public void onSuccess(Token token) {
//                                ProjectUtil.pauseProgressDialog();
//
////                              startActivity(new Intent(mContext, DashboardActivity.class));
////                              finish();
//
//                                Log.e("tokentoken", "Payment Success = " + token);
//                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
//                                // charge(token);
//                            }
//
//                            @Override
//                            public void onError(@NotNull Exception e) {
//                                ProjectUtil.pauseProgressDialog();
//                                Log.e("tokentoken", "Exception = " + e.getMessage() + "");
//                            }

//                        });

            } else {}

        });

        dialog.show();

    }

    private void bookOnDemandServiceApi(ModelInvoiceDetail modelInvoiceDetail,String requestId,String paymentStatus) {
        ProjectUtil.showProgressDialog(mContext,false,"Please wait...");
        Call<ResponseBody> call = null;

        call = ApiFactory.loadInterface().bookOnDemandServiceApi(
                modelInvoiceDetail.getResult().getAccepted_provider_id(),
                requestId,
                modelInvoiceDetail.getResult().getUser_id(),
                modelInvoiceDetail.getResult().getTotal_price(),
                "",
                paymentStatus);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("hjdhjkhfjksd","responseString = " + response);

                    if(jsonObject.getString("status").equals("1")) {

                        /* finish();
                           startActivity(new Intent(mContext, MyBookingActivity.class)); */
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

    @Override
    public int getItemCount() {
        return bookinglist.size();
    }

    public class BookingAdapaterViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivServiceImg;
        TextView rquestId,tvAddress,tvTime,tvServiceName,tvViewDetails,tvRequestStatus;

        public BookingAdapaterViewHolder(@NonNull View itemView) {
            super(itemView);
            ivServiceImg = itemView.findViewById(R.id.ivServiceImg);
            rquestId = itemView.findViewById(R.id.rquestId);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvViewDetails = itemView.findViewById(R.id.tvViewDetails);
            tvRequestStatus = itemView.findViewById(R.id.tvRequestStatus);
        }

    }


}
