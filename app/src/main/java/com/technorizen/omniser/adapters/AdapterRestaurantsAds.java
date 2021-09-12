package com.technorizen.omniser.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makeramen.roundedimageview.RoundedImageView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.technorizen.omniser.R;

import java.util.ArrayList;

public class AdapterRestaurantsAds extends SliderViewAdapter<AdapterRestaurantsAds.AdapterRecentAdsViewHolder> {

    Context mContext;
    ArrayList<Integer> adsList;

    public AdapterRestaurantsAds(Context mContext, ArrayList<Integer> adsList) {
        this.mContext = mContext;
        this.adsList = adsList;
    }

    public void renewItems(ArrayList<Integer> adsList) {
        this.adsList = adsList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.adsList.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Integer sliderItem) {
        this.adsList.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public AdapterRecentAdsViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_recents_add,parent,false);
        return new AdapterRecentAdsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterRecentAdsViewHolder holder, int position) {

        holder.itemsImage.setImageResource(adsList.get(position));

    }

    @Override
    public int getCount() {
        return adsList.size();
    }

    public class AdapterRecentAdsViewHolder extends SliderViewAdapter.ViewHolder{

        RoundedImageView itemsImage;

        public AdapterRecentAdsViewHolder(View itemView) {
            super(itemView);
            itemsImage = itemView.findViewById(R.id.itemsImage);
        }

    }


}

