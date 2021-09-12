package com.technorizen.omniser.ondemand.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.technorizen.omniser.R;
import com.technorizen.omniser.models.ModelLogin;
import com.technorizen.omniser.ondemand.models.ModelTransactions;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.SharedPref;

import java.util.ArrayList;

public class AdapterTransactions extends RecyclerView.Adapter<AdapterTransactions.onDemandViewHolder> {

    Context mContext;
    ArrayList<ModelTransactions.Result> transList;
    ModelLogin modelLogin;
    SharedPref sharedPref;

    public AdapterTransactions(Context mContext, ArrayList<ModelTransactions.Result> ondemandList) {
        this.mContext = mContext;
        this.transList = ondemandList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public onDemandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_transaction,parent,false);
        return new onDemandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull onDemandViewHolder holder, int position) {

        ModelTransactions.Result data = transList.get(position);

        holder.tvAmount.setText("$ " + data.getAmount());
        if(data.getSender_id().equals(modelLogin.getResult().getId())) {
            holder.tvDebitOrCredit.setText("Debit");
            holder.ivSendRecive.setImageResource(R.drawable.send_money);
        } else {
            holder.tvDebitOrCredit.setText("Credit");
            holder.ivSendRecive.setImageResource(R.drawable.receive_money);
        }

    }

    @Override
    public int getItemCount() {
        if(transList == null) {
            return 0;
        } else {
            return transList.size();
        }
    }

    public class onDemandViewHolder extends RecyclerView.ViewHolder{

        ImageView ivSendRecive;
        TextView tvDebitOrCredit,tvAmount;

        public onDemandViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSendRecive = itemView.findViewById(R.id.ivSendRecive);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDebitOrCredit = itemView.findViewById(R.id.tvDebitOrCredit);
        }

    }


}