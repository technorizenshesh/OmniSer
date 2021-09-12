package com.technorizen.omniser.fooddelivery.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.technorizen.omniser.R;
import com.technorizen.omniser.fooddelivery.activities.RestaurantDetailsActivity;
import com.technorizen.omniser.fooddelivery.models.ModelAllRes;
import com.technorizen.omniser.fooddelivery.models.ModelTopRatedRes;
import com.technorizen.omniser.utils.AppConstant;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdapterAllRes extends RecyclerView.Adapter<AdapterAllRes.MyViewHolder> {

    Context mContext;
    ArrayList<ModelAllRes.Result> reslist;

    public AdapterAllRes(Context mContext, ArrayList<ModelAllRes.Result> reslist) {
        this.mContext = mContext;
        this.reslist = reslist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_all_restaurants,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelAllRes.Result data = reslist.get(position);

        Log.e("sdfsdf","url = " + position + "   " + data.getImage());

        Glide.with(mContext).load(data.getImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.loading)
                .apply(new RequestOptions()
                .override(500,500))
                .into(holder.ivResImage);

        holder.resName.setText(data.getName());
        holder.resdesp.setText(data.getDescription());
        holder.tvAddress.setText(data.getAddress());
        holder.resName.setText(data.getName());

        if("true".equals(data.getSetLike())) holder.ivLike.setVisibility(View.VISIBLE);
        else holder.ivLike.setVisibility(View.GONE);

        if(data.getAccept_order_or_not().equals("true")) {
            holder.vOnOff.setBackgroundResource(R.drawable.green_oval);
        } else {
            holder.vOnOff.setBackgroundResource(R.drawable.gray_outline_oval);
        }

        Log.e("gasgkasasd","Res Name = " + data.getName());
        Log.e("gasgkasasd","Distance = " + data.getDistance() + AppConstant.KM);
        Log.e("gasgkasasd","Time = " + data.getEstimate_time());

        holder.tvDistance.setText(data.getDistance() + AppConstant.KM);
        holder.tvTime.setText(data.getEstimate_time());

        holder.itemView.setOnClickListener(v -> {

            if(data.getAccept_order_or_not().equals("true")) {
                mContext.startActivity(new Intent(mContext,RestaurantDetailsActivity.class)
                        .putExtra("resid",data.getId())
                );
            } else {
                notAcceptOrders();
            }

        });

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

        RoundedImageView ivResImage;
        TextView resName,resdesp,tvRating,tvAddress,tvDistance,tvTime;
        RatingBar rbRating;
        View vOnOff;
        ImageView ivLike;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivResImage = itemView.findViewById(R.id.ivResImage);
            resName = itemView.findViewById(R.id.resName);
            resdesp = itemView.findViewById(R.id.resdesp);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvTime = itemView.findViewById(R.id.tvTime);
            vOnOff = itemView.findViewById(R.id.vOnOff);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            rbRating = itemView.findViewById(R.id.rbRating);
            ivLike = itemView.findViewById(R.id.ivLike);
        }


    }

}

