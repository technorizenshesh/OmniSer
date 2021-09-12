package com.technorizen.omniser.ondemand.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.technorizen.omniser.R;
import com.technorizen.omniser.activities.MyBookingActivity;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.ondemand.activities.MakeOnDemandRequestActivity;
import com.technorizen.omniser.ondemand.models.ModelAvailProviders;
import com.technorizen.omniser.utils.ApiFactory;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;
import com.technorizen.omniser.utils.SharedPref;
import com.technorizen.omniser.utils.UpdateSubcatInterface;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterAvailableProvider extends RecyclerView.Adapter<AdapterAvailableProvider.MyViewHolder> {

    Context mContext;
    ArrayList<ModelAvailProviders.Result.Provider_details> prvidersList;
    String servicePrice,serviceImg,resquestId,status;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    UpdateSubcatInterface updateSubcatInterface;

    public AdapterAvailableProvider(Context mContext, ArrayList<ModelAvailProviders.Result.Provider_details> prvidersList,String price,String img,String reqId,String status) {
        this.mContext = mContext;
        this.servicePrice = price;
        this.serviceImg = img;
        this.resquestId = reqId;
        this.updateSubcatInterface = (UpdateSubcatInterface) mContext;
        this.status = status;
        this.prvidersList = prvidersList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_avail_providers,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ModelAvailProviders.Result.Provider_details data = prvidersList.get(position);

        if("hire".equals(status)) {
            holder.btConfirm.setVisibility(View.GONE);
            holder.btPayNow.setVisibility(View.VISIBLE);
        } else {
            holder.btConfirm.setVisibility(View.VISIBLE);
            holder.btPayNow.setVisibility(View.GONE);
        }

        Glide.with(mContext)
                .load(serviceImg)
                .placeholder(R.drawable.loading)
                .apply(new RequestOptions().override(300,300))
                .into(holder.ivServiceImage);

        // Picasso.get().load(data.getService_details().getImage()).into(holder.ivServiceImage);
        holder.tvProviderName.setText(data.getName());
        holder.tvProviderAddress.setText(data.getAddress());
        holder.tvProviderPrice.setText("$" + servicePrice);

        holder.btConfirm.setOnClickListener(v -> {
            openConfirmDialog(data.getId(),position);
        });

        holder.btPayNow.setOnClickListener(v -> {
            // openConfirmDialog(data.getId());
        });

    }

    private void openConfirmDialog(String providerId,int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure you want to confirm this provider?")
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmProviderApi(providerId,position);
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

    private void confirmProviderApi(String providerId,int position) {
        ProjectUtil.showProgressDialog(mContext,false,"Please wait...");
        Call<ResponseBody> call = null;

        Log.e("kajsdhshd","providerId = " + providerId);
        Log.e("kajsdhshd","resquestId = " + resquestId);
        Log.e("kajsdhshd","user_id = " + modelLogin.getResult().getId());

        call = ApiFactory.loadInterface().confirmByUser(
                providerId,
                resquestId,
                modelLogin.getResult().getId(),
                "hire");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                          prvidersList.remove(position);
                          notifyDataSetChanged();
                          ((Activity)mContext).finish();
                          // updateSubcatInterface.updateProviders();
                          // Toast.makeText(mContext, mContext.getString(R.string.success), Toast.LENGTH_SHORT).show();
                    } else {
                        //  Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        if(prvidersList == null) {
            return 0;
        } else {
            return prvidersList.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivServiceImage;
        TextView tvProviderName,tvProviderAddress,tvProviderPrice,btConfirm,btPayNow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivServiceImage = itemView.findViewById(R.id.ivServiceImage);
            tvProviderName = itemView.findViewById(R.id.tvProviderName);
            tvProviderAddress = itemView.findViewById(R.id.tvProviderAddress);
            tvProviderPrice = itemView.findViewById(R.id.tvProviderPrice);
            btConfirm = itemView.findViewById(R.id.btConfirm);
            btPayNow = itemView.findViewById(R.id.btPayNow);
        }
    }

}
