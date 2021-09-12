package com.technorizen.omniser.fooddelivery.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.StoreCategoryAdapterBinding;
import com.technorizen.omniser.ondemandmodels.ModelOnDemand;

import java.util.ArrayList;

public class AdapterHomeFoodCat extends RecyclerView.Adapter<AdapterHomeFoodCat.FoodCatViewHolder> {

    Context mContext;
    ArrayList<ModelOnDemand.Result> catList;
    UpdateResListener updateRes;
    int rowPosition = 0;

    public AdapterHomeFoodCat(Context mContext, ArrayList<ModelOnDemand.Result> catList,UpdateResListener updateRes) {
        this.mContext = mContext;
        this.catList = catList;
        this.updateRes = updateRes;
    }

    @NonNull
    @Override
    public FoodCatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StoreCategoryAdapterBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.store_category_adapter,parent,false);
        return new FoodCatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodCatViewHolder holder, int position) {

        ModelOnDemand.Result data = catList.get(position);

        holder.binding.tvName.setText(data.getSub_cat_name());
        Picasso.get().load(data.getImage())
                .placeholder(R.drawable.loading)
                .into(holder.binding.catImg);

        holder.binding.catImg.setOnClickListener(v -> {
            rowPosition = position;
            updateRes.onSucces(data.getId());
            notifyDataSetChanged();
        });

        if(rowPosition == position)
            holder.binding.vView.setVisibility(View.VISIBLE);
        else {
            holder.binding.vView.setVisibility(View.GONE);
        }

    }

    public interface UpdateResListener {
        void onSucces(String id);
    }

    @Override
    public int getItemCount() {
        return catList == null?0:catList.size();
    }

    class FoodCatViewHolder extends RecyclerView.ViewHolder {

        StoreCategoryAdapterBinding binding;

        public FoodCatViewHolder(@NonNull StoreCategoryAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
