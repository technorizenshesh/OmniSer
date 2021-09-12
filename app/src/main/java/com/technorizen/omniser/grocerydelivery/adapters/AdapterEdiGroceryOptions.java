package com.technorizen.omniser.grocerydelivery.adapters;

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
import com.technorizen.omniser.fooddelivery.adapters.AdapterCartItems;
import com.technorizen.omniser.fooddelivery.models.ModelCartItems;
import com.technorizen.omniser.utils.AppConstant;

import java.util.ArrayList;

public class AdapterEdiGroceryOptions extends BaseAdapter {

    Context mContext;
    ArrayList<ModelCartItems.Result.Topping> toppingList;
    String itemPrice = "0.0";

    public AdapterEdiGroceryOptions(Context mContext, ArrayList<ModelCartItems.Result.Topping> toppingList,String price) {
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

        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_grocery_options,parent
                ,false);

        ModelCartItems.Result.Topping data = toppingList.get(position);

        RadioButton rbOptions = view.findViewById(R.id.rbOptions);
        TextView tvToppingName = view.findViewById(R.id.tvToppingName);
        TextView tvToppingPrice = view.findViewById(R.id.tvToppingPrice);

        // AdapterGroceryItems.updatePrice(toppingList.getPrice());

        tvToppingName.setText(data.getValue());
        tvToppingPrice.setText(AppConstant.DOLLER_SIGN + data.getPrice());

        if(data.getStatus().equals("true")) {
            rbOptions.setChecked(true);
        } else {
            rbOptions.setChecked(false);
        }

        rbOptions.setOnClickListener(v -> {

            if(toppingList.get(position).getStatus().equals("true")) {
                toppingList.get(position).setStatus("true");
                Log.e("sdfsfsdf","cbTopping true = " + position);
                // notifyDataSetChanged();
            } else {
                toppingList.get(position).setStatus("false");
                Log.e("sdfsfsdf","cbTopping false = " + position);
                // notifyDataSetChanged();
            }

            toppingList.get(position).setStatus("true");

            for (int i=0 ;i<toppingList.size(); i++) {
                if(position != i) {
                    toppingList.get(i).setStatus("false");
                }
            }

            notifyDataSetChanged();

            AdapterGroceryCartItems.updatePrice(toppingList.get(position).getPrice(),itemPrice);

        });

        return view;

    }


}