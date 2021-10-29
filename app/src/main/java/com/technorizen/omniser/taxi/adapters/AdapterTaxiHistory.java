package com.technorizen.omniser.taxi.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.AdapterTaxiBookingBinding;
import com.technorizen.omniser.databinding.AddFeedbackDialogBinding;
import com.technorizen.omniser.databinding.TaxiPaymentHistoryDialogBinding;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.taxi.activities.TrackTaxiAct;
import com.technorizen.omniser.taxi.models.ModelTaxiHistory;
import com.technorizen.omniser.utils.Api;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterTaxiHistory extends RecyclerView.Adapter<AdapterTaxiHistory.TaxiHolder> {

    Context mContext;
    ArrayList<ModelTaxiHistory.Result> taxiList;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    PayNowInterface payNowInterface;
    String payMethod = null;

    public AdapterTaxiHistory(Context mContext, ArrayList<ModelTaxiHistory.Result> taxiList, PayNowInterface payNowInterface) {
        this.mContext = mContext;
        this.taxiList = taxiList;
        this.payNowInterface = payNowInterface;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public TaxiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterTaxiBookingBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.adapter_taxi_booking, parent, false);
        return new TaxiHolder(binding);
    }

    public interface PayNowInterface {
        void onSuccess(ModelTaxiHistory.Result data, String payMethod, int position);
    }

    @Override
    public void onBindViewHolder(@NonNull TaxiHolder holder, int position) {

        ModelTaxiHistory.Result data = taxiList.get(position);

        Log.e("sdfdsfsfsfsf", "data = " + position + data.getPicklaterdate());
        Log.e("sdfdsfsfsfsf", "data = " + position + data.getPicuplocation());
        Log.e("sdfdsfsfsfsf", "data = " + position + data.getDropofflocation());
        Log.e("sdfdsfsfsfsf", "data = " + position + data.getStatus());

        holder.binding.tvDateTime.setText(data.getPicklaterdate());
        holder.binding.tvFrom.setText(data.getPicuplocation());
        holder.binding.tvDestination.setText(data.getDropofflocation());

        if ("es".equals(sharedPref.getLanguage("lan"))) {
            if (data.getStatus().equalsIgnoreCase("Pending")) {
                holder.binding.tvStatus.setText("Pendiente");
            } else if ("complete".equalsIgnoreCase(data.getStatus())) {
                holder.binding.tvStatus.setText("Completo");
            } else if ("unpaid".equalsIgnoreCase(data.getStatus())) {
                holder.binding.tvStatus.setText("No pagado");
            } else if ("paid".equalsIgnoreCase(data.getStatus())) {
                holder.binding.tvStatus.setText("Pagado");
            } else if ("Cancel".equalsIgnoreCase(data.getStatus())) {
                holder.binding.btPayNow.setVisibility(View.GONE);
                holder.binding.tvStatus.setText("Cancelada");
            }
        } else {
            if ("Cancel".equalsIgnoreCase(data.getStatus())) {
                holder.binding.tvStatus.setText("Cancelled");
                holder.binding.btPayNow.setVisibility(View.GONE);
            } else {
                holder.binding.tvStatus.setText(data.getStatus());
            }
        }

        if (data.getStatus().equalsIgnoreCase("Pending")) {
            holder.binding.GoDetail.setVisibility(View.GONE);
        } else {
            holder.binding.GoDetail.setVisibility(View.VISIBLE);
        }

        if (data.getStatus().equals("Paid")) {
            if ("Yes".equals(data.getUser_rating_status())) {
                holder.binding.btGiveFeedback.setVisibility(View.GONE);
            } else if ("No".equals(data.getUser_rating_status())) {
                holder.binding.btGiveFeedback.setVisibility(View.VISIBLE);
            } else {
                holder.binding.btGiveFeedback.setVisibility(View.VISIBLE);
            }
            holder.binding.btPayNow.setVisibility(View.GONE);
        } else {
            if ("Cancel".equalsIgnoreCase(data.getStatus())) {
                holder.binding.btPayNow.setVisibility(View.GONE);
            } else {
                holder.binding.btPayNow.setVisibility(View.VISIBLE);
            }
            holder.binding.btGiveFeedback.setVisibility(View.GONE);
        }

        if (data.getStatus().equals("Cancel_by_user")) {
            holder.binding.btPayNow.setVisibility(View.GONE);
            holder.binding.btGiveFeedback.setVisibility(View.GONE);
            holder.binding.tvStatus.setText(data.getStatus());
            if ("es".equals(sharedPref.getLanguage("lan"))) {
                holder.binding.tvStatus.setText("Cancelar por usuario");
            } else {
                holder.binding.tvStatus.setText("Cancel By User");
            }
        } else if (data.getStatus().equals("Cancel_by_driver")) {
            holder.binding.btPayNow.setVisibility(View.GONE);
            holder.binding.btGiveFeedback.setVisibility(View.GONE);
            if ("es".equals(sharedPref.getLanguage("lan"))) {
                holder.binding.tvStatus.setText("Cancelar por conductor");
            } else {
                holder.binding.tvStatus.setText("Cancel By Driver");
            }
        }

        holder.binding.btGiveFeedback.setOnClickListener(v -> {
            addFeedbackDialog(data);
        });

        holder.binding.btPayNow.setOnClickListener(v -> {
            openPaymentSummaryDialog(data, position);
        });

        holder.binding.GoDetail.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, TrackTaxiAct.class)
                    .putExtra("request_id", data.getId())
            );
        });

    }

    private void addFeedbackDialog(ModelTaxiHistory.Result data) {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        AddFeedbackDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.add_feedback_dialog, null, false);

        dialog.setContentView(dialogBinding.getRoot());

        ProjectUtil.changeDialogStatusBar(dialog, mContext);

        dialogBinding.tvDrivername.setText(data.getDriver_name());
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    dialog.dismiss();
                return false;
            }
        });

        try {
            Picasso.get().load(data.getDriver_image())
                    .error(R.drawable.default_profile_icon)
                    .placeholder(R.drawable.default_profile_icon)
                    .into(dialogBinding.driveUserProfile);
        } catch (Exception e) {
        }

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btSubmit.setOnClickListener(v -> {
            addFeedbackApi(data, dialog, String.valueOf(dialogBinding.ratingBar.getRating())
                    , dialogBinding.tvComment.getText().toString().trim());
        });

        dialog.show();

    }

    private void addFeedbackApi(ModelTaxiHistory.Result data, Dialog dialog, String rating, String comment) {
        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
        Api api = ApiFactory.loadInterface();

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", modelLogin.getResult().getId());
        params.put("rating", rating);
        params.put("request_id", data.getId());
        params.put("review", comment);

        Call<ResponseBody> call = api.addUserTaxiFeedbackApi(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        data.setUser_rating_status("Yes");
                        notifyDataSetChanged();
                        dialog.dismiss();
                        // Log.e("responseStringdfsdfsd","responseString = " + responseString);

                    } else {
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

    private void openPaymentSummaryDialog(ModelTaxiHistory.Result data, int position) {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        TaxiPaymentHistoryDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.taxi_payment_history_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.setPayment(data);
        dialogBinding.tvEstiPrice.setText(AppConstant.DOLLER_SIGN + data.getEstimate_charge_amount());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btPayNow.setOnClickListener(v -> {
            dialog.dismiss();
            if (dialogBinding.rbCash.isChecked()) {
                payMethod = "cash";
                payNowInterface.onSuccess(data, payMethod, position);
            } else if (dialogBinding.rbOnline.isChecked()) {
                payMethod = "online";
                payNowInterface.onSuccess(data, payMethod, position);
            } else if (dialogBinding.rbWallet.isChecked()) {
                payMethod = "wallet";
                payNowInterface.onSuccess(data, payMethod, position);
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.please_select_pay_method), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return taxiList == null ? 0 : taxiList.size();
    }

    public class TaxiHolder extends RecyclerView.ViewHolder {

        AdapterTaxiBookingBinding binding;

        public TaxiHolder(@NonNull AdapterTaxiBookingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
