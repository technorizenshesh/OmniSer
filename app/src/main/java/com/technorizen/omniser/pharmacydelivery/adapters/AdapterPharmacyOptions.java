package com.technorizen.omniser.pharmacydelivery.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.technorizen.omniser.R;
import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;
import com.technorizen.omniser.utils.AppConstant;
import java.util.ArrayList;

public class AdapterPharmacyOptions extends BaseAdapter {

    Context mContext;
    ArrayList<ModelResTypeItems.Result.Item_data.Topping> toppingList;

    public AdapterPharmacyOptions(Context mContext, ArrayList<ModelResTypeItems.Result.Item_data.Topping> toppingList) {
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
            AdapterPharmacyItems.updatePrice(toppingList.get(position).getPrice());

        });

        return view;

    }

}
