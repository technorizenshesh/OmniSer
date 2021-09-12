package com.technorizen.omniser.ondemand.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.technorizen.omniser.R;
import com.technorizen.omniser.ondemand.models.ModelInvoiceDetail;
import java.util.ArrayList;

public class AdapterAddItems extends RecyclerView.Adapter<AdapterAddItems.ItemViewHolder> {

    Context mContext;
    ArrayList<ModelInvoiceDetail.Result.Item_array> itemsList;

    public AdapterAddItems(Context mContext, ArrayList<ModelInvoiceDetail.Result.Item_array> itemsList) {
        this.mContext = mContext;
        this.itemsList = itemsList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_used,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ModelInvoiceDetail.Result.Item_array data = itemsList.get(position);

        holder.tvItemName.setText(data.getItem_name());
        holder.tvItemPrice.setText(mContext.getString(R.string.doller) + data.getItem_price());

    }

    @Override
    public int getItemCount() {
        if(itemsList == null) {
            return 0;
        } else {
            return itemsList.size();
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView tvItemName,tvItemPrice;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
        }

    }

}
