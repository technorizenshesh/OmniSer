package com.technorizen.omniser.fooddelivery.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.technorizen.omniser.R;
import com.technorizen.omniser.fooddelivery.models.ModelMyOrders;
import com.technorizen.omniser.utils.AppConstant;
import java.util.ArrayList;

public class AdapterCartDetails extends RecyclerView.Adapter<AdapterCartDetails.AdapterCartDetailsViewHolder> {

    Context mContext;
    ArrayList<ModelMyOrders.Result.Order_history> orderDetails;

    public AdapterCartDetails(Context mContext, ArrayList<ModelMyOrders.Result.Order_history> orderDetails) {
        this.mContext = mContext;
        this.orderDetails = orderDetails;
    }

    @NonNull
    @Override
    public AdapterCartDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_food_order_detail,parent,false);
        return new AdapterCartDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCartDetailsViewHolder holder, int position) {

        ModelMyOrders.Result.Order_history data = orderDetails.get(position);

        Log.e("gfsfsdfd","image = " + data.getItem_details().getImage());
        Log.e("gfsfsdfd","price = " + data.getItem_details().getItem_price());
        Log.e("gfsfsdfd","quantity = " + data.getQuantity());

        Glide.with(mContext).load(data.getItem_details().getImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.loading)
                .apply(new RequestOptions().override(300,300))
                .into(holder.ivItemImage);

        holder.tvName.setText(data.getItem_details().getItem_name());
        holder.tvPrice.setText(AppConstant.DOLLER_SIGN + data.getItem_details().getItem_price() +
                " x " + data.getQuantity());

        try {
            if(data.getTopingss() != null && data.getTopingss().size() != 0) {
                boolean isData = false;
                for(int i=0;i<data.getTopingss().size();i++) {
                    if(data.getTopingss().get(i).getStatus().equals("true")) {
                        isData = true;
                        addToppingLayout(holder.llTopping,data.getTopingss().get(i).getValue()
                                ,data.getTopingss().get(i).getPrice());
                    }
                }
                if(isData) {
                    holder.tvToppingTitle.setVisibility(View.VISIBLE);
                    holder.llTopping.setVisibility(View.VISIBLE);
                } else {
                    holder.tvToppingTitle.setVisibility(View.GONE);
                    holder.llTopping.setVisibility(View.GONE);
                }
            } else {
                holder.tvToppingTitle.setVisibility(View.GONE);
                holder.llTopping.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            holder.tvToppingTitle.setVisibility(View.GONE);
            holder.llTopping.setVisibility(View.GONE);
        }

    }


    private void addToppingLayout(LinearLayout linearLayout, String name, String price) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.add_topping_layout,null,false);

        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvPrice = view.findViewById(R.id.tvPrice);

        tvName.setText(name);
        tvPrice.setText(AppConstant.DOLLER_SIGN+price);

        linearLayout.addView(view);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return orderDetails.size();
    }

    public class AdapterCartDetailsViewHolder extends RecyclerView.ViewHolder{

        RoundedImageView ivItemImage;
        TextView tvName,tvPrice,tvToppingTitle;
        LinearLayout llTopping;

        public AdapterCartDetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            ivItemImage  = itemView.findViewById(R.id.ivItemImage);
            tvName  = itemView.findViewById(R.id.tvName);
            tvPrice  = itemView.findViewById(R.id.tvPrice);
            llTopping  = itemView.findViewById(R.id.llTopping);
            tvToppingTitle  = itemView.findViewById(R.id.tvToppingTitle);

        }
    }


}

