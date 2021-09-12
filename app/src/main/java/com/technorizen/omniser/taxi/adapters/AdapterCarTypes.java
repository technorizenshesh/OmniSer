package com.technorizen.omniser.taxi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.AdapterCarTypesBinding;
import com.technorizen.omniser.taxi.models.ModelCarType;
import com.technorizen.omniser.utils.AppConstant;

import java.util.ArrayList;

public class AdapterCarTypes extends RecyclerView.Adapter<AdapterCarTypes.CarVieHolder> {

    Context mContext;
    ArrayList<ModelCarType.Result> carList;
    int parentPosition = 0;
    CarSelectorListener listener;

    public AdapterCarTypes(Context mContext, ArrayList<ModelCarType.Result> carList,CarSelectorListener listener) {
        this.mContext = mContext;
        this.carList = carList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterCarTypesBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.adapter_car_types,parent,false);
        return new CarVieHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarVieHolder holder, int position) {

        ModelCarType.Result data = carList.get(position);

        Picasso.get().load(data.getImage())
                .placeholder(R.drawable.loading)
        .error(R.drawable.loading).into(holder.binding.ivVehicleImage);

        holder.binding.tvName.setText(data.getName());
        holder.binding.tvVehiclePrice.setText(AppConstant.DOLLER_SIGN + data.getCharges());

        holder.binding.llSelectCar.setOnClickListener(v -> {
            parentPosition = position;
            listener.onSuccess(data.getId(),data.getEstimated_charge(),data.getDistance());
            notifyDataSetChanged();
        });

        if(position == parentPosition) {
            listener.onSuccess(data.getId(),data.getEstimated_charge(),data.getDistance());
            holder.binding.llSelectCar.setBackgroundResource(R.drawable.green_outline_back);
        } else {
            holder.binding.llSelectCar.setBackground(null);
        }

    }

    public interface CarSelectorListener {
        void onSuccess(String carId, String amount, String distance);
    }

    @Override
    public int getItemCount() {
        return carList == null?0:carList.size();
    }

    public class CarVieHolder extends RecyclerView.ViewHolder {

        AdapterCarTypesBinding binding;

        public CarVieHolder(AdapterCarTypesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
