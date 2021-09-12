package com.technorizen.omniser.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.technorizen.omniser.R;
import com.technorizen.omniser.ondemand.activities.CarWashinCatActivity;
import com.technorizen.omniser.ondemand.activities.MakeOnDemandRequestActivity;
import com.technorizen.omniser.ondemandmodels.ModelOnDemand;
import java.util.ArrayList;

public class AdapterOnDemand extends RecyclerView.Adapter<AdapterOnDemand.onDemandViewHolder> {

    Context mContext;
    ArrayList<ModelOnDemand.Result> ondemandList;

    public AdapterOnDemand(Context mContext, ArrayList<ModelOnDemand.Result> ondemandList) {
        this.mContext = mContext;
        this.ondemandList = ondemandList;
    }

    @NonNull
    @Override
    public onDemandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_ondemand,parent,false);
        return new onDemandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull onDemandViewHolder holder, int position) {

        ModelOnDemand.Result data = ondemandList.get(position);

        Picasso.get().load(data.getImage()).into(holder.ivOndemImg);
        holder.tvOndem.setText(data.getSub_cat_name());

        holder.itemView.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, MakeOnDemandRequestActivity.class)
            .putExtra("service_id",data.getId())
            );
        });

    }

    @Override
    public int getItemCount() {
        return ondemandList.size();
    }

    public class onDemandViewHolder extends RecyclerView.ViewHolder{

        ImageView ivOndemImg;
        TextView tvOndem;

        public onDemandViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOndemImg = itemView.findViewById(R.id.ivOndemImg);
            tvOndem = itemView.findViewById(R.id.tvOndem);
        }

    }


}
