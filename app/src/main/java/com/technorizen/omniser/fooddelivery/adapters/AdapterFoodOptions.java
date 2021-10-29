package com.technorizen.omniser.fooddelivery.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import com.technorizen.omniser.R;
import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;
import com.technorizen.omniser.utils.AppConstant;
import java.util.ArrayList;

public class AdapterFoodOptions extends BaseAdapter {

    Context mContext;
    int prePosition = 0;
    ArrayList<ModelResTypeItems.Result.Item_data.ExtraOptions> toppingList;
    boolean isFirstTimeOption = false;

    public AdapterFoodOptions(Context mContext,ArrayList<ModelResTypeItems.Result.Item_data.ExtraOptions> toppingList) {
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

        ModelResTypeItems.Result.Item_data.ExtraOptions data = toppingList.get(position);

        RadioButton rbOption = view.findViewById(R.id.rbOption);
        TextView tvOptionName = view.findViewById(R.id.tvOptionName);
        TextView tvOptionPrice = view.findViewById(R.id.tvOptionPrice);

        tvOptionName.setText(data.getOption_name());
        tvOptionPrice.setText(AppConstant.DOLLER_SIGN + data.getOption_price());

        rbOption.setOnClickListener(v -> {
            // AdapterResItems.optionPrice = data.getOption_price();
            Log.e("optionPrice","AdapterResItems.optionPrice = " + AdapterResItems.optionPrice);
            AdapterResItems.optionId = data.getId();
            AdapterResItems.isFirstTime = true;
            AdapterResItems.updateOption(prePosition,position,data.getOption_price());
            prePosition = position;
        });

        return view;

    }


}
