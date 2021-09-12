package com.technorizen.omniser.fooddelivery.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.technorizen.omniser.R;
import com.technorizen.omniser.fooddelivery.activities.FoodOrderStatusActivity;
import com.technorizen.omniser.fooddelivery.activities.TrackFoodOrderActivity;
import com.technorizen.omniser.fooddelivery.models.ModelMyOrders;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.ProjectUtil;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMyOrders extends RecyclerView.Adapter<AdapterMyOrders.AdapterOrderHistoryViewHolder> {

    Context mContext;
    ArrayList<ModelMyOrders.Result> orderList;
    boolean isHistory;
    String storeType = "";

    public AdapterMyOrders(Context mContext, ArrayList<ModelMyOrders.Result> orderList,boolean isHistory,String storeType) {
        this.mContext = mContext;
        this.orderList = orderList;
        this.isHistory = isHistory;
        this.storeType = storeType;
    }

    @NonNull
    @Override
    public AdapterOrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_my_orders,parent,false);
        return new AdapterOrderHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOrderHistoryViewHolder holder, int position) {

        ModelMyOrders.Result data = orderList.get(position);

        holder.tvOrderId.setText(mContext.getString(R.string.order_id) + data.getOrder_id());
        holder.tvOrderAmt.setText(AppConstant.DOLLER_SIGN + data.getTotal_amount());
        holder.tvPaymentMethod.setText(data.getPayment_type().toUpperCase());
        Picasso.get().load(data.getStore_image()).placeholder(R.drawable.loading).into(holder.ivStoreImage);
        holder.tvStoreName.setText(data.getStore_name());
        holder.tvTime.setText(data.getOrder_place_time());

        holder.tvDate.setText(ProjectUtil.getDay(data.getDelivery_date()));
        holder.tvStoreAddress.setText(data.getStore_address() + " " + data.getLand_mark_add());

        if(data.getDriver_details() == null) {
            holder.btTrackOrder.setVisibility(View.GONE);
        } else {
            if(data.getStatus().equals("delivered")) {
                holder.btTrackOrder.setVisibility(View.GONE);
            } else if(data.getStatus().equals("cancel")) {
                holder.btCheckStatus.setText(R.string.order_reject);
                holder.btTrackOrder.setVisibility(View.GONE);
            } else {
                holder.btTrackOrder.setVisibility(View.VISIBLE);
            }
        }

        holder.btTrackOrder.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, TrackFoodOrderActivity.class)
            .putExtra("data",data)
            );
        });

        holder.btCheckDetails.setOnClickListener(v -> {
            openOrderDetailDialog(data,orderList.get(position).getOrder_history());
        });

        if(isHistory) {
            holder.btCheckStatus.setVisibility(View.GONE);
        } else {
            holder.btCheckStatus.setVisibility(View.VISIBLE);
        }

        holder.btCheckStatus.setOnClickListener(v -> {
            if(!data.getStatus().equals("cancel")) {
                mContext.startActivity(new Intent(mContext, FoodOrderStatusActivity.class)
                        .putExtra("data",data)
                        .putExtra(AppConstant.STORE_TYPE,storeType)
                );
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.order_reject), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openOrderDetailDialog(ModelMyOrders.Result data,ArrayList<ModelMyOrders.Result.Order_history> orderDetails) {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.orders_food_detail_dialog);

        ProjectUtil.changeDialogStatusBar(dialog,mContext);

        ImageView ivBack = dialog.findViewById(R.id.ivBack);
        TextView tvOrderAmt = dialog.findViewById(R.id.tvOrderAmt);
        TextView tvDate = dialog.findViewById(R.id.tvDate);
        TextView tvDevAdd = dialog.findViewById(R.id.tvDevAdd);
        TextView tvStoreName = dialog.findViewById(R.id.tvStoreName);
        TextView tvStoreAddress = dialog.findViewById(R.id.tvStoreAddress);
        CircleImageView ivStoreImage = dialog.findViewById(R.id.ivStoreImage);
        TextView tvPayMethod = dialog.findViewById(R.id.tvPayMethod);
        RecyclerView rvOrderDetails = dialog.findViewById(R.id.rvOrderDetails);

        tvStoreAddress.setText(data.getStore_address() + " " + data.getLand_mark_add());
        tvOrderAmt.setText(AppConstant.DOLLER_SIGN + data.getTotal_amount());
        tvDate.setText(data.getDelivery_date());
        tvStoreName.setText(data.getStore_name());

        Picasso.get().load(data.getStore_image())
                .placeholder(R.drawable.loading)
                .into(ivStoreImage);

        tvPayMethod.setText(data.getPayment_type());
        tvDevAdd.setText(data.getDelivery_address());

        ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        rvOrderDetails.setLayoutManager(new LinearLayoutManager(mContext));
        AdapterCartDetails adapterCartDetails = new AdapterCartDetails(mContext,orderDetails);
        rvOrderDetails.setAdapter(adapterCartDetails);

        dialog.show();

    }

    @Override
    public int getItemCount() {
        if(orderList != null) {
            return orderList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class AdapterOrderHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate,tvTime,tvOrderId,tvOrderAmt,tvPaymentMethod,tvDeliveryCharges,tvStoreName,tvStoreAddress;
        Button btCheckDetails,btCheckStatus,btTrackOrder;
        CircleImageView ivStoreImage;

        public AdapterOrderHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderAmt = itemView.findViewById(R.id.tvOrderAmt);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvDeliveryCharges = itemView.findViewById(R.id.tvDeliveryCharges);
            btCheckDetails = itemView.findViewById(R.id.btCheckDetails);
            btCheckStatus = itemView.findViewById(R.id.btCheckStatus);
            btTrackOrder = itemView.findViewById(R.id.btTrackOrder);
            ivStoreImage = itemView.findViewById(R.id.ivStoreImage);
            tvStoreName = itemView.findViewById(R.id.tvStoreName);
            tvStoreAddress = itemView.findViewById(R.id.tvStoreAddress);
        }
    }


}

