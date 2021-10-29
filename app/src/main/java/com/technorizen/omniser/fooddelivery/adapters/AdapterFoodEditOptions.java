package com.technorizen.omniser.fooddelivery.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.technorizen.omniser.R;
import com.technorizen.omniser.fooddelivery.models.ModelCartItems;
import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;
import com.technorizen.omniser.utils.AppConstant;
import java.util.ArrayList;

public class AdapterFoodEditOptions extends BaseAdapter {

    Context mContext;
    int prePosition = 0;
    ArrayList<ModelCartItems.Result.ExtraOptions> toppingList;
    boolean isFirstTimeOption = false;

    public AdapterFoodEditOptions(Context mContext,ArrayList<ModelCartItems.Result.ExtraOptions> toppingList) {
        this.mContext = mContext;
        this.toppingList = toppingList;
    }

    @Override
    public int getCount() {
        if(toppingList == null) {
            return 0;
        } else {
            return toppingList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return toppingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_food_options,parent,false);

        ModelCartItems.Result.ExtraOptions data = toppingList.get(position);

        RadioButton rbOption = view.findViewById(R.id.rbOption);
        TextView tvOptionName = view.findViewById(R.id.tvOptionName);
        TextView tvOptionPrice = view.findViewById(R.id.tvOptionPrice);

        tvOptionName.setText(data.getOption_name());
        tvOptionPrice.setText(AppConstant.DOLLER_SIGN + data.getOption_price());

        if(data.getStatus().equals("true")) {
            AdapterCartItems.optionId = data.getId();
            AdapterCartItems.isFirstTime = true;
            AdapterCartItems.optionPrice = data.getOption_price();
            prePosition = position;
            rbOption.setChecked(true);
        } else {
            rbOption.setChecked(false);
        }

        rbOption.setOnClickListener(v -> {
            // AdapterResItems.optionPrice = data.getOption_price();
            Log.e("optionPrice","AdapterResItems.optionPrice = " + AdapterResItems.optionPrice);
            AdapterCartItems.optionId = data.getId();
            AdapterCartItems.isFirstTime = true;
            Log.e("AdapterCartItems","AdapterCartItems.optionPrice out = " + AdapterCartItems.optionPrice);
            AdapterCartItems.updateOption(prePosition,position,data.getOption_price());
            prePosition = position;
        });

        return view;

    }


}
