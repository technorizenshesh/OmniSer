package com.technorizen.omniser.fooddelivery.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.technorizen.omniser.R;
import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;
import com.technorizen.omniser.utils.AppConstant;
import java.util.ArrayList;

public class AdapterFoodTopping extends BaseAdapter {

    Context mContext;
    ArrayList<ModelResTypeItems.Result.Item_data.Topping> toppingList;
    String itemPrice = "0.0";

    public AdapterFoodTopping(Context mContext, ArrayList<ModelResTypeItems.Result.Item_data.Topping> toppingList,String price) {
        this.mContext = mContext;
        this.toppingList = toppingList;
        this.itemPrice = price;
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

        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_food_topping,parent,false);

        ModelResTypeItems.Result.Item_data.Topping data = toppingList.get(position);

        CheckBox cbTopping = view.findViewById(R.id.cbTopping);
        TextView tvToppingName = view.findViewById(R.id.tvToppingName);
        TextView tvToppingPrice = view.findViewById(R.id.tvToppingPrice);

        tvToppingName.setText(data.getValue());
        tvToppingPrice.setText(AppConstant.DOLLER_SIGN + data.getPrice());

        cbTopping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("sdfsfsdf","Topping");

                int toppingTotal = 0;

                if(isChecked) {
                    toppingList.get(position).setChecked(true);
                    Log.e("sdfsfsdf","cbTopping true = " + position);
                    // notifyDataSetChanged();
                } else {
                    toppingList.get(position).setChecked(false);
                    Log.e("sdfsfsdf","cbTopping false = " + position);
                    // notifyDataSetChanged();
                }

                Log.e("sdfsfsdf","toppingList.size() = " + toppingList.size());
                for(int i=0;i<toppingList.size();i++) {
                    Log.e("sdfsfsdf","inside For = " + i);
                    Log.e("sdfsfsdf","sdfsfsdf isChecked = " + toppingList.get(i).isChecked());
                    if(toppingList.get(i).isChecked()) {
                        toppingTotal = toppingTotal + Integer.parseInt(toppingList.get(i).getPrice());
                    }
                }

                AdapterResItems.updatePrice(String.valueOf(toppingTotal),itemPrice);

            }
        });

        return view;

    }


}
