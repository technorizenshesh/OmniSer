package com.technorizen.omniser.fooddelivery.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.technorizen.omniser.R;
import com.technorizen.omniser.fooddelivery.activities.RestaurantDetailsActivity;
import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.SharedPref;

import java.util.ArrayList;

public class AdapterResCategoryList extends RecyclerView.Adapter<AdapterResCategoryList.MyViewHolder> {

    Context mContext;
    ArrayList<ModelResTypeItems.Result> resitemslist;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    int row_index = 0;

    public AdapterResCategoryList(Context mContext, ArrayList<ModelResTypeItems.Result> reslist) {
        this.mContext = mContext;
        this.resitemslist = reslist;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_cat_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ModelResTypeItems.Result data = resitemslist.get(position);

        holder.tvCatName.setText(data.getCategory_name());

        holder.tvCatName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index=position;
                ((RestaurantDetailsActivity)mContext).scrollItemToPosition(position);
                notifyDataSetChanged();
            }
        });

        if(row_index == position) {
            holder.tvCatName.setBackgroundResource(R.drawable.button_green_back_20);
            holder.tvCatName.setTextColor(Color.WHITE);
        } else {
            holder.tvCatName.setBackgroundDrawable(null);
            holder.tvCatName.setTextColor(Color.BLACK);
        }

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
        if(resitemslist == null || resitemslist.size() == 0) {
            return 0;
        } else {
            return resitemslist.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvCatName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCatName = itemView.findViewById(R.id.tvCatName);
        }


    }



}
