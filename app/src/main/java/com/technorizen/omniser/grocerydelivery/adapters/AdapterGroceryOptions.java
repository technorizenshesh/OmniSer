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
import com.technorizen.omniser.fooddelivery.adapters.AdapterResItems;
import com.technorizen.omniser.fooddelivery.adapters.AdapterResItemsSearch;
import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;
import com.technorizen.omniser.utils.AppConstant;

import java.util.ArrayList;

public class AdapterGroceryOptions extends BaseAdapter {

    Context mContext;
    boolean isSearch = false;
    ArrayList<ModelResTypeItems.Result.Item_data.Topping> toppingList;

    public AdapterGroceryOptions(Context mContext, ArrayList<ModelResTypeItems.Result.Item_data.Topping> toppingList,boolean isSearch) {
        this.mContext = mContext;
        this.toppingList = toppingList;
        this.isSearch = isSearch;
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

        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_grocery_options,parent,false);

        ModelResTypeItems.Result.Item_data.Topping data = toppingList.get(position);

        RadioButton rbOptions = view.findViewById(R.id.rbOptions);
        TextView tvToppingName = view.findViewById(R.id.tvToppingName);
        TextView tvToppingPrice = view.findViewById(R.id.tvToppingPrice);

        tvToppingName.setText(data.getValue());
        tvToppingPrice.setText(AppConstant.DOLLER_SIGN + data.getPrice());

        if(toppingList.get(position).isChecked()) {
            rbOptions.setChecked(true);
        } else {
            rbOptions.setChecked(false);
        }

        rbOptions.setOnClickListener(v -> {

            toppingList.get(position).setChecked(true);

            for (int i=0 ;i<toppingList.size(); i++) {
                if(position != i) {
                    toppingList.get(i).setChecked(false);
                }
            }

            notifyDataSetChanged();

            Log.e("toppingListtoppingList","toppingList.get(position).getPrice() = " + toppingList.get(position).getPrice());

            if(isSearch) AdapterResItemsSearch.updatePriceGrocery(toppingList.get(position).getPrice());
            else AdapterGroceryItems.updatePrice(toppingList.get(position).getPrice());

        });

        return view;

    }

}
