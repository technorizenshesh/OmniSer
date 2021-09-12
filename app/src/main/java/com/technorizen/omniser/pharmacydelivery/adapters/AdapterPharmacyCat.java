package com.technorizen.omniser.pharmacydelivery.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.technorizen.omniser.R;
import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.SharedPref;
import java.util.ArrayList;

public class AdapterPharmacyCat extends RecyclerView.Adapter<AdapterPharmacyCat.MyViewHolder> {

    Context mContext;
    ArrayList<ModelResTypeItems.Result> resitemslist;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    public AdapterPharmacyCat(Context mContext, ArrayList<ModelResTypeItems.Result> reslist) {
        this.mContext = mContext;
        this.resitemslist = reslist;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        Log.e("resitemslist","resitemslist = " + resitemslist.size());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_res_items_with_cat, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelResTypeItems.Result data = resitemslist.get(position);

        holder.tvCatName.setText(data.getCategory_name());

        Log.e("resitemslist","getItem_data = " + data.getItem_data().size());

        AdapterPharmacyItems adapterResItems = new AdapterPharmacyItems(mContext,data.getItem_data(),data.getId(),position);
        holder.rvItems.setLayoutManager(new LinearLayoutManager(mContext));
        holder.rvItems.setAdapter(adapterResItems);

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
        RecyclerView rvItems;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCatName = itemView.findViewById(R.id.tvCatName);
            rvItems = itemView.findViewById(R.id.rvItems);
        }


    }

}
