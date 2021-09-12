package com.technorizen.omniser.grocerydelivery.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.technorizen.omniser.R;
import com.technorizen.omniser.fooddelivery.activities.RestaurantDetailsActivity;
import com.technorizen.omniser.fooddelivery.adapters.AdapterHomeRes;
import com.technorizen.omniser.fooddelivery.models.ModelTopRatedRes;
import com.technorizen.omniser.grocerydelivery.activities.GraceryShopDetailActivity;

import java.util.ArrayList;

public class AdapterHomeGrocery extends RecyclerView.Adapter<AdapterHomeGrocery.MyViewHolder> {

    Context mContext;
    ArrayList<ModelTopRatedRes.Result> reslist;

    public AdapterHomeGrocery(Context mContext, ArrayList<ModelTopRatedRes.Result> reslist) {
        this.mContext = mContext;
        this.reslist = reslist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_6_restaurants,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelTopRatedRes.Result data = reslist.get(position);

        Glide.with(mContext)
                .load(data.getImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.loading)
                /* .apply(new RequestOptions().override(500,500)) */
                .into(holder.resImage);

        holder.resName.setText(data.getName());
        holder.resdesp.setText(data.getDescription());
        holder.tvAddress.setText(data.getAddress());
        holder.resName.setText(data.getName());

        if(data.getSetLike().equals("true")) {
            holder.ivLike.setVisibility(View.VISIBLE);
        } else {
            holder.ivLike.setVisibility(View.GONE);
        }

        if(data.getAccept_order_or_not().equals("true")) {
            holder.vOnOff.setBackgroundResource(R.drawable.green_oval);
        } else {
            holder.vOnOff.setBackgroundResource(R.drawable.gray_outline_oval);
        }

        holder.itemView.setOnClickListener(v -> {
            if(data.getAccept_order_or_not().equals("true")) {
                mContext.startActivity(new Intent(mContext, GraceryShopDetailActivity.class)
                        .putExtra("resid",data.getId())
                );
            } else {
                notAcceptOrders();
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void notAcceptOrders() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.not_accept_order_text));
        builder.setCancelable(true);
        builder.setPositiveButton(mContext.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return reslist==null?0:reslist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivLike;
        RoundedImageView resImage;
        TextView resName,resdesp,tvRating,tvAddress;
        RatingBar rbRating;
        View vOnOff;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            resImage = itemView.findViewById(R.id.resImage);
            resName = itemView.findViewById(R.id.resName);
            resdesp = itemView.findViewById(R.id.resdesp);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            vOnOff = itemView.findViewById(R.id.vOnOff);
            rbRating = itemView.findViewById(R.id.rbRating);
            ivLike = itemView.findViewById(R.id.ivLike);
        }

    }

}
